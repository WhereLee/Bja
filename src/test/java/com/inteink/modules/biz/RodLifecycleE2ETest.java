package com.inteink.modules.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.modules.biz.mapper.BizLiftingRodLogMapper;
import com.inteink.modules.biz.mapper.BizLiftingRodMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.entity.BizLiftingRodLog;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;
import com.inteink.modules.biz.model.form.StrategyForm;
import com.inteink.modules.biz.service.ConverterService;
import com.inteink.modules.biz.service.LiftingRodService;
import com.inteink.modules.biz.service.LiftingStrategyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 端到端集成测试：真实 MySQL(testcontainers) 下跑完整业务链路——
 * 建杆 → 绑转换器 → 策略审核(生成定时任务) → 下发(Mock 网关成功) → 校验库内 rod_state 与 @BizLog 落的 rod_log。
 * 无 Docker 环境自动跳过（disabledWithoutDocker），不阻断本地/CI 构建。
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class RodLifecycleE2ETest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("inteink_it")
            .withUsername("it")
            .withPassword("it")
            .withInitScript("db/inteink-faster.sql");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.druid.url", mysql::getJdbcUrl);
        r.add("spring.datasource.druid.username", mysql::getUsername);
        r.add("spring.datasource.druid.password", mysql::getPassword);
        r.add("spring.redis.host", redis::getHost);
        r.add("spring.redis.port", () -> redis.getMappedPort(6379));
        r.add("biz.device.mode", () -> "mock");
        r.add("biz.reconcile.enabled", () -> "false");
    }

    @Autowired
    private LiftingRodService liftingRodService;
    @Autowired
    private ConverterService converterService;
    @Autowired
    private LiftingStrategyService strategyService;
    @Autowired
    private BizLiftingRodMapper rodMapper;
    @Autowired
    private BizLiftingRodLogMapper rodLogMapper;

    @Test
    void 建杆_绑定_审核_下发_留痕_全链路() {
        BizLiftingRod rod = new BizLiftingRod();
        rod.setRodName("it-rod-1");
        rod.setRodAddr("addr");
        Long rodId = liftingRodService.saveRod(rod, 1L);

        BizConverter conv = new BizConverter();
        conv.setConverterSn("IT0001");
        conv.setConverterIp("127.0.0.1");
        conv.setConverterPort(1);
        Long convId = converterService.saveConverter(conv, 1L);
        converterService.bind(convId, rodId);

        StrategyForm form = new StrategyForm();
        form.setStrategyName("it-strategy");
        form.setStrategyAction(1);
        form.setStrategyType(1); // 每日
        form.setDetailBegin("03:00");
        form.setDetailEnd("04:00");
        form.setRodIds(List.of(rodId));
        Long sid = strategyService.saveStrategy(form, 1L);
        strategyService.audit(sid, true, "E2E 通过", 1L); // 通过 → 生成成对定时任务

        // 下发（Mock 网关恒成功）：应更新 rod_state 并经 @BizLog 落一条 AUTO rod_log
        liftingRodService.operateRod(rodId, 1, RodLogTypeEnum.AUTO, sid);

        assertEquals(1, rodMapper.selectById(rodId).getRodState());
        List<BizLiftingRodLog> logs = rodLogMapper.selectList(new LambdaQueryWrapper<BizLiftingRodLog>()
                .eq(BizLiftingRodLog::getRodId, rodId));
        assertFalse(logs.isEmpty());
        assertEquals(1, logs.get(0).getLogResult()); // 成功
    }
}

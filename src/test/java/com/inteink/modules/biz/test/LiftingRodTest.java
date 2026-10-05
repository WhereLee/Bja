package com.inteink.modules.biz.test; // 你的测试类路径

import com.inteink.InteinkFaster;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import org.junit.Test; // JUnit4的Test注解（不是jupiter的）
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

// JUnit4核心注解（和你可运行的测试类一致）
@RunWith(SpringRunner.class)
// 指定启动类（解决路径问题，类名必须和实际启动类一致）
@SpringBootTest(classes = InteinkFaster.class)
public class LiftingRodTest {

    @Resource
    private LiftingRodService liftingRodService;

    // JUnit4的@Test注解（不是org.junit.jupiter.api.Test）
    @Test
    public void testLiftUp() {
        try {
            Long rodId = 2L; // 数据库中存在的升降杆ID
            Integer action = 1; // 升
            Long operator = 1001L;
            liftingRodService.liftRod(rodId, action, operator);
            System.out.println("升杆测试执行成功！");
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常，方便定位问题
        }
    }

    @Test
    public void testLiftDown() {
        try {
            Long rodId = 2L;
            Integer action = 2; // 降
            Long operator = 1001L;
            liftingRodService.liftRod(rodId, action, operator);
            System.out.println("降杆测试执行成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
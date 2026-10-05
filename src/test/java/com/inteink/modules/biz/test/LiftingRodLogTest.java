package com.inteink.modules.biz.test;

import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.model.entity.BizLiftingRodLog;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.service.lifting.LiftingRodLogService;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

/**
 * 升降杆操作日志测试类
 * 测试场景：日志插入、分页查询、条件查询、无数据查询
 *
 * @author 实习开发
 * @date 2025-11-28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.inteink.InteinkFaster.class) // 替换为你的启动类
public class LiftingRodLogTest {

    @Autowired
    private LiftingRodService liftingRodService; // 用于生成测试日志（通过升降操作）

    @Autowired
    private LiftingRodLogService liftingRodLogService; // 日志查询Service

    // 测试用例：执行升杆操作，验证日志是否插入成功
    @Test
    public void testLogInsert() {
        System.out.println("=== 测试：执行升杆操作，验证日志插入 ===");
        Long rodId = 1L; // 已存在的升降杆ID（数据库中需有该杆）
        Integer action = 1; // 1-升
        Long operatorId = 1001L; // 测试操作人ID

        // 1. 执行升杆操作（会自动插入日志）
        liftingRodService.liftRod(rodId, action, operatorId);

        // 2. 查询刚插入的日志（按rodId+action查询）
        LiftingRodForm form = new LiftingRodForm();
        form.setRodId(rodId);
        form.setLogAction(action);
        Result<PageUtils> result = queryLogList(form);

        // 3. 断言验证（判断日志是否插入成功）
        assertTrue("接口调用失败", result.getCode() == 0); // 假设code=0表示成功（按公司Result规范调整）
        PageUtils page = result.getData();
        assertTrue("日志未插入", page.getTotalCount() > 0); // 总记录数>0，说明插入成功
        System.out.println("测试通过！插入的日志总数：" + page.getTotalCount());
        System.out.println("日志列表：" + page.getList());
    }

    // 测试用例：分页查询所有日志（无条件）
    @Test
    public void testLogPageQuery() {
        System.out.println("\n=== 测试：分页查询所有日志 ===");
        LiftingRodForm form = new LiftingRodForm();
        form.setPageNum(1); // 第1页
        form.setPageSize(10); // 每页10条

        Result<PageUtils> result = queryLogList(form);

        // 断言验证
        assertTrue("接口调用失败", result.getCode() == 0);
        PageUtils page = result.getData();
        System.out.println("总日志数：" + page.getTotalCount());
        System.out.println("当前页码：" + page.getCurrPage());
        System.out.println("每页条数：" + page.getPageSize());
        System.out.println("当前页日志：" + page.getList());
        assertNotNull("分页数据为空", page);
    }

    // 测试用例：条件查询（按rodId+logType查询）
    @Test
    public void testLogConditionQuery() {
        System.out.println("\n=== 测试：条件查询日志（rodId=1+手动操作） ===");
        LiftingRodForm form = new LiftingRodForm();
        form.setRodId(1L); // 按升降杆ID查询
        form.setLogType(1); // 1-手动操作（按公司LogTypeEnum调整）
        form.setPageNum(1);
        form.setPageSize(10);

        Result<PageUtils> result = queryLogList(form);

        // 断言验证
        assertTrue("接口调用失败", result.getCode() == 0);
        PageUtils page = result.getData();
        System.out.println("符合条件的日志数：" + page.getTotalCount());
        System.out.println("符合条件的日志列表：" + page.getList());
        // 若存在符合条件的日志，验证列表中的日志是否匹配条件
        if (page.getTotalCount() > 0) {
            BizLiftingRodLog log = (BizLiftingRodLog) page.getList().get(0);
            assertEquals("rodId不匹配", 1L, log.getRodId().longValue());
            assertEquals("logType不匹配", 1, log.getLogType().intValue());
        }
    }

    // 测试用例：查询不存在的日志（验证无数据返回格式）
    @Test
    public void testLogNoDataQuery() {
        System.out.println("\n=== 测试：查询不存在的日志 ===");
        LiftingRodForm form = new LiftingRodForm();
        form.setRodId(999L); // 不存在的升降杆ID
        form.setPageNum(1);
        form.setPageSize(10);

        Result<PageUtils> result = queryLogList(form);

        // 断言验证（无数据时返回格式正确）
        assertTrue("接口调用失败", result.getCode() == 0);
        PageUtils page = result.getData();
        assertEquals("总记录数应为0", 0, page.getTotalCount());
        assertEquals("总页数应为0", 0, page.getTotalPage());
        assertTrue("日志列表应为空", page.getList().isEmpty());
        System.out.println("测试通过！无数据时返回格式正确：" + result);
    }

    // 公共方法：调用日志查询接口（复用代码）
    private Result<PageUtils> queryLogList(LiftingRodForm form) {
        try {
            // 调用Service查询日志（传入测试用户ID=1001）
            PageUtils page = liftingRodLogService.queryLogPage(form, 1001L);
            return Result.ok(page); // 按公司Result规范返回
        } catch (Exception e) {
            System.err.println("日志查询失败：" + e.getMessage());
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
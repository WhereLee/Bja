package com.inteink.modules.biz.test;

import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.inteink.InteinkFaster.class) // 替换为你的启动类
public class LiftingRodCrudTest {

    @Autowired
    private LiftingRodService liftingRodService;

    // 测试1：新增升降杆（仅用实体类中存在的字段）
    @Test
    public void testSave() {
        System.out.println("=== 测试：新增升降杆 ===");
        BizLiftingRod rod = new BizLiftingRod();
        // 仅用实体类中定义的字段（和数据库一一对应）
        rod.setRodName("测试升降杆001"); // 名称
        rod.setRodAddr("大门主入口"); // 安装位置
        rod.setRodLongtitude(new BigDecimal("116.403963")); // 经度
        rod.setRodLatitude(new BigDecimal("39.915119")); // 纬度
        rod.setRodOffline(true); // 在线状态：true=在线（对应数据库1）
        rod.setRodRemark("测试新增升降杆"); // 备注
        rod.setRodState(BizLiftingRod.RodStateEnum.HIDE.getCode()); // 升降状态：0-不显示
        rod.setRodCreator(1001L); // 创建人ID
        rod.setRodCreatetime(System.currentTimeMillis() / 1000); // 创建时间戳（秒）
        rod.setRodUpdatetime(System.currentTimeMillis() / 1000); // 更新时间戳（秒）
        rod.setRodStatus(0L); // 有效状态：0-有效

        // 调用MyBatis-Plus自带的save方法（继承来的）
        boolean saveResult = liftingRodService.save(rod);
        assertNotNull("新增后ID为空", rod.getRodId());
        assertTrue("新增失败", saveResult);
        System.out.println("新增成功，升降杆ID：" + rod.getRodId());
    }

    // 测试2：分页查询升降杆（用你的LiftingRodForm）
    @Test
    public void testQueryRodPage() {
        System.out.println("\n=== 测试：分页查询升降杆 ===");
        LiftingRodForm form = new LiftingRodForm();
        form.setPageNum(1);
        form.setPageSize(10);
        form.setRodId(2L); // 查询ID=1的升降杆（仅用实体类存在的字段）

        PageUtils page = liftingRodService.queryRodPage(form);
        assertTrue("查询无数据", page.getTotalCount() > 0);
        System.out.println("分页查询成功：总条数=" + page.getTotalCount());
    }

    // 测试3：修改升降杆（仅用实体类中存在的字段）
    @Test
    public void testUpdate() {
        System.out.println("\n=== 测试：修改升降杆 ===");
        Long rodId = 2L; // 替换为testSave新增的实际ID
        BizLiftingRod rod = new BizLiftingRod();
        rod.setRodId(rodId); // 主键必传
        rod.setRodName("测试升降杆002-修改"); // 修改名称
        rod.setRodAddr("大门侧入口"); // 修改安装位置
        rod.setRodUpdatetime(System.currentTimeMillis() / 1000); // 更新时间戳

        // 调用MyBatis-Plus自带的updateById方法
        boolean updateResult = liftingRodService.updateById(rod);
        assertTrue("修改失败", updateResult);

        // 验证修改结果
        BizLiftingRod updatedRod = liftingRodService.getById(rodId);
        assertEquals("名称修改失败", "测试升降杆002-修改", updatedRod.getRodName());
        assertEquals("安装位置修改失败", "大门侧入口", updatedRod.getRodAddr());
    }

    // 测试4：删除升降杆
    @Test
    public void testDelete() {
        System.out.println("\n=== 测试：删除升降杆 ===");
        Long rodId = 1L; // 替换为testSave新增的实际ID
        // 调用MyBatis-Plus自带的removeById方法
        boolean deleteResult = liftingRodService.removeById(rodId);
        assertTrue("删除失败", deleteResult);
        assertNull("删除后仍存在", liftingRodService.getById(rodId));
    }

    // 测试5：升降杆操作（liftRod原有逻辑，适配真实实体类）
    @Test
    public void testLiftRod() {
        System.out.println("\n=== 测试：升降杆操作 ===");
        Long rodId = 1L; // 替换为新增的实际ID
        Integer action = 1; // 1-升（对应RodStateEnum.UP）
        Long operatorId = 1001L; // 操作人ID

        // 调用原有liftRod方法
        liftingRodService.liftRod(rodId, action, operatorId);
        System.out.println("升降杆操作成功");
    }
}
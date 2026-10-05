package com.inteink.modules.biz.test;

import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.form.ConverterForm;
import com.inteink.modules.biz.service.lifting.ConverterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.inteink.InteinkFaster.class)
public class ConverterTest {

    @Autowired
    private ConverterService converterService;

    // 测试1：新增转换器（需先有存在的升降杆ID，比如rodId=1）
    @Test
    public void testSaveConverter() {
        System.out.println("=== 测试：新增转换器 ===");
        BizConverter converter = new BizConverter();
        converter.setConverterPort(8080);
        converter.setConverterSn("SN20251128001");
        converter.setConverterIp("192.168.1.101");
        converter.setRodId(2L); // 替换为已存在的升降杆ID
        converter.setConverterCreator(1001L); // 创建人ID

        boolean saveResult = converterService.saveConverter(converter);
        assertTrue("新增失败", saveResult);
        assertNotNull("转换器ID为空", converter.getConverterId());
        System.out.println("新增成功，转换器ID：" + converter.getConverterId());
    }

    // 测试2：分页查询转换器
    @Test
    public void testQueryPage() {
        System.out.println("=== 测试：分页查询转换器 ===");
        ConverterForm form = new ConverterForm();
        form.setPageNum(1);
        form.setPageSize(10);
        form.setRodId(2L); // 查询关联rodId=2的转换器

        PageUtils page = converterService.queryPage(form);
        assertTrue("查询无数据", page.getTotalCount() > 0);
        System.out.println("查询成功，总条数：" + page.getTotalCount());
    }

    @Test
    public void testUpdateConverter() {
        System.out.println("=== 测试：修改转换器（端口+IP+绑定升降杆） ===");

        // 替换成你数据库中真实存在的ID
        Long existConverterId = 4L;    // 已存在的转换器ID
        Long newRodId = 2L;            // 要绑定的新升降杆ID（必须存在且有效）

        // 构造修改对象（同时传端口、IP、rod_id）
        BizConverter converter = new BizConverter();
        converter.setConverterId(existConverterId); // 必传：定位转换器
        converter.setConverterPort(9090);          // 改端口
        converter.setConverterIp("192.168.1.102"); // 改IP
        converter.setRodId(newRodId);              // 改绑定的升降杆ID

        try {
            boolean updateResult = converterService.updateConverter(converter);
            assertTrue("修改失败", updateResult);
            System.out.println("✅ 修改成功！");

            // 验证修改结果
            BizConverter updated = converterService.getById(existConverterId);
            System.out.println("新端口：" + updated.getConverterPort());
            System.out.println("新IP：" + updated.getConverterIp());
            System.out.println("新绑定升降杆ID：" + updated.getRodId());
        } catch (Exception e) {
            System.err.println("❌ 失败原因：" + e.getMessage());
            fail(e.getMessage());
        }
    }
}
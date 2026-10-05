package com.inteink.modules.sys.test;

import com.inteink.modules.sys.entity.SysUserEntity;
import com.inteink.modules.sys.service.SysUserService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

// 标记这是SpringBoot测试类，启动Spring容器
@SpringBootTest
// 用JUnit4运行测试（框架默认依赖，没有则加Maven依赖）
@RunWith(SpringRunner.class)
public class ResetPasswordTest {

    // 注入用户服务（用于查询用户、更新密码）
    @Resource
    private SysUserService sysUserService;

    // 测试方法：重置用户密码为 123456
    @Test
    public void resetUserPassword() {
        // -------------------------- 第一步：配置你要重置的用户信息 --------------------------
        String targetUsername = "dev@kf"; // 替换成你要重置的用户名（数据库中存在的）
        String newPlainPassword = "123456"; // 要设置的新明文密码

        // -------------------------- 第二步：查询目标用户（获取userSalt） --------------------------
        // 调用SysUserService的方法，根据用户名查询用户（如果框架没有getByUsername方法，用QueryWrapper查询）
        SysUserEntity user = sysUserService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUserEntity>()
                        .eq("user_name", targetUsername) // 注意：字段名要和数据库一致（可能是user_name或username）
        );

        // 校验用户是否存在
        if (user == null) {
            System.out.println("❌ 未找到用户名：" + targetUsername + "，请检查用户名是否正确！");
            return;
        }
        System.out.println("✅ 找到用户：" + user.getUserName() + "，用户ID：" + user.getUserId() + "，盐值：" + user.getUserSalt());

        // -------------------------- 第三步：按框架规则加密新密码 --------------------------
        // 核心：SHA256 + 用户的userSalt，和你之前看到的updatePassword逻辑完全一致
        String encryptedPassword = new Sha256Hash(newPlainPassword, user.getUserSalt()).toHex();
        System.out.println("✅ 明文密码 " + newPlainPassword + " 加密后：" + encryptedPassword);

        // -------------------------- 第四步：更新数据库中的密码 --------------------------
        // 构建更新对象，只改user_password字段
        SysUserEntity updateUser = new SysUserEntity();
        updateUser.setUserId(user.getUserId()); // 主键ID（必须传，否则无法更新）
        updateUser.setUserPassword(encryptedPassword); // 加密后的新密码

        // 调用Service更新密码
        boolean updateSuccess = sysUserService.updateById(updateUser);

        // 输出结果
        if (updateSuccess) {
            System.out.println("🎉 密码重置成功！新密码：" + newPlainPassword);
            System.out.println("登录时用：用户名=" + targetUsername + "，密码=" + newPlainPassword);
        } else {
            System.out.println("❌ 密码重置失败，请检查数据库连接或权限！");
        }
    }
}
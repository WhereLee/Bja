/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.modules.sys.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.annotation.SysLog;
import com.inteink.common.utils.HttpContextUtils;
import com.inteink.common.utils.IPUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.sys.entity.SysLoginEntity;
import com.inteink.modules.sys.form.SysLoginForm;
import com.inteink.modules.sys.service.SysCaptchaService;
import com.inteink.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * 登录相关
 *
 * @author Mark sunlightcs@gmail.com
 */
@Api(tags = "登录登出",position = 1)
@RestController
public class SysLoginController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysCaptchaService sysCaptchaService;

	/**
	 * 验证码-没有调用
	 */
	@GetMapping("captcha.jpg")
	public void captcha(HttpServletResponse response, String uuid)throws IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");

		//获取图片验证码
		BufferedImage image = sysCaptchaService.getCaptcha(uuid);

		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		IOUtils.closeQuietly(out);
	}

	/**
	 * 初始配置
	 */
	@ApiOperation(value = "初始配置",notes = "查看初始配置；权限说明：不需要登录，不限制权限")
	@ApiOperationSupport(order = 1)
	@GetMapping("/sys/init")
	public Result<Map<String, Object>> init() {

		return Result.ok(sysUserService.getInitParam());
	}

	/**
	 * 登录  账号+验证码(企业微信|短信)
	 */
	@ApiOperation(value = "登录",notes = "系统登录")
	@ApiOperationSupport(order = 21)
	@SysLog(module = "登录登出",func = "登录",value = "系统登录")
	@PostMapping("/sys/login")
	public Result<SysLoginEntity> login(@RequestBody SysLoginForm form) {
		/*boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
		if(!captcha){
			return R.error("验证码不正确");
		}*/

		//获取request
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		String ip = IPUtils.getIpAddr(request);

		SysLoginEntity login = sysUserService.login(form, ip);
		return Result.ok(login, login.getMsg());
	}


	/**
	 * 退出
	 */
	//@SysLog(func = "退出",value = "退出")
	@ApiOperation(value = "退出",notes = "退出登录")
	@ApiOperationSupport(order = 30)
	@SysLog(module = "登录登出",func = "退出",value = "退出登录")
	@PostMapping("/sys/logout")
	public Result logout() {
		sysUserService.logout(getUserId());

		return Result.ok();
	}
	
}

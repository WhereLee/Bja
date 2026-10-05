package com.inteink.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * 
 * @author wll
 * @email 
 * @date 2020-04-22 15:02:49
 */
@ApiModel("系统日志对象")
@Data
@TableName("sys_log")
public class SysLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "日志ID",position = 1)
	@TableId
	private Long logId;

	@ApiModelProperty(value = "日志类型 1-WEB端 2-APP端",position = 10)
	private Integer logType;
	/**
	 * 访问的URL 如：/api/user/login
	 */
	@ApiModelProperty(value = "访问URL",position = 20)
	private String logUrl;
	/**
	 * 参数
	 */
	@ApiModelProperty(value = "参数",position = 30)
	private String logParams;
	/**
	 * 执行结果 0-成功 1-失败
	 */
	@ApiModelProperty(value = "执行结果 0-成功 1-失败",position = 40)
	private Integer logState;
	/**
	 * 执行信息 成功；失败（异常信息）
	 */
	@ApiModelProperty(value = "执行信息",position = 50)
	private String logMessage;

	@ApiModelProperty(value = "返回信息",position = 55)
	private String logReturn;
	/**
	 * 异常信息
	 */
	@ApiModelProperty(value = "异常信息",position = 60)
	private String logError;
	/**
	 * 操作所属功能模块 菜单管理、OLT管理 等
	 */
	@ApiModelProperty(value = "模块",position = 70)
	private String logModule;
	/**
	 * 操作 查询、保存等
	 */
	@ApiModelProperty(value = "操作",position = 80)
	private String logFunc;
	/**
	 * 操作说明 查询用户等
	 */
	@ApiModelProperty(value = "操作说明",position = 90)
	private String logOperation;
	/**
	 * 调用的方法
	 */
	@ApiModelProperty(value = "调用方法",position = 100)
	private String logMethod;
	/**
	 * 请求的IP
	 */
	@ApiModelProperty(value = "请求IP",position = 110)
	private String logIp;
	/**
	 * 请求的浏览器
	 */
	@ApiModelProperty(value = "请求浏览器",position = 120)
	private String logBrowser;
	/**
	 * 执行时长 单位 毫秒
	 */
	@ApiModelProperty(value = "执行时长（毫秒）",position = 130)
	private Long logDuration;
	/**
	 * 操作人 null是表示没有获取到 -1是系统 0是websocket的虚拟用
	 */
	@ApiModelProperty(value = "操作人",position = 140)
	private Long logCreator;
	/**
	 * 操作人用户名
	 */
	@ApiModelProperty(value = "操作员姓名",position = 150)
	private String logCreatorName;

	@ApiModelProperty(value = "OPENID",position = 160)
	private String logOpenid;

	@ApiModelProperty(value = "昵称",position = 170)
	private String logNickname;

	@ApiModelProperty(value = "头像URL",position = 180)
	private String logProfile;

	@ApiModelProperty(value = "手机",position = 190)
	private String logMobile;
	/**
	 * 创建时的时间戳，单位为秒
	 */
	@ApiModelProperty(value = "操作时间戳（秒）",position = 200)
	private Long logCreatetime;
}

package com.inteink.modules.sys.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 前端传入参数对象封装
 * @author wll
 * @email 
 * @date 2021-02-20 15:06:27
 */
@ApiModel("XXVO")
@Data
public class SysUserWechatVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
    @ApiModelProperty(value = "XXXX",position = 1)
    //TODO @NotNull @Size @NotBlank 等校验 字符类型字段空格tab键等字符过滤 StringUtils.replaceBlank()方法
	private Long wechatId;
	/**
	 * OPENID
	 */
    @ApiModelProperty(value = "XXXX",position = 1)
    //TODO @NotNull @Size @NotBlank 等校验 字符类型字段空格tab键等字符过滤 StringUtils.replaceBlank()方法
	private String wechatOpenid;
	/**
	 * 昵称
	 */
    @ApiModelProperty(value = "XXXX",position = 1)
    //TODO @NotNull @Size @NotBlank 等校验 字符类型字段空格tab键等字符过滤 StringUtils.replaceBlank()方法
	private String wechatNickname;
	/**
	 * 头像URL
	 */
    @ApiModelProperty(value = "XXXX",position = 1)
    //TODO @NotNull @Size @NotBlank 等校验 字符类型字段空格tab键等字符过滤 StringUtils.replaceBlank()方法
	private String wechatProfile;
	/**
	 * 手机
	 */
    @ApiModelProperty(value = "XXXX",position = 1)
    //TODO @NotNull @Size @NotBlank 等校验 字符类型字段空格tab键等字符过滤 StringUtils.replaceBlank()方法
	private String wechatMobile;
	/**
	 * 首次登录时间 时间戳 单位秒
	 */
    @ApiModelProperty(value = "XXXX",position = 1)
    //TODO @NotNull @Size @NotBlank 等校验 字符类型字段空格tab键等字符过滤 StringUtils.replaceBlank()方法
	private Long wechatCreatetime;
	/**
	 * 更新时间 时间戳 单位秒
	 */
    @ApiModelProperty(value = "XXXX",position = 1)
    //TODO @NotNull @Size @NotBlank 等校验 字符类型字段空格tab键等字符过滤 StringUtils.replaceBlank()方法
	private Long wechatUpdatetime;

}

package com.inteink.modules.sys.vo;


import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 
 * 前端传入参数对象封装
 * @author author
 * @email 
 * @date 2023-03-18 15:05:04
 */
@ApiModel("字典VO")
@Data
public class SysDictionaryVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键 自增
	 */
    @ApiModelProperty(value = "字典主键",position = 1)
	@NotNull(message = "字典主键不能为空",groups = {UpdateGroup.class})
	private Long dicId;
	/**
	 * 分类  短信SMS、IP黑白名单 iplist、企业微信 qyweixin 等
	 */
    @ApiModelProperty(value = "分类  短信SMS、IP黑白名单 iplist、企业微信 qyweixin 等",position = 10)
	@Size(max = 64,message = "分类过长")
	@NotBlank(message = "分类不能为空",groups = {AddGroup.class})
	private String dicSort;
	/**
	 * 值
	 */
    @ApiModelProperty(value = "值",position = 21)
	@Size(max = 2048,message = "值过长")
	@NotBlank(message = "值不能为空",groups = {AddGroup.class})
	private String dicValue;
	/**
	 * 说明、备注
	 */
    @ApiModelProperty(value = "说明、备注",position = 31)
	@Size(max = 256,message = "备注过长")
	private String dicRemark;

}

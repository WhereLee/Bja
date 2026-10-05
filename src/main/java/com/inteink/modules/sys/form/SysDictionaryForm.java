package com.inteink.modules.sys.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * 查询数据对象
 * @author author
 * @email 
 * @date 2023-03-18 15:05:04
 */
@ApiModel("字典查询对象")
@Data
public class SysDictionaryForm extends CommonForm {
    @ApiModelProperty(value = "分类  短信SMS、IP黑白名单 iplist、企业微信 qyweixin 等-精确",position = 10)
    private String dicSort;

    @ApiModelProperty(value = "值",position = 31)
    private String dicValue;
}

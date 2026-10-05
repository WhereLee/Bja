package com.inteink.modules.app.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("楼栋查询对象")
@Data
public class AppLoginForm {
    @ApiModelProperty(value = "code-必填",position = 1)
    private String code;

    @ApiModelProperty(value = "加密数据encryptedData-必填",position = 10)
    private String encryptedData;

    @ApiModelProperty(value = "偏移量iv-必填",position = 10)
    private String iv;

}

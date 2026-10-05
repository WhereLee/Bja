package com.inteink.modules.biz.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 转换器 列表/分页查询条件。
 */
@ApiModel("转换器查询条件")
@Data
public class ConverterForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("设备编号SN（模糊）")
    private String converterSn;

    @ApiModelProperty("IP地址（模糊）")
    private String converterIp;

    @ApiModelProperty("绑定状态 true-已绑定 false-未绑定 null-不限")
    private Boolean bound;

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;
}

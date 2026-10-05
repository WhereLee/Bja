package com.inteink.modules.biz.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("转换器查询表单")
public class ConverterForm {
    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;

    @ApiModelProperty("关联升降杆ID")
    private Long rodId;

    @ApiModelProperty("转换器SN")
    private String converterSn;
}
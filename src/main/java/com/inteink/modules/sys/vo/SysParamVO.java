package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel("参数VO")
@Data
public class SysParamVO {
    @ApiModelProperty(value = "参数ID",position = 1)
    @NotNull(message = "参数主键不能为空",groups = {UpdateGroup.class})
    private Long paramId;

    @ApiModelProperty(value = "参数名称",position = 2)
    @Size(max = 256,message = "参数名称过长",groups = {AddGroup.class})
    @NotBlank(message="参数名称不能为空",groups = {AddGroup.class})
    private String paramName;

    public void setParamName(String paramName) {
        this.paramName = StringUtils.replaceBlank(paramName);
    }

    @ApiModelProperty(value = "参数Key",position = 3)
    @Size(max = 256,message = "参数Key过长",groups = {AddGroup.class})
    @NotBlank(message="参数Key不能为空",groups = {AddGroup.class})
    private String paramKey;

    public void setParamKey(String paramKey) {
        this.paramKey = StringUtils.replaceBlank(paramKey);
    }

    @ApiModelProperty(value = "参数值",position = 4)
    @Size(max = 256,message = "参数值过长",groups = {AddGroup.class,UpdateGroup.class})
    @NotNull(message = "参数值不能为空",groups = {AddGroup.class})
    private String paramValue;

    public void setParamValue(String paramValue) {
        this.paramValue = StringUtils.replaceBlank(paramValue);
    }

    @ApiModelProperty(value = "参数说明",position = 5)
    @Size(max = 256,message = "参数说明过长",groups = {AddGroup.class})
    @NotNull(message = "参数说明不能为空",groups = {AddGroup.class})
    private String paramComment;

    public void setParamComment(String paramComment) {
        this.paramComment = StringUtils.replaceBlank(paramComment);
    }

}

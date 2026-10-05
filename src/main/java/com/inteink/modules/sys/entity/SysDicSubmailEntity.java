package com.inteink.modules.sys.entity;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("赛迪云短信对象")
@Data
public class SysDicSubmailEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "赛迪云appid",position = 20)
    private String submailAppid;

    @ApiModelProperty(value = "赛迪云appkey",position = 30)
    private String submailAppkey;

    @ApiModelProperty(value = "赛迪云模块id",position = 40)
    private String submailProject;

    public SysDicSubmailEntity() {};

    public SysDicSubmailEntity(String submailAppid, String submailAppkey, String submailProject) {
        this.submailAppid = submailAppid;
        this.submailAppkey = submailAppkey;
        this.submailProject = submailProject;
    }

    /**
     * 校验参数
     * @return true：成功  false：
     */
    public Boolean check() {
        return (StringUtils.isNotBlank(submailAppid) && StringUtils.isNotBlank(submailAppkey) && StringUtils.isNotBlank(submailProject));
    }
}

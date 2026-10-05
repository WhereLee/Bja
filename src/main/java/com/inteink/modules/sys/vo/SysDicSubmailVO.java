package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@ApiModel("赛迪云短信VO")
@Data
public class SysDicSubmailVO {
    @ApiModelProperty(value = "赛迪云appid",position = 30)
    @Size(max = 2048,message = "赛迪云appid过长")
    private String submailAppid;

    public void setSubmailAppid(String submailAppid) {
        this.submailAppid = StringUtils.replaceBlank(submailAppid);
    }

    @ApiModelProperty(value = "赛迪云appkey",position = 30)
    @Size(max = 2048,message = "赛迪云appkey过长")
    private String submailAppkey;

    public void setSubmailAppkey(String submailAppkey) {
        this.submailAppkey = StringUtils.replaceBlank(submailAppkey);
    }

    @ApiModelProperty(value = "赛迪云模块id",position = 40)
    @Size(max = 2048,message = "赛迪云模块id过长")
    private String submailProject;

    public void setSubmailProject(String submailProject) {
        this.submailProject = StringUtils.replaceBlank(submailProject);
    }
}

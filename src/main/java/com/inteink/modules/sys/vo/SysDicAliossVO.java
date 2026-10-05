package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@ApiModel("AliossVO")
@Data
public class SysDicAliossVO {
    @ApiModelProperty(value = "阿里OSS ENDPOINT",position = 20)
    @Size(max = 2048,message = "ENDPOINT过长")
    private String aliossEndpoint;

    public void setAliossEndpoint(String aliossEndpoint) {
        this.aliossEndpoint = StringUtils.replaceBlank(aliossEndpoint);
    }

    @ApiModelProperty(value = "阿里OSS accessKeyId",position = 30)
    @Size(max = 2048,message = "accessKeyId过长")
    private String aliossAk;

    public void setAliossAk(String aliossAk) {
        this.aliossAk = StringUtils.replaceBlank(aliossAk);
    }

    @ApiModelProperty(value = "阿里OSS secretAccessKey",position = 40)
    @Size(max = 2048,message = "secretAccessKey过长")
    private String aliossSk;

    public void setAliossSk(String aliossSk) {
        this.aliossSk = StringUtils.replaceBlank(aliossSk);
    }

    @ApiModelProperty(value = "图片存储的BUCKET",position = 50)
    @Size(max = 2048,message = "图片BUCKET过长")
    private String aliossBucketPic;

    public void setAliossBucketPic(String aliossBucketPic) {
        this.aliossBucketPic = StringUtils.replaceBlank(aliossBucketPic);
    }

    @ApiModelProperty(value = "音频存储的BUCKET",position = 60)
    @Size(max = 2048,message = "音频BUCKET过长")
    private String aliossBucketAudio;

    public void setAliossBucketAudio(String aliossBucketAudio) {
        this.aliossBucketAudio = StringUtils.replaceBlank(aliossBucketAudio);
    }

    @ApiModelProperty(value = "视频存储的BUCKET",position = 70)
    @Size(max = 2048,message = "视频BUCKET过长")
    private String aliossBucketVideo;

    public void setAliossBucketVideo(String aliossBucketVideo) {
        this.aliossBucketVideo = StringUtils.replaceBlank(aliossBucketVideo);
    }
}

package com.inteink.modules.sys.entity;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("Alioss对象")
@Data
public class SysDicAliossEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "阿里OSS ENDPOINT",position = 20)
    private String aliossEndpoint;

    @ApiModelProperty(value = "阿里OSS accessKeyId",position = 30)
    private String aliossAk;

    @ApiModelProperty(value = "阿里OSS secretAccessKey",position = 40)
    private String aliossSk;

    @ApiModelProperty(value = "图片存储的BUCKET",position = 50)
    private String aliossBucketPic;

    @ApiModelProperty(value = "视频存储的BUCKET",position = 60)
    private String aliossBucketAudio;

    @ApiModelProperty(value = "音频存储的BUCKET",position = 70)
    private String aliossBucketVideo;

    public SysDicAliossEntity() {}

    public SysDicAliossEntity(String aliossEndpoint, String aliossAk, String aliossSk
            , String aliossBucketPic, String aliossBucketAudio, String aliossBucketVideo) {
        this.aliossEndpoint = aliossEndpoint;
        this.aliossAk = aliossAk;
        this.aliossSk = aliossSk;
        this.aliossBucketPic = aliossBucketPic;
        this.aliossBucketAudio = aliossBucketAudio;
        this.aliossBucketVideo = aliossBucketVideo;
    }

    /**
     * 校验
     * @return true：通过 false：阿里云OSS参数未配置
     */
    public Boolean check() {
        return (StringUtils.isNotBlank(aliossEndpoint) && StringUtils.isNotBlank(aliossAk) && StringUtils.isNotBlank(aliossSk)
                && StringUtils.isNotBlank(aliossBucketPic) && StringUtils.isNotBlank(aliossBucketAudio) && StringUtils.isNotBlank(aliossBucketVideo));
    }
}

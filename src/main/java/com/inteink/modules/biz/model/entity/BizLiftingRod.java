package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 道闸/升降杆：被操作、有状态的业务对象。
 */
@ApiModel("道闸/升降杆实体")
@Data
@TableName("biz_lifting_rod")
public class BizLiftingRod implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("杆ID")
    @TableId(value = "rod_id", type = IdType.AUTO)
    private Long rodId;

    @ApiModelProperty("名称")
    private String rodName;

    @ApiModelProperty("安装位置")
    private String rodAddr;

    @ApiModelProperty("经度")
    private BigDecimal rodLongtitude;

    @ApiModelProperty("纬度")
    private BigDecimal rodLatitude;

    @ApiModelProperty("是否在线 0-离线 1-在线")
    private Integer rodOffline;

    @ApiModelProperty("升降状态 0-默认 1-升 2-降")
    private Integer rodState;

    @ApiModelProperty("备注")
    private String rodRemark;

    @ApiModelProperty("创建人")
    private Long rodCreator;

    @ApiModelProperty("创建时间戳(秒)")
    private Long rodCreatetime;

    @ApiModelProperty("更新时间戳(秒)")
    private Long rodUpdatetime;

    @ApiModelProperty("状态 0-有效 >0-无效(逻辑删除)")
    private Long rodStatus;

    /** 未删除即有效 */
    public boolean isValid() {
        return rodStatus != null && rodStatus == 0L;
    }
}

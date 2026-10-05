package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 转换器：道闸背后的网络控制设备（台账 + 绑定到某根杆）。
 * 无独立生命周期；rod_id 为空表示已登记、暂未绑定杆。
 */
@ApiModel("转换器实体")
@Data
@TableName("biz_converter")
public class BizConverter implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("转换器ID")
    @TableId(value = "converter_id", type = IdType.AUTO)
    private Long converterId;

    @ApiModelProperty("设备端口")
    private Integer converterPort;

    @ApiModelProperty("设备编号 SN")
    private String converterSn;

    @ApiModelProperty("IP地址")
    private String converterIp;

    @ApiModelProperty("绑定的升降杆ID（可空=未绑定）")
    private Long rodId;

    @ApiModelProperty("创建人")
    private Long converterCreator;

    @ApiModelProperty("创建时间戳(秒)")
    private Long converterCreatetime;

    @ApiModelProperty("更新时间戳(秒)")
    private Long converterUpdatetime;

    @ApiModelProperty("状态 0-有效 >0-无效(逻辑删除)")
    private Long converterStatus;

    public boolean isValid() {
        return converterStatus != null && converterStatus == 0L;
    }
}

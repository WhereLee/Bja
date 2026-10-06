package com.inteink.modules.biz.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 看板汇总：在线/离线/未绑定计数 + 每杆实时项列表。
 */
@ApiModel("看板汇总")
@Data
public class DashboardVO {
    @ApiModelProperty("杆总数")
    private int total;
    @ApiModelProperty("在线数（绑定且探测可达）")
    private int onlineCount;
    @ApiModelProperty("离线数（绑定但探测不可达）")
    private int offlineCount;
    @ApiModelProperty("未绑定数")
    private int unboundCount;
    @ApiModelProperty("每杆实时项")
    private List<RodLiveVO> rods;
}

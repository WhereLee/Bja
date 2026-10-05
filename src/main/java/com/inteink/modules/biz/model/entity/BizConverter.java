package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 转换器实体类（升降杆硬件对接设备）
 * 对应表：biz_converter
 * @author 实习开发
 * @date 2025-11-27
 */
@Data
@TableName("biz_converter") // 精准映射数据表名
public class BizConverter implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 自增
     */
    @TableId(value = "converter_id", type = IdType.AUTO)
    private Long converterId;

    /**
     * 设备端口
     */
    @TableField("converter_port")
    private Integer converterPort;

    /**
     * 设备编号 SN
     */
    @TableField("converter_sn")
    private String converterSn;

    /**
     * IP地址
     */
    @TableField("converter_ip")
    private String converterIp;

    /**
     * 关联升降杆ID（关联biz_lifting_rod表的rod_id）
     */
    @TableField("rod_id")
    private Long rodId;

    /**
     * 创建人（关联用户表ID）
     */
    @TableField("converter_creator")
    private Long converterCreator;

    /**
     * 创建时间戳 秒
     */
    @TableField("converter_createtime")
    private Long converterCreatetime;

    /**
     * 更新时间戳 秒
     */
    @TableField("converter_updatetime")
    private Long converterUpdatetime;

    /**
     * 状态 0-有效 >0 无效 默认0
     */
    @TableField("converter_status")
    private Long converterStatus;

    // ========== 扩展：硬件设备常用校验（可选） ==========
    /**
     * 校验IP地址格式是否合法（简单正则）
     * @return IP格式是否合法
     */
    public boolean checkIpFormat() {
        if (converterIp == null) {
            return false;
        }
        // 简单IPV4正则（仅适配基础场景，复杂场景可引入第三方工具类）
        String ipRegex = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";
        return converterIp.matches(ipRegex);
    }

    /**
     * 校验端口号是否合法（0-65535）
     * @return 端口是否合法
     */
    public boolean checkPortValid() {
        return converterPort != null && converterPort >= 0 && converterPort <= 65535;
    }
}
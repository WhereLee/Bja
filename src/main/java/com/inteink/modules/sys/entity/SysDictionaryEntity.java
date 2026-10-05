package com.inteink.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.inteink.modules.sys.vo.SysDictionaryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * 
 * @author author
 * @email 
 * @date 2023-03-09 15:18:02
 */
@ApiModel("字典实体")
@Data
@TableName("sys_dictionary")
public class SysDictionaryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键 自增
	 */
    @ApiModelProperty(value = "主键 自增",position = 1)
	@TableId
	private Long dicId;
	/**
	 * 分类  短信SMS、IP黑白名单 iplist、企业微信 qyweixin 等
	 */
    @ApiModelProperty(value = "分类  短信SMS、IP黑白名单 iplist、企业微信 qyweixin 等",position = 10)
	private String dicSort;
	/**
	 * Key
	 */
    @ApiModelProperty(value = "Key",position = 21)
	private String dicKey;
	/**
	 * 值
	 */
    @ApiModelProperty(value = "值",position = 31)
	private String dicValue;
	/**
	 * 说明、备注
	 */
    @ApiModelProperty(value = "说明、备注",position = 41)
	private String dicRemark;
	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人ID",position = 60)
	private Long dicCreator;
	/**
	 * 创建时间戳，单位秒
	 */
	@ApiModelProperty(value = "创建时间戳（秒）",position = 70)
	private Long dicCreatetime;
	/**
	 * 更新时间戳，单位秒
	 */
	@ApiModelProperty(value = "更新时间戳（秒）",position = 80)
	private Long dicUpdatetime;
	/**
	 * 状态 0-有效 >0 无效 默认0
	 */
	@ApiModelProperty(value = "状态标志 状态 0-有效 >0 无效 默认0",position = 100)
	private Long dicStatus;

    public SysDictionaryEntity() {}

	/**
	 * 新增或修改
	 * @param vo
	 * @param type 1-新增 2-修改
	 * @param key
	 */
	public SysDictionaryEntity(SysDictionaryVO vo, Integer type, Integer key, Long creator) {
		Long timestamp = System.currentTimeMillis()/1000;
		this.dicValue = vo.getDicValue();
		this.dicRemark = vo.getDicRemark();
		this.dicUpdatetime = timestamp;
		if (type == 1) {
			this.dicSort = vo.getDicSort();
			this.dicKey = key + "";
			this.dicCreator = creator;
			this.dicCreatetime = timestamp;
		} else
			this.dicId = vo.getDicId();
	}

	/**
	 * 删除-将status=id
	 * @param dicId
	 */
	public SysDictionaryEntity(Long dicId) {
		this.dicId = dicId;
		this.dicStatus = dicId;
		this.dicUpdatetime = System.currentTimeMillis()/1000;
	}

	/**
	 * 判断数据是否有效，即未删除
	 * @return true：是
	 */
	public boolean valid() {
		return (dicStatus != null && dicStatus == 0);
	}
}

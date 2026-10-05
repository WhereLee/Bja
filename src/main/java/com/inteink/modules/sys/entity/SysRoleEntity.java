package com.inteink.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

import com.inteink.modules.sys.vo.SysRoleVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 
 * 
 * @author wll
 * @email 
 * @date 2020-04-22 14:30:49
 */
@ApiModel("角色实体")
@Data
@TableName("sys_role")
public class SysRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "角色ID",position = 1)
	@TableId
	private Long roleId;
	/**
	 * 角色类型：0：开发员 1：系统管理员 2：其他管理员（如网络管理员，维修人员等）
	 * 0：只开发用，不开放给广电
	 * 1：系统管理员
	 * 2：其他管理员
	 */
	//private Integer roleType;
	/**
	 * 角色名称
	 */
	@ApiModelProperty(value = "角色名称",position = 3)
	private String roleName;

	/**
	 * 角色描述，备注
	 */
	@ApiModelProperty(value = "角色描述",position = 4)
	private String roleComment;

	/**
	 * 创建人 对应user表id 
	 */
	@ApiModelProperty(value = "创建人",position = 5)
	private Long roleCreator;
	/**
	 * 角色创建时间戳，单位秒
	 */
	@ApiModelProperty(value = "创建时间戳（秒）",position = 6)
	private Long roleCreatetime;
	/**
	 * 角色修改时间戳，单位秒
	 */
	@ApiModelProperty(value = "更新时间戳（秒）",position = 7)
	private Long roleUpdatetime;
	/**
	 * 0-有效，>0-无效，表示删除
	 */
	@ApiModelProperty(value = "状态标志 0-正常 >0-删除",position = 9)
	//@TableLogic
	private Long roleStatus;

	@ApiModelProperty(value = "菜单ID列表",position = 10)
	@TableField(exist=false)
	private List<Long> menuIdList;

	@ApiModelProperty(value = "菜单列表",position = 11)
	@TableField(exist=false)
	private List<SysMenuEntity> menuList;

	@ApiModelProperty(value = "创建人名称",position = 14)
	@TableField(exist=false)
	private String creatorName;



	public SysRoleEntity() {}

	/**
	 * 新增或修改
	 * @param roleVO 前端传入参数对象
	 * @param type 1-新增 2-修改
	 * @param roleCreator 创建人
	 */
	public SysRoleEntity(SysRoleVO roleVO,Integer type,Long roleCreator) {
		this.roleName = roleVO.getRoleName();
		this.roleComment = roleVO.getRoleComment();
		this.menuIdList = roleVO.getMenuIdList();
		if (type == 1) {
			this.roleCreator = roleCreator;
			this.roleCreatetime = System.currentTimeMillis()/1000;
			this.roleUpdatetime = System.currentTimeMillis()/1000;
		} else if (type == 2) {
			this.roleId = roleVO.getRoleId();
			this.roleUpdatetime = System.currentTimeMillis()/1000;
		}
	}

	/**
	 * 删除-将status=id
	 * @param roleId
	 */
	public SysRoleEntity(Long roleId) {
		this.roleId = roleId;
		this.roleStatus = roleId;
		this.roleUpdatetime = System.currentTimeMillis()/1000;
	}

	/**
	 * 判断数据是否有效，即未删除
	 * @return true：是
	 */
	public boolean isValid() {
		return (roleStatus != null && roleStatus == 0);
	}

}

package com.inteink.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author wll
 * @email 
 * @date 2020-04-22 14:30:49
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Long id;
	/**
	 * 角色ID，sys_role表
	 */
	private Long roleId;
	/**
	 * 菜单ID，sys_menu表
	 */
	private Long menuId;

}

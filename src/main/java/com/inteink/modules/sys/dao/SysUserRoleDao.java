/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inteink.modules.sys.entity.SysUserRoleEntity;
import org.springframework.stereotype.Repository;

/**
 * 用户与角色对应关系
 *
 * @author Mark sunlightcs@gmail.com
 */
@Repository
public interface SysUserRoleDao extends BaseMapper<SysUserRoleEntity> {
}

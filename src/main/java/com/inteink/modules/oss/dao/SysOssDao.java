/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.modules.oss.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inteink.modules.oss.entity.SysOssEntity;
import org.springframework.stereotype.Repository;

/**
 * 文件上传
 *
 * @author Mark sunlightcs@gmail.com
 */
@Repository
public interface SysOssDao extends BaseMapper<SysOssEntity> {
	
}

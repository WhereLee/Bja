package com.inteink.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.sys.entity.SysLogEntity;
import com.inteink.modules.sys.form.SysLogForm;


/**
 * 
 *
 * @author wll
 * @email 
 * @date 2020-04-22 15:02:49
 */
public interface SysLogService extends IService<SysLogEntity> {

    /**
     * 查询日志信息-分页
     * @param form
     * @param userId 登录用户（非开发员则排除开发员日志）
     * @return
     */
    PageUtils queryPage(SysLogForm form, Long userId);

    /**
     * 根据ID 查询日志
     * @param logId
     * @return
     */
    SysLogEntity getInfo(Long logId);
}


package com.inteink.modules.sys.service.impl;

import com.inteink.common.exception.RRException;
import com.inteink.common.utils.*;
import com.inteink.modules.sys.form.SysLogForm;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.inteink.modules.sys.dao.SysLogDao;
import com.inteink.modules.sys.entity.SysLogEntity;
import com.inteink.modules.sys.service.SysLogService;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    /**
     * 查询日志信息-分页
     * @param form
     * @param userId 登录用户（非开发员则排除开发员日志）
     * @return
     */
    @Override
    public PageUtils queryPage(SysLogForm form, Long userId) {
        //非开发员，排除开发员产生的日志
        boolean excludeDeveloper = userId == null || !userId.equals(Constant.DEVELOPER_USERID);

        QueryWrapper<SysLogEntity> wrapper = new QueryWrapper<SysLogEntity>()
                .ne(excludeDeveloper, "log_creator", Constant.DEVELOPER_USERID)
                .eq(form.getLogType() != null, "log_type", form.getLogType())
                .eq(form.getLogState() != null, "log_state", form.getLogState())
                .like(StringUtils.isNotBlank(form.getLogModule()), "log_module", form.getLogModule())
                .like(StringUtils.isNotBlank(form.getLogFunc()), "log_func", form.getLogFunc())
                .like(StringUtils.isNotBlank(form.getLogCreatorName()), "log_creator_name", form.getLogCreatorName())
                .like(StringUtils.isNotBlank(form.getLogParams()), "log_params", form.getLogParams())
                .like(StringUtils.isNotBlank(form.getLogOpenid()), "log_openid", form.getLogOpenid())
                .like(StringUtils.isNotBlank(form.getLogNickname()), "log_nickname", form.getLogNickname())
                .like(StringUtils.isNotBlank(form.getLogMobile()), "log_mobile", form.getLogMobile())
                .ge(form.getStarttime() != null, "log_createtime", form.getStarttime())
                .le(form.getEndtime() != null, "log_createtime", form.getEndtime());

        IPage<SysLogEntity> page = this.page(
                new Query<SysLogEntity>().getPage(new MapUtils()
                        .put(Constant.PAGE, form.getPage()).put(Constant.LIMIT, form.getLimit())
                        .put(Constant.ORDER_FIELD, "log_id").put(Constant.ORDER, "desc")),
                wrapper);

        return new PageUtils(page);
    }

    /**
     * 根据ID 查询日志
     * @param logId
     * @return
     */
    @Override
    public SysLogEntity getInfo(Long logId) {
        //1.查询日志信息
        SysLogEntity log = this.getById(logId);

        if (log == null || log.getLogId() == null) {
            throw new RRException("该日志信息不存在或已删除");
        }

        return log;
    }
}

package com.inteink.modules.sys.dao;

import com.inteink.modules.sys.entity.SysDictionaryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inteink.modules.sys.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 
 * 
 * @author author
 * @email 
 * @date 2023-03-09 15:18:02
 */
@Repository
public interface SysDictionaryDao extends BaseMapper<SysDictionaryEntity> {
    /**
     * 取最大的Key
     * @param dicSort
     * @return
     */
    Integer getLatestKey(String dicSort);

    /**
     * 设置IP黑白名单
     * @param iplistVO
     */
    void setIpList(@Param("iplistVO") SysDicIplistVO iplistVO);

    /**
     * 配置短信
     * @param smsVO
     */
    void setSms(@Param("smsVO") SysDicSmsVO smsVO);

    /**
     * 配置短信-赛迪云
     * @param submailVO
     */
    void setSubmail(@Param("submailVO") SysDicSubmailVO submailVO);

    /**
     * 配置企业微信
     * @param qyweixinVO
     */
    void setQyweixin(@Param("qyweixinVO") SysDicQyweixinVO qyweixinVO);

    /**
     * 配置微信小程序
     * @param wechatVO
     */
    void setWechat(@Param("wechatVO") SysDicWechatVO wechatVO);

    /**
     * 配置阿里OSS
     * @param aliossVO
     */
    void setAlioss(@Param("aliossVO") SysDicAliossVO aliossVO);
}

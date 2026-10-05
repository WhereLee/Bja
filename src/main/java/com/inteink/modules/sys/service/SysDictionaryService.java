package com.inteink.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.sys.entity.*;
import com.inteink.modules.sys.form.SysDictionaryForm;
import com.inteink.modules.sys.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 
 *
 * @author author
 * @email 
 * @date 2023-03-09 15:18:02
 */
public interface SysDictionaryService extends IService<SysDictionaryEntity> {
    /**
     * 分页查询字典
     * @param form
     * @return
     */
    PageUtils queryPage(SysDictionaryForm form);

    /**
     * 下拉查询字典
     * @param form
     * @return
     */
    List<SysDictionaryEntity> queryList(SysDictionaryForm form);

    /**
     * 根据ID查询字典
     * @param dicId
     * @return
     */
    SysDictionaryEntity getInfo(Long dicId);

    /**
     * 新增字典
     * @param vo
     * @param creator
     */
    void save(SysDictionaryVO vo, Long creator);

    /**
     * 修改字典
     * @param vo
     */
    void update(SysDictionaryVO vo);

    /**
     * 逻辑删除字典
     * @param dicId
     */
    void delete(Long dicId);

    /**
     * 查询IP黑白名单
     * @return
     */
    SysDicIplistEntity getIpList();

    /**
     * 设置IP黑白名单
     * @param iplistVO
     */
    void setIpList(SysDicIplistVO iplistVO);

    /**
     * 查询短信设置
     * @return
     */
    SysDicSmsEntity getSms();

    /**
     * 设置短信
     * @param smsVO
     */
    void setSms(SysDicSmsVO smsVO);

    /**
     * 校验验证码
     * @param codeKey
     * @param captcha
     */
    void verifySmsCode(String codeKey, String captcha);

    /**
     * 查询短信设置-赛迪云
     * @return
     */
    SysDicSubmailEntity getSubmail();

    /**
     * 设置短信-赛迪云
     * @param submailVO
     */
    void setSubmail(SysDicSubmailVO submailVO);

    /**
     * 校验验证码-赛迪云
     * @param codeKey
     * @param captcha
     */
    void verifySubmailCode(String codeKey, String captcha);

    /**
     * 查询企业微信设置
     * @return
     */
    SysDicQyweixinEntity getQyweixin();

    /**
     * 设置企业微信
     * @param qyweixinVO
     */
    void setQyweixin(SysDicQyweixinVO qyweixinVO);

    /**
     * 查询微信小程序设置
     * @return
     */
    SysDicWechatEntity getWechat();

    /**
     * 设置微信小程序
     * @param wechatVO
     */
    void setWechat(SysDicWechatVO wechatVO);

    /**
     * 查询微阿里OSS设置
     * @return
     */
    SysDicAliossEntity getAlioss();

    /**
     * 配置阿里OSS
     * @param aliossVO
     */
    void setAlioss(@Param("aliossVO") SysDicAliossVO aliossVO);

}


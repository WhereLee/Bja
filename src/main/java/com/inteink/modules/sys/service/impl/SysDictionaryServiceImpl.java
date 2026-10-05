package com.inteink.modules.sys.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inteink.common.exception.RRException;
import com.inteink.common.utils.*;
import com.inteink.common.validator.Assert;
import com.inteink.common.validator.ValidatorUtils;
import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import com.inteink.modules.sys.entity.*;
import com.inteink.modules.sys.form.SysDictionaryForm;
import com.inteink.modules.sys.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.inteink.modules.sys.dao.SysDictionaryDao;
import com.inteink.modules.sys.service.SysDictionaryService;

import java.util.List;

@Slf4j
@Service("sysDictionaryService")
public class SysDictionaryServiceImpl extends ServiceImpl<SysDictionaryDao, SysDictionaryEntity> implements SysDictionaryService {
    @Autowired
    private SysDictionaryDao sysDictionaryDao;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 分页查询字典
     * @param form
     * @return
     */
    @Override
    public PageUtils queryPage(SysDictionaryForm form) {
        //查询条件
        String dicSort = form.getDicSort();
        String dicValue = form.getDicValue();

        //查询
        IPage<SysDictionaryEntity> page = this.page(
                new Query<SysDictionaryEntity>().getPage(new MapUtils()
                        .put(Constant.PAGE,form.getPage()).put(Constant.LIMIT,form.getLimit())),
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_status", 0)
                        .eq(StringUtils.isNotBlank(dicSort), "dic_sort", dicSort)
                        .like(StringUtils.isNotBlank(dicValue), "dic_value", dicValue)
        );

        return new PageUtils(page);
    }



    /**
     * 下拉查询字典
     * @param form
     * @return
     */
    @Override
    public List<SysDictionaryEntity> queryList(SysDictionaryForm form) {
        //查询条件
        String dicSort = form.getDicSort();
        String dicValue = form.getDicValue();

        //查询
        List<SysDictionaryEntity> list = this.list(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_status", 0)
                        .eq(StringUtils.isNotBlank(dicSort), "dic_sort", dicSort)
                        .like(StringUtils.isNotBlank(dicValue), "dic_value", dicValue)
        );

        return list;
    }

    /**
     * 根据ID查询字典
     * @param dicId
     * @return
     */
    @Override
    public SysDictionaryEntity getInfo(Long dicId) {
        //1.查询字典
        SysDictionaryEntity dic = this.getById(dicId);

        if (dic == null || !dic.valid())
            throw new RRException("该字典不存在或已删除");

        //2.查询其他信息 TODO

        return dic;
    }

    /**
     * 新增字典
     * @param vo
     * @param creator
     */
    @Override
    public void save(SysDictionaryVO vo, Long creator) {
        //1.数据校验
        ValidatorUtils.validateEntity(vo,AddGroup.class);

        String dicSort = vo.getDicSort();
        String dicValue = vo.getDicValue();

        //2.校验其他 TODO
        //校验值是否已存在
        Integer count = this.count(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_status", 0)
                        .eq("dic_sort", dicSort)
                        .eq("dic_value", dicValue)
        );
        if (count != null && count > 0)
            throw new RRException("该值已经存在");

        //3.生成dicKey  当前最大Key+1
        Integer dicKey = sysDictionaryDao.getLatestKey(dicSort);

        //3.创建实体类
        SysDictionaryEntity dic = new SysDictionaryEntity(vo, 1, dicKey+1, creator);

        //4.新增字典
        this.save(dic);
    }

    /**
     * 修改字典
     * 分类 dicSort不能修改
     * @param vo
     */
    @Override
    public void update(SysDictionaryVO vo) {
        //1.数据校验
        ValidatorUtils.validateEntity(vo,UpdateGroup.class);

        Long dicId = vo.getDicId();

        //2.校验 id 是否存在
        SysDictionaryEntity old = this.getById(dicId);
        if (old == null || !old.valid())
            throw new RRException("该字典信息不存在或已删除");


        String dicSort = old.getDicSort();
        String dicValue = vo.getDicValue();

        //3.校验其他 TODO
        //校验值是否已存在
        Integer count = this.count(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_status", 0)
                        .eq("dic_sort", dicSort)
                        .eq("dic_value", dicValue)
                        .ne("dic_id", dicId)
        );
        if (count != null && count > 0)
            throw new RRException("该值已经存在");

        //4.创建实体类
        SysDictionaryEntity dic = new SysDictionaryEntity(vo, 2, null, null);

        //5.修改字典
        this.updateById(dic);

    }

    /**
     * 逻辑删除字典
     * @param dicId
     */
    @Override
    public void delete(Long dicId) {
        //1.校验参数
        Assert.isNull(dicId,"字典ID不能为空");

        //2.校验 id 是否存在
        SysDictionaryEntity old = this.getById(dicId);
        if (old == null || !old.valid())
            throw new RRException("该字典信息不存在或已删除");

        //3.校验其他 TODO

        //4.逻辑删除  status = id
        this.updateById(new SysDictionaryEntity(dicId));
    }


    /**
     * 查询IP黑白名单
     * @return
     */
    @Override
    public SysDicIplistEntity getIpList() {
        //1.从Redis获取
        if (redisUtils.hasKey(Constant.REDIS_SYS_DIC_IPLIST))
            return (SysDicIplistEntity) redisUtils.getKeyValue(Constant.REDIS_SYS_DIC_IPLIST);

        //Redis中没有则查询数据库
        //2.查询字典-黑白名单
        List<SysDictionaryEntity> disList = this.list(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_sort", Constant.DIC_SORT_IPLIST)
        );

        if (disList == null || disList.size() == 0)
            return new SysDicIplistEntity();

        //3.数据处理
        String whiteList = "";
        String blackList = "";
        for (SysDictionaryEntity dic : disList) {
            String dicKey = dic.getDicKey();
            String dicValue = dic.getDicValue();
            if (Constant.DIC_KEY_WHITE_LIST.equals(dicKey))
                whiteList = dicValue;
            if (Constant.DIC_KEY_BLACK_LIST.equals(dicKey))
                blackList = dicValue;
        }

        SysDicIplistEntity iplist = new SysDicIplistEntity(whiteList, blackList);

        //4.存入Redis
        redisUtils.setKeyValue(Constant.REDIS_SYS_DIC_IPLIST, iplist);

        return iplist;
    }

    /**
     * 设置IP黑白名单
     * @param iplistVO
     */
    @Override
    public void setIpList(SysDicIplistVO iplistVO) {
        //0.校验
        ValidatorUtils.validateEntity(iplistVO);

        //1.设置
        sysDictionaryDao.setIpList(iplistVO);

        //2.清除redis
        redisUtils.delete(Constant.REDIS_SYS_DIC_IPLIST);
    }

    /**
     * 查询短信配置
     * @return
     */
    @Override
    public SysDicSmsEntity getSms() {
        //1.从Redis获取
        if (redisUtils.hasKey(Constant.REDIS_SYS_DIC_SMS))
            return (SysDicSmsEntity) redisUtils.getKeyValue(Constant.REDIS_SYS_DIC_SMS);

        //Redis中没有则查询数据库
        //2.查询字典-短信
        List<SysDictionaryEntity> disList = this.list(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_sort", Constant.DIC_SORT_SMS)
        );

        if (disList == null || disList.size() == 0)
            return new SysDicSmsEntity();

        //2.数据处理
        String aliyunAppkey = "";
        String aliyunAppserve = "";
        String aliyunSignname = "";
        String aliyunTemplatecode = "";
        String aliyunTemplatecodeMsg = "";
        for (SysDictionaryEntity dic : disList) {
            String dicKey = dic.getDicKey();
            String dicValue = dic.getDicValue();
            if (Constant.DIC_KEY_ALIYUN_APPKEY.equals(dicKey))
                aliyunAppkey = dicValue;
            if (Constant.DIC_KEY_ALIYUN_APPSERVE.equals(dicKey))
                aliyunAppserve = dicValue;
            if (Constant.DIC_KEY_ALIYUN_SIGNNAME.equals(dicKey))
                aliyunSignname = dicValue;
            if (Constant.DIC_KEY_ALIYUN_TEMPLATECODE.equals(dicKey))
                aliyunTemplatecode = dicValue;
            if (Constant.DIC_KEY_ALIYUN_TEMPLATECODE_MSG.equals(dicKey))
                aliyunTemplatecodeMsg = dicValue;
        }

        SysDicSmsEntity sms = new SysDicSmsEntity(aliyunAppkey, aliyunAppserve, aliyunSignname, aliyunTemplatecode, aliyunTemplatecodeMsg);

        //3.存入Redis
        redisUtils.setKeyValue(Constant.REDIS_SYS_DIC_SMS, sms);

        return sms;
    }

    /**
     * 设置短信
     * @param smsVO
     */
    @Override
    public void setSms(SysDicSmsVO smsVO) {
        //1.设置
        sysDictionaryDao.setSms(smsVO);

        //2.清除redis
        redisUtils.delete(Constant.REDIS_SYS_DIC_SMS);
    }

    /**
     * 校验验证码
     * @param codeKey
     * @param captcha
     */
    @Override
    public void verifySmsCode(String codeKey, String captcha) {
        //当前时间戳
        Long timestamp = System.currentTimeMillis()/1000;

        if (!redisUtils.hasKey(codeKey))
            throw new RRException("验证码已过期或失效");

        JSONObject codeObj = (JSONObject) redisUtils.getKeyValue(codeKey);
        //验证码错误次数
        Integer codeTimes = codeObj.getInteger("times");
        if (codeTimes != null && codeTimes >= 3)
            throw new RRException("验证码已失效");

        String code = codeObj.get("code").toString();
        if (!captcha.equals(code)) {
            //验证次数 错误次数++
            codeTimes = (codeTimes == null ? 1 : codeTimes+1);
            codeObj.put("times", codeTimes);
            //发送时间
            Long sendtime = codeObj.getLong("sendtime");
            //更新Redis数据
            redisUtils.deleteKey(codeKey);
            redisUtils.setKeyValue(codeKey, codeObj, (timestamp-sendtime));

            throw new RRException("验证码错误");
        }

        //验证成功后 验证码失效
        redisUtils.deleteKey(codeKey);
    }

    /**
     * 查询短信设置-赛迪云
     * @return
     */
    @Override
    public SysDicSubmailEntity getSubmail() {
        //1.从Redis获取
        if (redisUtils.hasKey(Constant.REDIS_SYS_DIC_SUBMAIL))
            return (SysDicSubmailEntity) redisUtils.getKeyValue(Constant.REDIS_SYS_DIC_SUBMAIL);

        //Redis中没有则查询数据库
        //2.查询字典-短信
        List<SysDictionaryEntity> disList = this.list(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_sort", Constant.DIC_SORT_SUBMAIL)
        );

        if (disList == null || disList.size() == 0)
            return new SysDicSubmailEntity();

        //2.数据处理
        String submailAppid = "";
        String submailAppkey = "";
        String submailProject = "";
        for (SysDictionaryEntity dic : disList) {
            String dicKey = dic.getDicKey();
            String dicValue = dic.getDicValue();
            if (Constant.DIC_KEY_SUBMAIL_APPKID.equals(dicKey))
                submailAppid = dicValue;
            if (Constant.DIC_KEY_SUBMAIL_APPKEY.equals(dicKey))
                submailAppkey = dicValue;
            if (Constant.DIC_KEY_SUBMAIL_PROJECT.equals(dicKey))
                submailProject = dicValue;
        }

        SysDicSubmailEntity submail = new SysDicSubmailEntity(submailAppid, submailAppkey, submailProject);

        //3.存入Redis
        redisUtils.setKeyValue(Constant.REDIS_SYS_DIC_SUBMAIL, submail);

        return submail;
    }

    /**
     * 设置短信-赛迪云
     * @param submailVO
     */
    @Override
    public void setSubmail(SysDicSubmailVO submailVO) {
        //1.设置
        sysDictionaryDao.setSubmail(submailVO);

        //2.清除redis
        redisUtils.delete(Constant.REDIS_SYS_DIC_SUBMAIL);
    }

    /**
     * 校验验证码-赛迪云
     * @param codeKey
     * @param captcha
     */
    @Override
    public void verifySubmailCode(String codeKey, String captcha) {
        verifySmsCode(codeKey, captcha);
    }

    /**
     * 查询企业微信设置
     * @return
     */
    @Override
    public SysDicQyweixinEntity getQyweixin() {
        //1.从Redis获取
        if (redisUtils.hasKey(Constant.REDIS_SYS_DIC_QYWEIXIN))
            return (SysDicQyweixinEntity) redisUtils.getKeyValue(Constant.REDIS_SYS_DIC_QYWEIXIN);

        //Redis中没有则查询数据库
        //2.查询字典-短信
        List<SysDictionaryEntity> disList = this.list(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_sort", Constant.DIC_SORT_QYWEIXIN)
        );

        if (disList == null || disList.size() == 0)
            return new SysDicQyweixinEntity();

        //2.数据处理
        String qyweixinCorpid = "";
        String qyweixinAgentid = "";
        String qyweixinCorsecret = "";
        for (SysDictionaryEntity dic : disList) {
            String dicKey = dic.getDicKey();
            String dicValue = dic.getDicValue();
            if (Constant.DIC_KEY_QYWEIXIN_CORPID.equals(dicKey))
                qyweixinCorpid = dicValue;
            if (Constant.DIC_KEY_QYWEIXIN_AGENTID.equals(dicKey))
                qyweixinAgentid = dicValue;
            if (Constant.DIC_KEY_QYWEIXIN_CORSECRET.equals(dicKey))
                qyweixinCorsecret = dicValue;
        }

        SysDicQyweixinEntity qyweixin = new SysDicQyweixinEntity(qyweixinCorpid, qyweixinAgentid, qyweixinCorsecret);

        //3.存入Redis
        redisUtils.setKeyValue(Constant.REDIS_SYS_DIC_QYWEIXIN, qyweixin);

        return qyweixin;
    }

    /**
     * 设置企业微信
     * @param qyweixinVO
     */
    @Override
    public void setQyweixin(SysDicQyweixinVO qyweixinVO) {
        //1.设置
        sysDictionaryDao.setQyweixin(qyweixinVO);

        //2.清除redis
        redisUtils.delete(Constant.REDIS_SYS_DIC_QYWEIXIN);

    }

    /**
     * 查询微信小程序设置
     * @return
     */
    @Override
    public SysDicWechatEntity getWechat() {
        //1.从Redis获取
        if (redisUtils.hasKey(Constant.REDIS_SYS_DIC_WECHAT))
            return (SysDicWechatEntity) redisUtils.getKeyValue(Constant.REDIS_SYS_DIC_WECHAT);

        //Redis中没有则查询数据库
        //2.查询字典-微信小程序
        List<SysDictionaryEntity> disList = this.list(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_sort", Constant.DIC_SORT_WECHAT)
        );

        if (disList == null || disList.size() == 0)
            return new SysDicWechatEntity();

        //2.数据处理
        String wechatAppid = "";
        String wechatAppsecret = "";
        for (SysDictionaryEntity dic : disList) {
            String dicKey = dic.getDicKey();
            String dicValue = dic.getDicValue();
            if (Constant.DIC_KEY_WECHAT_APPID.equals(dicKey))
                wechatAppid = dicValue;
            if (Constant.DIC_KEY_WECHAT_APPSECRET.equals(dicKey))
                wechatAppsecret = dicValue;
        }

        SysDicWechatEntity wechat = new SysDicWechatEntity(wechatAppid, wechatAppsecret);

        //3.存入Redis
        redisUtils.setKeyValue(Constant.REDIS_SYS_DIC_WECHAT, wechat);

        return wechat;
    }

    /**
     * 设置微信小程序
     * @param wechatVO
     */
    @Override
    public void setWechat(SysDicWechatVO wechatVO) {
        //1.设置
        sysDictionaryDao.setWechat(wechatVO);

        //2.清除redis
        redisUtils.delete(Constant.REDIS_SYS_DIC_WECHAT);
    }

    /**
     * 查询微阿里OSS设置
     * @return
     */
    @Override
    public SysDicAliossEntity getAlioss() {
        //1.从Redis获取
        if (redisUtils.hasKey(Constant.REDIS_SYS_DIC_ALIOSS))
            return (SysDicAliossEntity) redisUtils.getKeyValue(Constant.REDIS_SYS_DIC_ALIOSS);

        //Redis中没有则查询数据库
        //2.查询字典-阿里OSS
        List<SysDictionaryEntity> disList = this.list(
                new QueryWrapper<SysDictionaryEntity>()
                        .eq("dic_sort", Constant.DIC_SORT_ALIOSS)
        );

        if (disList == null || disList.size() == 0)
            return new SysDicAliossEntity();

        //2.数据处理
        String aliossEndpoint = "";
        String aliossAk = "";
        String aliossSk = "";
        String aliossBucketPic = "";
        String aliossBucketAudio = "";
        String aliossBucketVideo = "";
        for (SysDictionaryEntity dic : disList) {
            String dicKey = dic.getDicKey();
            String dicValue = dic.getDicValue();
            if (Constant.DIC_KEY_ALIOSS_ENDPOINT.equals(dicKey))
                aliossEndpoint = dicValue;
            if (Constant.DIC_KEY_ALIOSS_AK.equals(dicKey))
                aliossAk = dicValue;
            if (Constant.DIC_KEY_ALIOSS_SK.equals(dicKey))
                aliossSk = dicValue;
            if (Constant.DIC_KEY_ALIOSS_BUCKET_PIC.equals(dicKey))
                aliossBucketPic = dicValue;
            if (Constant.DIC_KEY_ALIOSS_BUCKET_AUDIO.equals(dicKey))
                aliossBucketAudio = dicValue;
            if (Constant.DIC_KEY_ALIOSS_BUCKET_VEDIO.equals(dicKey))
                aliossBucketVideo = dicValue;
        }

        SysDicAliossEntity alioss = new SysDicAliossEntity(aliossEndpoint, aliossAk, aliossSk, aliossBucketPic, aliossBucketAudio, aliossBucketVideo);

        //3.存入Redis
        redisUtils.setKeyValue(Constant.REDIS_SYS_DIC_ALIOSS, alioss);

        return alioss;
    }

    /**
     * 配置阿里OSS
     * @param aliossVO
     */
    @Override
    public void setAlioss(SysDicAliossVO aliossVO) {
        //1.设置
        sysDictionaryDao.setAlioss(aliossVO);

        //2.清除redis
        redisUtils.delete(Constant.REDIS_SYS_DIC_ALIOSS);
    }
}
/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.common.utils;

/**
 * 常量
 *
 * @author Mark sunlightcs@gmail.com
 */
public class Constant {
    /**
     * 开发员角色ID
     */
	public static final Long DEVELOPER_ROLEID = 1L;
    /**
     * 系统管理员角色ID
     */
    public static final Long SYSADMIN_ROLEID = 2L;
    /**
     * 开发员用户ID
     */
    public static final Long DEVELOPER_USERID = 1L;
    /**
     * 开发员角色类型
     */
    public static final Long DEVELOPER_ROLETYPE = 1L;
    /**
     * 系统管理员角色类型
     */
    public static final Long SYSADMIN_ROLETYPE = 2L;

    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";

    //文件上传临时路径
    public static final String MULTIPART_TEMP_PATH = "./res/temp";
    //授权文件路径
    public static final String MAC_DOWN_PATH = "./res/download/auth";
    //模板下载路径
    public static final String TEMP_DOWN_PATH = "./res/download/temp";
    //设备配置下载路径
    public static final String DEVCONFIG_DOWN_PATH = "./res/download/devconfig";
    //测点图片上传路径
    public static final String MENU_PIC_UPLOAD_PATH = "./res/upload/pic/menu";
    //小区图标上传路径
    public static final String WORK_PIC_UPLOAD_PATH = "./res/upload/pic/work";
    //excel下载路径
    public static final String XLS_DOWNLOAD_PATH = "./res/download/xls";

    /**
     * 系统授权用salt
     */
    public static final String AUTH_SALT = "www.inteink.com";

    /**
     * 身份证、手机号、家庭住址 加密用key
     */
    public static final String KEY = "www.inteink.com";

    /**
     * 系统字典
     */
    public static final String DIC_SORT_IPLIST = "iplist";
    public static final String DIC_KEY_WHITE_LIST = "white_list";
    public static final String DIC_KEY_BLACK_LIST = "black_list";

    public static final String DIC_SORT_SMS = "sms";
    public static final String DIC_KEY_ALIYUN_APPKEY = "aliyun_appkey";
    public static final String DIC_KEY_ALIYUN_APPSERVE = "aliyun_appserve";
    public static final String DIC_KEY_ALIYUN_SIGNNAME = "aliyun_signname";
    public static final String DIC_KEY_ALIYUN_TEMPLATECODE = "aliyun_templatecode";
    public static final String DIC_SORT_SUBMAIL = "submail";
    public static final String DIC_KEY_SUBMAIL_APPKID = "submail_appid";
    public static final String DIC_KEY_SUBMAIL_APPKEY = "submail_appkey";
    public static final String DIC_KEY_SUBMAIL_PROJECT = "submail_project";

    public static final String DIC_SORT_QYWEIXIN = "qyweixin";
    public static final String DIC_KEY_QYWEIXIN_CORPID = "qyweixin_corpid";
    public static final String DIC_KEY_QYWEIXIN_AGENTID = "qyweixin_agentid";
    public static final String DIC_KEY_QYWEIXIN_CORSECRET = "qyweixin_corsecret";
    public static final String DIC_KEY_ALIYUN_TEMPLATECODE_MSG = "aliyun_templatecode_msg";

    public static final String DIC_SORT_WECHAT = "wechat";
    public static final String DIC_KEY_WECHAT_APPID = "wechat_appid";
    public static final String DIC_KEY_WECHAT_APPSECRET = "wechat_appsecret";

    public static final String DIC_SORT_ALIOSS = "alioss";
    public static final String DIC_KEY_ALIOSS_ENDPOINT = "alioss_endpoint";
    public static final String DIC_KEY_ALIOSS_AK = "alioss_ak";
    public static final String DIC_KEY_ALIOSS_SK = "alioss_sk";
    public static final String DIC_KEY_ALIOSS_BUCKET_PIC = "alioss_bucket_pic";
    public static final String DIC_KEY_ALIOSS_BUCKET_AUDIO = "alioss_bucket_audio";
    public static final String DIC_KEY_ALIOSS_BUCKET_VEDIO = "alioss_bucket_video";

    /**
     * 系统参数
     */
    //门禁
    public static final String PARAM_WMJ_APPID = "wmj_appid";//微门禁开放平台APPID
    public static final String PARAM_WMJ_APPSECRET = "wmj_appsecret";//微门禁开放平台APPSecret

    //小程序
    public static final String PARAM_WECHAT_APPID = "wechat_appid";//微信小程序APPID
    public static final String PARAM_WECHAT_APPSECRET = "wechat_appsecret";//微信小程序APPSecret

    /**
     * Redis 相关key
     */
    //系统参数 阿里云短信参数等 List<SysParamEntity> 对象存放
    public static final String REDIS_SYS_PARAM = "redis_sys_param";
    //字典 黑白名单配置
    public static final String REDIS_SYS_DIC_IPLIST = "redis_sys_dic_iplist";
    //字典 短信配置
    public static final String REDIS_SYS_DIC_SMS = "redis_sys_dic_sms";
    //字典 短信配置-赛迪云
    public static final String REDIS_SYS_DIC_SUBMAIL = "redis_sys_dic_submail";
    //字典 企业微信配置
    public static final String REDIS_SYS_DIC_QYWEIXIN = "redis_sys_dic_qyweixin";
    //企业微信-token
    public static final String REDIS_QYWEIXIN_TOKEN = "redis_qyweixin_token";
    //字典 微信小程序配置
    public static final String REDIS_SYS_DIC_WECHAT = "redis_sys_dic_wechat";
    //微信小程序微信-token
    public static final String REDIS_WECHAT_TOKEN = "redis_wechat_token";
    //字典 阿里OSS配置
    public static final String REDIS_SYS_DIC_ALIOSS = "redis_sys_dic_alioss";


    //登录短信验证码验证  1-开启 2-关闭  默认关闭
    public static final String PARAM_CODE_SMS_VERIFY = "code_sms_verify";
    //登录企业微信验证码验证  1-开启 2-关闭  默认关闭
    public static final String PARAM_CODE_QYWEIXIN_VERIFY = "code_qyweixin_verify";
    //口令最大尝试次数，超过则限时锁定账号  0-不做限制
    public static final String PARAM_ATTEMPT_LIMIT = "attempt_limit";
    //账号限时锁定时间（单位：分钟） 默认5分钟
    public static final String PARAM_LOCK_TIME = "lock_time";
    //口令定期变更  1-强制变更 2-不强制，只提醒
    public static final String PARAM_CHANGE_FORCE = "change_force";
    //口令变更时限（单位：天） 默认 30天
    public static final String PARAM_CHANGE_LIMIT = "change_limit";

    /**
	 * 菜单类型
	 * 
	 * @author chenshun
	 * @email sunlightcs@gmail.com
	 * @date 2016年11月15日 下午1:24:29
	 */
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    /**
     * 定时任务状态
     * 
     * @author chenshun
     * @email sunlightcs@gmail.com
     * @date 2016年12月3日 上午12:07:22
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
    	NORMAL(0),
        /**
         * 暂停
         */
    	PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

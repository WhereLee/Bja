package com.inteink.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

import com.inteink.common.utils.AESUtil;
import com.inteink.common.utils.Constant;
import com.inteink.common.utils.StringUtils;
import com.inteink.modules.sys.vo.SysPasswordVO;
import com.inteink.modules.sys.vo.SysUserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;

/**
 * 
 * 
 * @author wll
 * @email 
 * @date 2020-04-22 14:30:49
 */
@ApiModel("用户对象")
@Data
@TableName("sys_user")
public class SysUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "用户ID",position = 1)
	@TableId
	private Long userId;
	/**
	 * 用户名
	 */
	@ApiModelProperty(value = "用户名称",position = 3)
	private String userName;
	/**
	 * 使用md5保存.用户从后台页面登录时候，输入的密码需要经过MD5处理，才可以传给后台
	 */
	@ApiModelProperty(value = "用户密码",position = 4)
	private String userPassword;
	/**
	 * 盐值，系统自动产生
	 */
	@ApiModelProperty(value = "盐值",position = 5)
	private String userSalt;
	/**
	 * 用户的真实名字
	 */
	@ApiModelProperty(value = "用户真实姓名",position = 6)
	private String userRealname;
	/**
	 * 邮箱
	 */
	@ApiModelProperty(value = "邮箱",position = 7)
	private String userEmail;
	/**
	 * 电话号码
	 */
	@ApiModelProperty(value = "电话号码",position = 8)
	private String userPhone;
	/**
	 * 企业微信用户ID
	 */
	@ApiModelProperty(value = "企业微信用户ID",position = 9)
	private String userQyweixinId;
	/**
	 * 最后一次登录时间戳
	 */
	@ApiModelProperty(value = "最后一次登录时间戳（秒）",position = 12)
	private Long userLogintime;
	/**
	 * 最后一次登录IP
	 */
	@ApiModelProperty(value = "最后一次登录IP",position = 13)
	private String userLoginip;
	/**
	 * 密码变更时间戳 秒  初始为第一次登录时间
	 */
	@ApiModelProperty(value = "密码变更时间戳 秒  初始为第一次登录时间",position = 13)
	private Long userPwdChangetime;
	/**
	 * 创建人 对应user表id 
	 */
	@ApiModelProperty(value = "创建人",position = 14)
	private Long userCreator;
	/**
	 * 创建时的时间戳，单位为秒
	 */
	@ApiModelProperty(value = "创建时间戳（秒）",position = 15)
	private Long userCreatetime;
	/**
	 * 更新时的时间戳，单位为秒
	 */
	@ApiModelProperty(value = "更新时间戳（秒）",position = 16)
	private Long userUpdatetime;
	/**
	 * 0-正常，1-关闭，关闭后，将不能登录系统
	 */
	@ApiModelProperty(value = "回收标志 0-正常 1-回收 回收后不能登录系统",position = 17)
	private Integer userRecycle;
	/**
	 * 0-正常，>0-删除
	 */
	@ApiModelProperty(value = "状态标志 0-正常 >0-删除",position = 18)
	//@TableLogic
	private Long userStatus;

	@ApiModelProperty(value = "角色ID列表",position = 19)
	@TableField(exist=false)
	private List<Long> roleIdList;

	@ApiModelProperty(value = "角色列表",position = 20)
	@TableField(exist=false)
	private List<SysRoleEntity> roleList;

	@ApiModelProperty(value = "创建人名称",position = 21)
	@TableField(exist=false)
	private String creatorName;

	@ApiModelProperty(value = "用户最大角色id 1-开发员角色 2-系统管理员角色 其他-其他管理员角色 null-没有角色",position = 22)
	@TableField(exist=false)
	private Long roleType;

	@ApiModelProperty(value = "用户角色名称 多个用逗号分隔",position = 23)
	@TableField(exist=false)
	private String roleNames;


	public SysUserEntity() {}

	/**
	 * 开放/关闭用户
	 * @param userId
	 * @param userRecycle
	 */
	public SysUserEntity(Long userId, Integer userRecycle) {
		this.userId = userId;
		this.userRecycle = userRecycle;
		this.userUpdatetime = System.currentTimeMillis()/1000;
	}

	//更新登录时间
	public SysUserEntity(Long userId,Long userLogintime, String userLoginip, Long userPwdChangetime){
		this.userId = userId;
		this.userLogintime = userLogintime;
		this.userLoginip = userLoginip;
		this.userPwdChangetime = userPwdChangetime;
	}

	/**
	 * 新增或修改
	 * 修改时不能修改用户类型，不能修改密码
	 * @param userVO 前端传入参数对象
	 * @param type 1-新增 2-修改
	 * @param userCreator 创建人
	 */
	public SysUserEntity(SysUserVO userVO,Integer type,Long userCreator) {
		this.userName = userVO.getUserName();
		String userRealname = userVO.getUserRealname();
		if (StringUtils.isBlank(userRealname))
			this.userRealname = "";
		if (StringUtils.isNotBlank(userRealname) && userRealname.indexOf("*") == -1)
			this.userRealname = userRealname;
		String userPhone = userVO.getUserPhone();
		if (StringUtils.isBlank(userPhone))
			this.userPhone = "";
		if (StringUtils.isNotBlank(userPhone) && userPhone.indexOf("*") == -1)
			this.userPhone = AESUtil.encrypt(userPhone, Constant.KEY);
		this.userQyweixinId = userVO.getUserQyweixinId();
		this.userEmail = userVO.getUserEmail();
		this.roleIdList = userVO.getRoleIdList();
		if (type == 1) {
			String salt = RandomStringUtils.randomAlphanumeric(20);
			this.userSalt = salt;
			this.userPassword = new Sha256Hash(userVO.getUserPassword(), salt).toHex();
			this.userCreator = userCreator;
			this.userCreatetime = System.currentTimeMillis()/1000;
			this.userUpdatetime = System.currentTimeMillis()/1000;
		} else if (type == 2) {
			this.userId = userVO.getUserId();
			this.userUpdatetime = System.currentTimeMillis()/1000;
		}
	}

	/**
	 * 重置密码
	 * @param passwordVO
	 */
	public SysUserEntity(SysPasswordVO passwordVO) {
		this.userId = passwordVO.getUserId();
		String salt = RandomStringUtils.randomAlphanumeric(20);
		this.userSalt = salt;
		this.userPassword = new Sha256Hash(passwordVO.getPassword(), salt).toHex();
		this.userUpdatetime = System.currentTimeMillis()/1000;
	}

	/**
	 * 删除-将status=id
	 * @param userId
	 */
	public SysUserEntity(Long userId) {
		this.userId = userId;
		this.userStatus = userId;
		this.userUpdatetime = System.currentTimeMillis()/1000;
	}

	/**
	 * 判断是否开放
	 * @return true：是
	 */
	public boolean open() {
		return (userRecycle != null && userRecycle == 0);
	}

	/**
	 * 判断数据是否有效，即未删除
	 * @return true：是
	 */
	public boolean valid() {
		return (userStatus != null && userStatus == 0);
	}

	/**
	 * 判断是否是开发员
	 * @return true：是
	 */
	public boolean developer() {
		//return (userType != null && userType == 0);
		return (roleType != null && roleType == 1L);
	}

	/**
	 * 判断是否是系统管理员
	 * @return true：是
	 */
	public boolean sysAdmin() {
		//return (userType != null && userType == 1);
		return (roleType != null && roleType == 2L);
	}

	/**
	 * 判断是否是其他管理员
	 * @return true：是
	 */
	public boolean otherAdmin() {
		//return (userType != null && userType == 2);
		return (roleType != null && roleType > 2L);
	}

	/**
	 * 判断是否是开发员或系统管理员
	 * @return true：是
	 */
	public boolean devOrSysAdmin() {
		//return (userType != null && (userType == 0 || userType == 1));
		return (roleType != null && (roleType == 1L || roleType == 2L));
	}
}

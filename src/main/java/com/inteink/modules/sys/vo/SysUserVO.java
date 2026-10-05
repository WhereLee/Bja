package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@ApiModel("用户VO")
@Data
public class SysUserVO {
    @ApiModelProperty(value = "用户ID",position = 1)
    @NotNull(message = "用户主键不能为空",groups = {UpdateGroup.class})
    private Long userId;        //用户ID

    @ApiModelProperty(value = "用户名称",position = 3)
    @Size(max = 64,message = "用户名过长",groups = {AddGroup.class,UpdateGroup.class})
    @NotBlank(message="用户名不能为空",groups = {AddGroup.class})
    private String userName;    //用户名

    public void setUserName(String userName) {
        this.userName = StringUtils.replaceBlank(userName);
    }

    @ApiModelProperty(value = "用户密码",position = 4)
    @NotBlank(message="密码不能为空",groups = {AddGroup.class})
    private String userPassword;    //密码

    @ApiModelProperty(value = "用户真实姓名",position = 5)
    @Size(max = 64,message = "用户真实姓名过长",groups = {AddGroup.class,UpdateGroup.class})
    private String userRealname;    //用户真实姓名

    public void setUserRealname(String userRealname) {
        this.userRealname = StringUtils.replaceBlank(userRealname);
    }

    @ApiModelProperty(value = "电话号码",position = 6)
    @Size(max = 256,message = "电话号码过长",groups = {AddGroup.class,UpdateGroup.class})
    private String userPhone;       //电话号码

    public void setUserPhone(String userPhone) {
        this.userPhone = StringUtils.replaceBlank(userPhone);
    }

    @ApiModelProperty(value = "企业微信用户ID",position = 6)
    private String userQyweixinId;

    public void setUserQyweixinId(String userQyweixinId) {
        this.userQyweixinId = StringUtils.replaceBlank(userQyweixinId);
    }

    @ApiModelProperty(value = "邮箱",position = 7)
    @Size(max = 128,message = "邮箱过长",groups = {AddGroup.class,UpdateGroup.class})
    @Email(message="邮箱格式不正确", groups = {AddGroup.class, UpdateGroup.class})
    private String userEmail;       //邮箱

    public void setUserEmail(String userEmail) {
        this.userEmail = StringUtils.replaceBlank(userEmail);
    }

    @ApiModelProperty(value = "角色ID列表",position = 12)
    private List<Long> roleIdList;      //角色列表
}

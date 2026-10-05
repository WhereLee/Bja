package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@ApiModel("角色VO")
@Data
public class SysRoleVO {
    @ApiModelProperty(value = "角色ID",position = 1)
    @NotNull(message = "角色主键不能为空",groups = {UpdateGroup.class})
    private Long roleId;        //角色ID

    @ApiModelProperty(value = "角色名称",position = 3)
    @Size(max = 64,message = "角色名称过长",groups = {AddGroup.class,UpdateGroup.class})
    @NotBlank(message="角色名称不能为空",groups = {AddGroup.class})
    private String roleName;    //角色名称

    public void setRoleName(String roleName) {
        this.roleName = StringUtils.replaceBlank(roleName);
    }

    @ApiModelProperty(value = "角色描述",position = 4)
    @Size(max = 256,message = "角色描述过长",groups = {AddGroup.class,UpdateGroup.class})
    private String roleComment;     //角色描述，备注

    public void setRoleComment(String roleComment) {
        this.roleComment = StringUtils.replaceBlank(roleComment);
    }

    @ApiModelProperty(value = "菜单ID列表",position = 5)
    private List<Long> menuIdList;      //菜单ID列表
}

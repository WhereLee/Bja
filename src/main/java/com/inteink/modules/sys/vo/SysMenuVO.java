package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

@ApiModel("菜单VO")
@Data
public class SysMenuVO {
    @ApiModelProperty(value = "菜单ID",position = 1)
    @NotNull(message = "菜单主键不能为空",groups = {UpdateGroup.class})
    private Long menuId;

    @ApiModelProperty(value = "菜单类型 0-目录 1-菜单 2-按钮",position = 20)
    @Min(value = 0,message = "菜单类型必须0-2之间",groups = {AddGroup.class,UpdateGroup.class})
    @Max(value = 2,message = "菜单类型必须0-2之间",groups = {AddGroup.class,UpdateGroup.class})
    @NotNull(message="菜单类型不能为空",groups = {AddGroup.class})
    private Integer menuType;

    @ApiModelProperty(value = "菜单名称",position = 30)
    @Size(max = 64,message = "菜单名称过长",groups = {AddGroup.class,UpdateGroup.class})
    @NotBlank(message="菜单名称不能为空",groups = {AddGroup.class})
    private String menuName;

    public void setMenuName(String menuName) {
        this.menuName = StringUtils.replaceBlank(menuName);
    }

    @ApiModelProperty(value = "菜单授权 英文逗号隔开",example = "user:list,user:create",position = 40)
    @Size(max = 256,message = "菜单授权过长",groups = {AddGroup.class,UpdateGroup.class})
    private String menuPerms;   //授权(多个用逗号分隔，如：user:list,user:create)

    public void setMenuPerms(String menuPerms) {
        this.menuPerms = StringUtils.replaceBlank(menuPerms);
    }

    @ApiModelProperty(value = "菜单/按钮图标",position = 50)
    @Size(max = 512,message = "菜单/按钮图标过长",groups = {AddGroup.class,UpdateGroup.class})
    private String menuIcon;    //菜单/按钮的图标

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = StringUtils.replaceBlank(menuIcon);
    }

    @ApiModelProperty(value = "菜单图片地址",position = 63)
    @Size(max = 512,message = "菜单图片地址过长",groups = {AddGroup.class,UpdateGroup.class})
    private String menuPic;

    public void setMenuPic(String menuPic) {
        this.menuPic = StringUtils.replaceBlank(menuPic);
    }

    @ApiModelProperty(value = "菜单默认图片地址",position = 64)
    @Size(max = 512,message = "菜单默认图片地址标过长",groups = {AddGroup.class,UpdateGroup.class})
    private String menuDefPic;

    public void setMenuDefPic(String menuDefPic) {
        this.menuDefPic = StringUtils.replaceBlank(menuDefPic);
    }

    @ApiModelProperty(value = "菜单URL-显示到地址栏",position = 60)
    @Size(max = 512,message = "菜单URL过长",groups = {AddGroup.class,UpdateGroup.class})
    private String menuUrl;     //菜单URL

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = StringUtils.replaceBlank(menuUrl);
    }

    @ApiModelProperty(value = "页面路径-实际使用的访问地址",position = 65)
    @Size(max = 512,message = "页面路径过长",groups = {AddGroup.class,UpdateGroup.class})
    private String menuPage;

    public void setMenuPage(String menuPage) {
        this.menuPage = StringUtils.replaceBlank(menuPage);
    }

    @ApiModelProperty(value = "父级ID，没有，则0",position = 70)
    //@NotNull(message = "父级ID不能为空",groups = {AddGroup.class})
    private Long menuFid;

    @ApiModelProperty(value = "菜单排序",position = 80)
    private Integer menuOrdernum;   //菜单的排序

}

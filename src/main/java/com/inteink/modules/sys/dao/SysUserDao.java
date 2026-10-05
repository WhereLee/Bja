package com.inteink.modules.sys.dao;

import com.inteink.modules.sys.entity.SysUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * 
 * @author wll
 * @email 
 * @date 2020-04-22 14:30:49
 */
@Repository
public interface SysUserDao extends BaseMapper<SysUserEntity> {
    /**
     * 根据用户名，查询系统用户
     * @param loginname
     * @return
     */
    SysUserEntity getUserByLoginname(String loginname);

    /**
     * 查询用户自己创建的用户ID列表
     * @param userId
     * @return
     */
    List<Long> queryUserIdByCreator(Long userId);
}

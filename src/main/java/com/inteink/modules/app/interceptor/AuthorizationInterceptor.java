/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.modules.app.interceptor;


import com.inteink.modules.app.annotation.Login;
import io.jsonwebtoken.Claims;
import com.inteink.common.exception.RRException;
import com.inteink.modules.app.utils.JwtUtils;
import com.inteink.modules.app.annotation.Login;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    //@Autowired
    //private JwtUtils jwtUtils;

    //public static final String USER_KEY = "userId";
    public static final String OPENID = "openid";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login annotation;
        if(handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        }else{
            return true;
        }

        if(annotation == null){
            return true;
        }

        //获取用户凭证
        String token = request.getHeader("token");
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }

        //凭证为空
        if(StringUtils.isBlank(token)){
            throw new RRException("无效 token", HttpStatus.UNAUTHORIZED.value());
        }

        //设置openid到request里，后续根据openid，获取用户信息
        request.setAttribute(OPENID, token);

        return true;
    }
}

package com.inteink.modules.app.resolver;

import com.inteink.common.exception.RRException;
import com.inteink.modules.app.annotation.LoginUser;
import com.inteink.modules.app.interceptor.AuthorizationInterceptor;
import com.inteink.modules.sys.dao.SysRoleDao;
import com.inteink.modules.sys.entity.SysUserEntity;
import com.inteink.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户(小程序用户)
 */
@Component
public class WechatUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleDao sysRoleDao;

    /**
     * 用于判定是否需要处理该参数分解，返回true为需要，并会去调用下面的方法resolveArgument
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(SysUserEntity.class) && parameter.hasParameterAnnotation(LoginUser.class);
    }

    /**
     * 真正用于处理参数分解的方法，返回的Object就是controller方法上的形参对象。
     * @param parameter
     * @param container
     * @param request
     * @param factory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container
            , NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
        //获取用户ID
        Object object = request.getAttribute(AuthorizationInterceptor.OPENID, RequestAttributes.SCOPE_REQUEST);
        if(object == null){
            throw new RRException("无效 token!", HttpStatus.UNAUTHORIZED.value());
        }

        //获取用户信息
        //TODO
        return null;
    }
}

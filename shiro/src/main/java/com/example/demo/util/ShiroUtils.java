package com.example.demo.util;

import com.example.demo.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisSessionDAO;

import java.util.Collection;
import java.util.Objects;

public class ShiroUtils {
    private ShiroUtils(){}
    private static RedisSessionDAO redisSessionDAO = SpringUtil.getBean(RedisSessionDAO.class);

    /**
     * 获取当前用户Session
     * @return
     */
    public static Session getSession(){
        return SecurityUtils.getSubject().getSession();
    }

    /**
     * 用户退出
     */
    public static void logout(){
        SecurityUtils.getSubject().logout();
    }

    public static void deleteCache(String username,boolean isRemoveSession){
        // 从缓存中获取Session
        Session session = null;
        Collection<Session> sessions = redisSessionDAO.getActiveSessions();
        User user;
        Object attribute = null;
        for (Session sessionInfo : sessions) {
            // 遍历Session，找到该用户名对应的session
            attribute = sessionInfo.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (attribute == null) {
                continue;
            }
            user = (User) ((SimplePrincipalCollection)attribute).getPrimaryPrincipal();

            if (user == null) {
                continue;
            }
            if (Objects.equals(user.getUserName(), username)) {
                session = sessionInfo;
                break;
            }
        }

        if (session == null||attribute == null) {
            return;
        }

        //删除session
        if (isRemoveSession) {
            redisSessionDAO.delete(session);
        }

        // 删除Cache，在访问受限接口是会重新授权
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        Authenticator authenticator = securityManager.getAuthenticator();
        ((LogoutAware)authenticator).onLogout((SimplePrincipalCollection)attribute);
    }
}

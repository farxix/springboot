package com.example.demo.shiro;

import com.example.demo.entity.Permissions;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.ILoginService;
import com.example.demo.util.ShiroUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private ILoginService loginService;

    /**
     * 授权
     * 用户进行权限验证时候shiro会去缓存中找，如果查不到数据，就会执行这个方法去查权限，并放入缓存中
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取登录用户名
        //principal参数可以是uuid ，
        // 据库主键，LDAP UUID或静态DN 或者是用户唯一的用户名。
        // 所以说这个值 必须唯一，你可以选择 邮箱，或者 手机号，身份证号等等。

        User userName =  (User) principalCollection.getPrimaryPrincipal();

        // 根据用户名去数据库查询用户信息
        User user = loginService.getUserByName(userName.getUserName());

        // 添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (Role role : user.getRoleSet()) {
            // 添加角色
            simpleAuthorizationInfo.addRole(role.getRoleName());
            // 添加权限
            for (Permissions permissions : role.getPermissions()) {
                simpleAuthorizationInfo.addStringPermission(permissions.getPermissionsName());
            }
        }
        return simpleAuthorizationInfo;
    }

    /**
     * 身份验证，认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 在Post请求的时候回先进行认证，然后再到请求
        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        // 获取用户信息
        String name = (String) authenticationToken.getPrincipal();
        User user = loginService.getUserByName(name);
        System.out.println("user:========="+user.toString());
        if (user == null) {
            throw new AuthenticationException();
        }

        // 进行验证
        //事实上,Shiro往Redis存Session信息的时候是在调用subject.login(token);方法执行的,
        // 所以当用户认证成功之后,说明用户具有登录的资格,这时候才会清除session进行踢人功能的实现.
        // 类似两个账号登录，会强制下线一个账号。
//        ShiroUtils.deleteCache(name,true);


//        原先这里SimpleAuthenticationInfo构造的时候传入的是username，而redis做缓存是需要key，value的，这里必须要传入user，获取id做key.


        return new SimpleAuthenticationInfo(user,user.getPassword(), ByteSource.Util.bytes(user.getSalt()),getName());

        // 验证成功开始踢人(清除缓存和Session)
//        ShiroUtils.deleteCache(name,true);
//        return simpleAuthenticationInfo;
    }

    /**
     * 清除指定用户授权缓存
     * @param principals
     */
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("清除指定用户授权缓存=================clearCachedAuthorizationInfo");
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 登出
     * 清除认证信息
     * @param principals
     */
    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        System.out.println("清除认证信息=================clearCachedAuthenticationInfo");
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }
}

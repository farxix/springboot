package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.util.ShiroUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @RequestMapping(value = "/login")
    public Map<String,Object> login(User user) {
        Map<String,Object> map = new HashMap<>();

        try {
            // 添加用户认证信息
            // 当你使用SecurityUtils.getSubject().getPrincipal(); 是从session中获取的信息
            Subject subject = SecurityUtils.getSubject();

            Subject currentUser = SecurityUtils.getSubject();
            if (null != currentUser && currentUser.isAuthenticated()) {
                currentUser.logout();
            }

            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
                    user.getUserName(), user.getPassword()
            );
            // 进行验证，这里可以捕获异常，然后返回对应信息
            subject.login(usernamePasswordToken);
            boolean authent = currentUser.isAuthenticated();
            System.out.println("authent======================"+authent);

//            subject.checkPermission("admin");
//            subject.checkPermissions("query","add");
        }catch (IncorrectCredentialsException e) {
            map.put("code",500);
            map.put("msg","用户不存在或者密码错误");
            return map;
        } catch (LockedAccountException e) {
            map.put("code",500);
            map.put("msg","登录失败，该用户已被冻结");
            return map;
        } catch (AuthenticationException e) {
            map.put("code",500);
            map.put("msg","该用户不存在");
            e.printStackTrace();
            return map;
        } catch (AuthorizationException e) {
            map.put("code",500);
            map.put("msg","没有权限");
            return map;
        } catch (Exception e) {
        map.put("code",500);
        map.put("msg","未知异常");
        return map;
    }
        map.put("code",0);
        map.put("msg","登录成功");
        map.put("token", ShiroUtils.getSession().getId().toString());
        return map;
    }

    /**
     * 登出
     * @Author Sans
     * @CreateTime 2019/6/19 10:38
     * @Return Map<String,Object> 返回结果
     */
    @RequestMapping("/getLogout")
    @RequiresUser
    public Map<String,Object> getLogout(){
        //登出Shiro会帮我们清理掉Session和Cache
        ShiroUtils.logout();
        Map<String,Object> map = new HashMap<>();
        map.put("code",200);
        map.put("msg","登出");
        return map;
    }

//    当你访问一个url的时候,会调用ShiroRealm的isPermitted(PrincipalCollection principals, String permission);
//    方法判断用户是否有这个权限。如果没有就会抛出异常。

    // 注解验证角色和权限
    @RequiresRoles("admin")
    @RequiresPermissions("add")
    @RequestMapping("/index")
    public String index() {
        System.out.println("index......");
        return "index!";
    }

    /**
     * 未登录
     * @Author Sans
     * @CreateTime 2019/6/20 9:22
     */
    @RequestMapping("/unauth")
    public Map<String,Object> unauth(){
        Map<String,Object> map = new HashMap<>();
        map.put("code",500);
        map.put("msg","未登录");
        return map;
    }

}

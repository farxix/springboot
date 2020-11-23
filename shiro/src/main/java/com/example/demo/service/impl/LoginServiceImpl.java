package com.example.demo.service.impl;

import com.example.demo.entity.Permissions;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.ILoginService;
import com.example.demo.util.SHA256Util;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class LoginServiceImpl implements ILoginService {
    @Override
    public User getUserByName(String userName) {
        return getMapByName(userName);
    }

    /**
     * 模拟数据库查询
     * @param userName
     * @return
     */
    private User getMapByName(String userName) {
        Map<String,User> map = new HashMap<>();
        // 共添加两个用户，都是admin角色
        // zhangsan有query和add权限，lisi只有query权限
        Permissions permissions1 = new Permissions("1","query");
        Permissions permissions2 = new Permissions("2","add");
        Set<Permissions> permissionsSet = new HashSet<>();
        permissionsSet.add(permissions1);
        permissionsSet.add(permissions2);

        Role role = new Role("1","admin",permissionsSet);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        User user = new User("1","zhangsan","123456",roleSet);

        // 随机生成盐值
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        // 进行加密
        String password ="123456";
        user.setPassword(SHA256Util.sha256(password, user.getSalt()));

        map.put(user.getUserName(),user);

        Permissions permissions3 = new Permissions("3","query");
        Set<Permissions> permissionsSet1 = new HashSet<>();
        permissionsSet1.add(permissions3);

        Role role1 = new Role("2","admin",permissionsSet1);
        Set<Role> roleSet1 = new HashSet<>();
        roleSet1.add(role1);

        User user1 = new User("2","lisi","123456",roleSet1);
        user1.setSalt(salt);
        String password1 ="123456";
        user1.setPassword(SHA256Util.sha256(password1, user1.getSalt()));

        map.put(user1.getUserName(),user1);

        return map.get(userName);
    }
}

package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class User implements Serializable {
    private String id;
    private String userName;
    private String password;
    /**
     * 盐值
     */
    private String salt;

    /**
     * 用户对应的角色集合
     */
    private Set<Role> roleSet;


    public User(String id, String userName, String password, Set<Role> roleSet) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.roleSet = roleSet;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Set<Role> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<Role> roleSet) {
        this.roleSet = roleSet;
    }
}

package com.example.demo.service;

import com.example.demo.entity.User;

public interface ILoginService {
    User getUserByName(String userName);
}

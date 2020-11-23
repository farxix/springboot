# 参考地址

```bash
https://juejin.cn/post/6844903872171868174
```

# 1、登录请求
```bash
http://localhost:8080/login?userName=zhangsan&password=123456
```
# 返回结果
```bash
{
    "msg": "登录成功",
    "code": 0,
    "token": "login_token_2a8ce313-6893-4e5c-934b-1fc85bfa7252"
}
```

# 2、根据返回的token，进行请求/index
```bash
Request Headers {
    Authorization: login_token_2a8ce313-6893-4e5c-934b-1fc85bfa7252
}

http://localhost:8080/index
```
# 返回结果
```bash
index!
```
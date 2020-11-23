package com.example.demo.shiro;

import com.example.demo.util.SHA256Util;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义配置类
 */
@Configuration
public class shiroConfig {

    private final String CACHE_KEY = "shiro:cache:s";
    private final String SESSION_KEY = "shiro:session:";
    //Redis配置
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * Filter工厂，设置对应的过滤条件和跳转条件
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 登录
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 首页
        shiroFilterFactoryBean.setSuccessUrl("/index");
        // 错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");

//        FilterRegistrationBean registration = new FilterRegistrationBean(authcFilter);
//        registration.setEnabled(false);
        Map<String, String> map = new HashMap<>();
        // 退出
        map.put("/logout", "logout");
        // 对所有用户认证，authc授权控制
        map.put("/**", "authc");
        // 登录
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 自定义Session管理
        securityManager.setSessionManager(sessionManager());
        // 自定义Cache实现
        securityManager.setCacheManager(cacheManager());

        // 自定义Realm验证
        securityManager.setRealm(myShiroRealm());
        return securityManager;
    }

    /**
     * 配置Cache管理器
     * 用于往Redis存储权限和角色标识
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager() {
        System.out.println("配置Cache管理器========================");
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setKeyPrefix(CACHE_KEY);
        // 配置缓存的话要求放在session里面的实体类必须有个id标识
        redisCacheManager.setPrincipalIdFieldName("username");
        return redisCacheManager;
    }

    /**
     * 将自己的验证方式加入容器
     * @return
     */
    @Bean
    public CustomRealm myShiroRealm() {
        CustomRealm customRealm = new CustomRealm();
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return customRealm;
    }

    /**
     * 凭证匹配器
     * 将密码校验交给shiro的SimpleAuthenticationInfo进行处理，在这里做匹配配置
     * @return
     */
    @Bean
    public CredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法：这里使用SHA256算法
        hashedCredentialsMatcher.setHashAlgorithmName(SHA256Util.HASH_ALGORITHM_NAME);
        // 散列的次数，比如散列两次，相当于md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(SHA256Util.HASH_ITERATIONS);
        return hashedCredentialsMatcher;
    }

    /**
     * 配置Session管理器
     * @return
     */
    @Bean
    public SessionManager sessionManager(){
        ShiroSessionManger shiroSessionManger = new ShiroSessionManger();
        shiroSessionManger.setSessionDAO(redisSessionDAO());
        shiroSessionManger.setDeleteInvalidSessions(true);
        return shiroSessionManger;
    }

    @Bean
    public SessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        redisSessionDAO.setKeyPrefix(SESSION_KEY);
        redisSessionDAO.setExpire(timeout);
        return redisSessionDAO;
    }

    @Bean
    public IRedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setTimeout(timeout);
//        redisManager.setPassword(password);
        return redisManager;
    }

    /**
     * SessionID生成器
     * @return
     */
    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return new ShiroSessionIdGenerator();
    }

}

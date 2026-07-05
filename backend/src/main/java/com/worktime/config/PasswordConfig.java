package com.worktime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 密码配置类：统一提供密码加密工具。
@Configuration
public class PasswordConfig {

    // BCrypt 是常见的密码哈希算法，适合保存登录密码。
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

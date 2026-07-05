package com.worktime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Web 配置类：放置和后端 Web 接口相关的全局配置。
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 登录拦截器。
    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    // 配置跨域访问，解决 Apifox 网页版或后续 Vue 前端调用后端时的 CORS 问题。
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 开发阶段允许任意来源访问后端接口，方便 Apifox 和本地前端调试。
                .allowedOriginPatterns("*")
                // 允许常见 HTTP 方法。
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许请求携带任意请求头。
                .allowedHeaders("*")
                // 当前阶段暂不携带 Cookie，避免和通配来源产生冲突。
                .allowCredentials(false)
                // 预检请求缓存 1 小时，减少浏览器重复发送 OPTIONS 请求。
                .maxAge(3600);
    }

    // 注册登录拦截器，除登录接口和健康检查接口外，其余 /api/** 接口都需要 token。
    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/health/**"
                );
    }
}

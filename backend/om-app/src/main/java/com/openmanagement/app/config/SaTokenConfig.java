package com.openmanagement.app.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.openmanagement.common.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Sa-Token auth interceptor
        registry.addInterceptor(new SaInterceptor(handler -> {
            SaRouter.match("/**")
                    .notMatch("/api/auth/login", "/api/auth/captcha",
                              "/swagger-ui/**", "/v3/api-docs/**", "/doc.html",
                              "/webjars/**", "/actuator/**")
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");

        // User context interceptor — populates UserContext from the current Sa-Token session
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                if (StpUtil.isLogin()) {
                    Object loginId = StpUtil.getLoginId();
                    if (loginId != null) {
                        try {
                            UserContext.setUserId(Long.parseLong(loginId.toString()));
                        } catch (NumberFormatException ignored) {}
                    }
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                UserContext.clear();
            }
        }).addPathPatterns("/**");
    }
}

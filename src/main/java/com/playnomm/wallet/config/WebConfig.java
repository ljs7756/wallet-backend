package com.playnomm.wallet.config;

import com.playnomm.wallet.config.interceptor.AuthorityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : hzn
 * @date : 2023/02/23
 * @description :
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	public void addInterceptors (InterceptorRegistry registry) {
		registry.addInterceptor (new AuthorityInterceptor ()).excludePathPatterns ("/swagger*/**", "/v3/api-docs/**", "/api/v1/auth/login/**", "/api/v1/auth/logout/**", "/images/**", "/", "/error")
				.addPathPatterns ("/**");
	}
}

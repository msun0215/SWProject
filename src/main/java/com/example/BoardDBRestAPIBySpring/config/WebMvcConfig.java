package com.example.BoardDBRestAPIBySpring.config;

import com.example.BoardDBRestAPIBySpring.config.jwt.JwtTokenIntercepter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Log4j2
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry){
        MustacheViewResolver resolver=new MustacheViewResolver();
        resolver.setCharset("UTF-8");
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");

        registry.viewResolver(resolver);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 작성한 인터셉터 추가
        registry.addInterceptor(jwtTokenInterceptor())
                // 전체 사용자 조회하는 /user/findAll에 대해 토큰 검사 진행
                .addPathPatterns("/user/findAll");
    }

    @Bean
    public JwtTokenIntercepter jwtTokenInterceptor(){
        return new JwtTokenIntercepter();
    }
}

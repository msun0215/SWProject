package com.example.BoardDBRestAPIBySpring.config;

import com.example.BoardDBRestAPIBySpring.config.jwt.JWTProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.logging.Filter;

@Log4j2
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        CorsConfiguration config=new CorsConfiguration();
        config.setAllowCredentials(true);   // 내 서버가 응답할 때 json을 javascript에서 처리할 수 있게 할지를 설정
        config.addAllowedOriginPattern("*");    // 포트번호 응답 다름 허용
        config.addAllowedOrigin("*");   // 모든 ip에 응답을 허용함
        config.addAllowedHeader("*");   // 모든 header에 응답을 허용함
        config.addAllowedMethod("*");   // 모든 post, get, put, delete, patch 등의 Method 요청을 허용함
        //config.addExposedHeader(JWTProperties.HEADER_STRING);
        config.addExposedHeader("Authorization");   // CORS로 인해 프론트단에서 인식하지 못하는 Authorization Header를 노출
        config.addExposedHeader(JWTProperties.REFRESH_HEADER);

        //source.registerCorsConfiguration("/api/**", config);    // /api/**로 들어오는 url에 대해서는 config대로 정의함
        source.registerCorsConfiguration("/**", config);    // /**로 들어오는 url에 대해서는 config대로 정의함
        System.out.println("Cors Filter 적용 완료");
        return new CorsFilter(source);
    }
}

package com.example.BoardDBRestAPIBySpring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebConfig {

	// cors 설정 참조
	//	https://velog.io/@juhyeon1114/Spring-security%EC%97%90%EC%84%9C-CORS%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0
	//@Override
	//public void addCorsMappings(final CorsRegistry registry) {
		//registry.addMapping("/**");
	//}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.csrf(cs->cs.disable());
//		http.authorizeHttpRequests(authorize ->
//				authorize
//						.requestMatchers("/user/**").hasAnyRole(1,2,3)    // 인증만 되면 들어갈 수 있는 주소
//						.requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
//						.requestMatchers("/admin/**").hasAnyRole("ADMIN")
//
//						.anyRequest().permitAll()	// 그 외에는 모두 혀용
//		)
		http.authorizeHttpRequests(a->
				a.anyRequest().permitAll()
		).formLogin(formLogin->{
			formLogin.loginPage("/loginForm")
					.loginProcessingUrl("/login")
					.defaultSuccessUrl("/");
		});
		return http.build();
	}
}

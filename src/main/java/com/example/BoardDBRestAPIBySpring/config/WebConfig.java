package com.example.BoardDBRestAPIBySpring.config;

import com.example.BoardDBRestAPIBySpring.config.jwt.*;
import com.example.BoardDBRestAPIBySpring.controller.handler.CustomAuthFailureHandler;
import com.example.BoardDBRestAPIBySpring.controller.handler.CustomLoginSuccessHandler;
import com.example.BoardDBRestAPIBySpring.controller.handler.JwtAccessDeniedHandler;
import com.example.BoardDBRestAPIBySpring.controller.handler.JwtAuthenticationEntryPoint;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Log4j2
@Configuration
@EnableWebSecurity	// Spring Security 설정
@EnableGlobalMethodSecurity(securedEnabled = true)	// @Secured 어노테이션으로 권한 설정
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:8080", exposedHeaders = JWTProperties.HEADER_STRING)
public class WebConfig {

	// cors 설정 참조
	//	https://velog.io/@juhyeon1114/Spring-security%EC%97%90%EC%84%9C-CORS%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0
	//@Override
	//public void addCorsMappings(final CorsRegistry registry) {
		//registry.addMapping("/**");
	//}
	private final CustomAuthFailureHandler customAuthFailureHandler;
	private final AuthenticationFailureHandler customFailureHandler;

	private final TokenUtils tokenUtils;
	@Autowired
	private final AuthenticationConfiguration authenticationConfiguration;

	private final CorsConfig corsConfig;
	private final CorsFilter corsFilter;
	private final JWTTokenProvider jwtTokenProvider;

	@Autowired
	private final MemberRepository memberRepository;

	@Bean
	public CustomAuthenticationProvider mycustomAuthenticationProvider(){
		return new CustomAuthenticationProvider();
	}

	@Bean
	public CustomLoginSuccessHandler myCustomLoginSuccessHandler(TokenUtils tokenUtils){return new CustomLoginSuccessHandler(tokenUtils);}

	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}

	// ACL(Access Control List, 접근 제어 목록)의 예외 URL 설정
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer(){
		return (web)->web.ignoring()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.csrf(cs-> cs.disable())
				.sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))		// 세션에 저장을 하지 않는다
				.formLogin(f->f.disable())
				.httpBasic(h->h.disable())
				.addFilterBefore(new CustomAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		http.authorizeRequests(authorize-> {     // 권한 부여
			// authorizeRequests가 deprecated됨에 따라 authorizeHttpRequests 사용 권장
			authorize
					// 회원가입
					.requestMatchers(HttpMethod.POST,"/join/**").permitAll()

					// 로그인
					.requestMatchers(HttpMethod.POST,"/login/**").permitAll()

					// 로그아웃
					.requestMatchers(HttpMethod.POST,"/logout/**").hasAnyRole("USER","MANAGER","ADMIN")

					// 회원
					.requestMatchers(HttpMethod.GET,"/users/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT, "/users/**").hasAnyRole("USER","MANAGER","ADMIN")
					.requestMatchers(HttpMethod.DELETE,"/users/**").hasAnyRole("USER","MANAGER","ADMIN")

					// 게시글
					.requestMatchers(HttpMethod.GET,"/boards/**").permitAll()
					.requestMatchers(HttpMethod.GET,"/boards/{boardId}/**").permitAll()
					.requestMatchers(HttpMethod.POST,"/boards/**").hasAnyRole("USER","MANAGER","ADMIN")
					.requestMatchers(HttpMethod.PUT,"/boards/{boardId}/**").hasAnyRole("USER","MANAGER","ADMIN")
					.requestMatchers(HttpMethod.DELETE,"/boards/{boardId}/**").hasAnyRole("USER","MANAGER","ADMIN")
					.requestMatchers(HttpMethod.POST,"/boards/users/roles/**").hasRole("USER")

					// 댓글
					.requestMatchers(HttpMethod.GET,"/boards/{boardId}/replies/**").permitAll()
					.requestMatchers(HttpMethod.POST,"/boards/{boardId}/replies/**").hasAnyRole("USER","MANAGER","ADMIN")
					.requestMatchers(HttpMethod.PUT,"/boards/{boardId}/replies/{repliesId}/**").hasAnyRole("USER","MANAGER","ADMIN")
					.requestMatchers(HttpMethod.DELETE,"/boards/{boardId}/replies/{repliesId}/**").hasAnyRole("USER","MANAGER","ADMIN")

//					.requestMatchers("/manager/**").hasAnyRole("MANAGER","ADMIN")
//					.requestMatchers("/admin/**").hasAnyRole("ADMIN")
					// hasAnyRole() 메소드는 자동으로 앞에 ROLE_을 추가해서 체크해준다
					.anyRequest().permitAll();  // 이외의 요청은 모두 허용함
		});
		//		.logout(logout->logout.logoutSuccessUrl("/"))

		// 예외 처리
		http.exceptionHandling().accessDeniedHandler(new JwtAccessDeniedHandler());
		http.exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint());

		http.headers().frameOptions().sameOrigin();

		// /user, /manager, /admin으로 들어가도 /loginForm으로 접근하도록
		return http.build();
	}
}

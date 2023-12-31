package com.example.BoardDBRestAPIBySpring.config;

import com.example.BoardDBRestAPIBySpring.config.jwt.CustomAuthenticationFilter;
import com.example.BoardDBRestAPIBySpring.config.jwt.CustomAuthenticationProvider;
import com.example.BoardDBRestAPIBySpring.config.jwt.JWTTokenProvider;
import com.example.BoardDBRestAPIBySpring.config.jwt.TokenUtils;
import com.example.BoardDBRestAPIBySpring.controller.handler.CustomAuthFailureHandler;
import com.example.BoardDBRestAPIBySpring.controller.handler.CustomLoginSuccessHandler;
import com.example.BoardDBRestAPIBySpring.controller.handler.JwtAccessDeniedHandler;
import com.example.BoardDBRestAPIBySpring.controller.handler.JwtAuthenticationEntryPoint;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

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
				//.apply(new MyCustomDs1());	// CustomFilter
						//.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		//.authorizeHttpRequests(authorize->{
		//.addFilter(new JWTAuthenticationFilter(authenticationManager))
		//.addFilter(new JWTAuthorizationFilter(authenticationManager, userRepository))
		http.authorizeRequests(authorize-> {     // 권한 부여
			// authorizeRequests가 deprecated됨에 따라 authorizeHttpRequests 사용 권장
			authorize
//                  .requestMatchers("/user/**").hasAnyRole("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//                  .requestMatchers("/manager/**").hasAnyRole("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//                  .requestMatchers("/admin/**").hasAnyRole("hasRole('ROLE_ADMIN')")

//					.requestMatchers(new AntPathRequestMatcher("/login/**")).authenticated()
//					.requestMatchers(new AntPathRequestMatcher("/login/**")).hasAnyRole("USER","MANAGER","ADMIN")
					.requestMatchers(new AntPathRequestMatcher("/user/**")).hasAnyRole("USER","MANAGER","ADMIN")
//					.requestMatchers("/manager/**").hasAnyRole("MANAGER","ADMIN")
//					.requestMatchers("/admin/**").hasAnyRole("ADMIN")
					// hasAnyRole() 메소드는 자동으로 앞에 ROLE_을 추가해서 체크해준다
					//.requestMatchers(new AntPathRequestMatcher("/**")).authenticated()
					.anyRequest().permitAll();  // 이외의 요청은 모두 허용함
		});
		//		.logout(logout->logout.logoutSuccessUrl("/"))

		// 예외 처리
		http.exceptionHandling().accessDeniedHandler(new JwtAccessDeniedHandler());
		http.exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint());

		http.headers().frameOptions().sameOrigin();


//		http.logout()
//				.logoutUrl("/logout")	// 로그인과 마찬가지로 POST 요청이 와야 함
//				.addLogoutHandler(((request, response, authentication) -> {
//					HttpSession session=request.getSession();
//					if(session!=null){
//						session.invalidate();	// 세션 삭제
//					}
//				})).logoutSuccessHandler(((request, response, authentication) -> {
//					response.sendRedirect("/");
//				}));

        /* Spring Security 사용 시
        http.formLogin(f->f{
            f.loginProcessingUrl("/login");     // 로그인 url 설정
        });
         */

		// /user, /manager, /admin으로 들어가도 /loginForm으로 접근하도록
		return http.build();
	}

	/*
	public class MyCustomDs1 extends AbstractHttpConfigurer<MyCustomDs1, HttpSecurity> { // custom Filter
		@Override
		public void configure(HttpSecurity http) throws Exception {
			AuthenticationManager authenticationManager=http.getSharedObject(AuthenticationManager.class);	// null값으로 받아들임?
			System.out.println("authenticationManager : "+authenticationManager);
			JWTAuthenticationFilter jwtAuthenticationFilter=new JWTAuthenticationFilter(authenticationManager,mycustomAuthenticationProvider());
			jwtAuthenticationFilter.setAuthenticationSuccessHandler(new CustomLoginSuccessHandler(tokenUtils));
			JWTAuthorizationFilter jwtAuthorizationFilter=new JWTAuthorizationFilter(authenticationManager, memberRepository);
			http.addFilter(corsConfig.corsFilter())
			http.addFilter(corsFilter)
					//.addFilter(new JWTAuthenticationFilter(authenticationManager, mycustomAuthenticationProvider()))  // AuthenticationManager를 Parameter로 넘겨줘야 함(로그인을 진행하는 데이터이기 때문)
					//.addFilter(jwtAuthenticationFilter)
					.addFilter(jwtAuthorizationFilter);

			System.out.println("authenticationManager3 : " + authenticationManager);    // log
		}

	}
	
	 */

/*
	@Bean
	public CustomLoginSuccessHandler customLoginSuccessHandler(){
		return new CustomLoginSuccessHandler();
	}

 */
    /*
    기존: WebSecurityConfigurerAdapter를 상속하고 configure매소드를 오버라이딩하여 설정하는 방법
    => 현재: SecurityFilterChain을 리턴하는 메소드를 빈에 등록하는 방식(컴포넌트 방식으로 컨테이너가 관리)
    //https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter

    @Override
    protected void configure(HttpSecurity http) throws  Exception{
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin").access("\"hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();
    }

     */
}

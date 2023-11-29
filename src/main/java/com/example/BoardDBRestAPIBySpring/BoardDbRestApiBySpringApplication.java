package com.example.BoardDBRestAPIBySpring;

import com.example.BoardDBRestAPIBySpring.config.jwt.JWTAuthenticationFilter;
import com.example.BoardDBRestAPIBySpring.controller.handler.CustomLoginSuccessHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Log4j2
@SpringBootApplication
public class BoardDbRestApiBySpringApplication {

	@Bean   // @Bean의 역할은 해당 메서드의 return 되는 Object를 IoC로 등록해줌
	public BCryptPasswordEncoder encodePwd(){
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(BoardDbRestApiBySpringApplication.class, args);
	}





}

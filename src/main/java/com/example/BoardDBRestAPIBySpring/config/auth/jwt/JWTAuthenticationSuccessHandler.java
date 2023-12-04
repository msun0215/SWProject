package com.example.BoardDBRestAPIBySpring.config.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
public class JWTAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final String text = "1";

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException, ServletException {

        log.info("authentication.getName() = {}", authentication.getName());

        response.sendRedirect("/successLogin");
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final FilterChain chain,
                                        final Authentication authentication) throws IOException, ServletException {

        log.info("authentication.getName() = {}", authentication.getName());

        request.getRequestDispatcher("/successLogin").forward(request, response);
    }
}

package com.example.BoardDBRestAPIBySpring.controller;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ErrorController {
    @GetMapping("/error/unauthorized")
    public ResponseEntity<Void> unauthorized(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

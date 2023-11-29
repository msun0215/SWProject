package com.example.BoardDBRestAPIBySpring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {
    @GetMapping("/error/unauthorized")
    public ResponseEntity<Void> unauthorized(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

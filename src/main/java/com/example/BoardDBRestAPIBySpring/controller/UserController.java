package com.example.BoardDBRestAPIBySpring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = "/user")
public class UserController {
    @GetMapping("/")
    public ModelAndView userMain(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("userMain");
        return mv;
    }
}

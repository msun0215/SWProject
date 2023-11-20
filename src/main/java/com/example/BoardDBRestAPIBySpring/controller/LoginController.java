package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;    // 암호화

    @Autowired
    private MemberRepository memberRepository;


    @GetMapping({"", "/"})
    public String index(){
        return "index";
    }

    @PostMapping("/join")
    public String join(Member member){
        System.out.println(member);
        //member.setRole("ROLE_USER");

        // 회원가입은 잘 되나 저장한 비밀번호로 저장됨
        // =>Security로 로그인을 할 수가 없음
        // Password가 Encrypt 되지 않았기 때문
        String rawPassword = member.getMember_pw();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); // 비밀번호 암호화
        member.setMember_pw(encPassword);
        member.setMember_role_id(3);
        memberRepository.save(member);

        return "redirect:/loginForm";   // member 저장이 완료되면 loginForm으로 되돌아가기
    }

    @GetMapping("/loginForm")
    public String loginForm(){return "loginForm";}

    @GetMapping("/joinForm")
    public String joinForm(){ return "joinForm";}

    @GetMapping("/user")
    public String user(){return "user";}

    @GetMapping("/manager")
    public String manager(){return "manager";}

    @GetMapping("/admin")
    public String admin(){return "admin";}

}

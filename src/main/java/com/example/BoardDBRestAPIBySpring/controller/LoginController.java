package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;    // 암호화

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;


    @GetMapping({"", "/"})
    public String index(){
        return "index";
    }

    @PostMapping("/join")
    public String join(@RequestParam String memberID, @RequestParam String memberPW, @RequestParam String memberName, @RequestParam String memberNickname){
        Member member=new Member();
        //member.setRole("ROLE_USER");

        // 회원가입은 잘 되나 저장한 비밀번호로 저장됨
        // =>Security로 로그인을 할 수가 없음
        // Password가 Encrypt 되지 않았기 때문
        member.setMemberID(memberID);
        member.setMemberPW(bCryptPasswordEncoder.encode(memberPW)); // 비밀번호 암호화
        member.setMemberName(memberName);
        member.setMemberNickname(memberNickname);
        Role roleWithId3 = roleRepository.findByRoleId(3L);
        member.setRoles(roleWithId3);
        memberRepository.save(member);

        return "redirect:/loginForm";   // member 저장이 완료되면 loginForm으로 되돌아가기
    }
/*
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, @RequestParam(value="exception", required = false) String exception, Model model){
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "loginForm";
    }

 */
    /*
@PostMapping(value = "/logout")
public ResponseEntity<Void> logout(HttpServletRequest servletRequest) {

    loginService.logout();
    return ResponseEntity.ok().build();
}

     */

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

package com.example.BoardDBRestAPIBySpring.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.BoardDBRestAPIBySpring.config.jwt.JWTProperties;
import com.example.BoardDBRestAPIBySpring.domain.LoginRequest;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.MemberResponseDTO;
import com.example.BoardDBRestAPIBySpring.domain.Message;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.domain.Token;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Log4j2
@RestController
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;    // 암호화
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;

//    Role role1=roleRepository.save(new Role(1,"ROLE_ADMIN"));
//    Role role2=roleRepository.save(new Role(2,"ROLE_MANAGER"));
//    Role role3=roleRepository.save(new Role(3,"ROLE_USER"));

    @GetMapping({"", "/"})
    // @RestController를 사용할 경우 ModelAndView를 사용해야 html 페이지로 이동할 수 있다.
    public ModelAndView index(){
        //System.out.println("login user ID : "+principalDetails.getMember().getMemberID());
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }


    @PostMapping("/login")
    public Token login(@RequestBody LoginRequest request){
        String memberID = request.getMemberID();
        String memberPW = request.getMemberPW();
        return memberService.login(memberID, memberPW);
    }


    @GetMapping("/user/list")
    public ResponseEntity<MemberResponseDTO> findAll(){
        final MemberResponseDTO memberResponseDTO=MemberResponseDTO.builder()
                .memberList(memberRepository.findAll()).build();

        return ResponseEntity.ok(memberResponseDTO);
    }

    @GetMapping("login/successLogin")
    public ModelAndView successLogin(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("successLogin");
        return mv;
    }

    @PostMapping("/join")
    public ModelAndView join(@ModelAttribute Member reqmember){
        // @RequestParam String memberID, @RequestParam String memberPW, @RequestParam String memberName, @RequestParam String memberNickname
        ModelAndView modelAndView=new ModelAndView();
        Member member=new Member();
        //member.setRole("ROLE_USER");

        // 회원가입은 잘 되나 저장한 비밀번호로 저장됨
        // =>Security로 로그인을 할 수가 없음
        // Password가 Encrypt 되지 않았기 때문
        member.setMemberID(reqmember.getMemberID());
        member.setMemberPW(bCryptPasswordEncoder.encode(reqmember.getMemberPW())); // 비밀번호 암호화
        member.setMemberName(reqmember.getMemberName());
        member.setMemberNickname(reqmember.getMemberNickname());
        Role roleWithId3 = roleRepository.findByRoleID(3L);     // 기본적으로 회원가입 할 경우 ROLE_USER로 등록
        member.setRoles(roleWithId3);
        memberRepository.save(member);

        modelAndView.addObject("data",
                new Message("회원가입이 완료되었습니다! 로그인을 진행해주세요","loginForm"));
        modelAndView.setViewName("message");

        return modelAndView;   // member 저장이 완료되면 loginForm으로 되돌아가기
    }

    @GetMapping("/token")
    public ValidateTokenDto validateToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(JWTProperties.HEADER_STRING);
        String token = authorizationHeader.replace(JWTProperties.TOKEN_PREFIX, "");
        try {
            String username = JWT.require(Algorithm.HMAC512(JWTProperties.SECRET)).build().verify(token)
                    .getClaim("username").asString();
            return new ValidateTokenDto(true, username);
        } catch (Exception e) {
            log.error("error = {}", e.getMessage());
            return new ValidateTokenDto(false, "");
        }
    }

    @GetMapping("/loginForm")
    public ModelAndView loginForm(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("loginForm");
        return modelAndView;
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

    @GetMapping("/joinForm")
    public ModelAndView joinForm(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("joinForm");
        return modelAndView;
    }

    public record ValidateTokenDto(boolean validate, String username) {
    }

//    @PostMapping("/login")
//    public Token login(@RequestBody Map<String, String> member){
//        log.info("memberID = {}", member.get("memberID"));
//        Member member1=memberRepository.findByMemberID(member.get("memberID"))
//                //.orElseThrow(()->new IllegalArgumentException("가입되지 않은 Email입니다."));
//
//    }



/*
    @GetMapping("/user")
    public String user(){return "user";}

    @GetMapping("/manager")
    public String manager(){return "manager";}

    @GetMapping("/admin")
    public String admin(){return "admin";}

 */

}

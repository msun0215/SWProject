package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.domain.*;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.service.AuthService;
import com.example.BoardDBRestAPIBySpring.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

// https://velog.io/@u-nij/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class LoginController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;    // 암호화

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final MemberService memberService;
    private final AuthService authService;
    private final long COOKIE_EXPIRATION=7776000;       // 90일

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

    // 로그인->Token 발급
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginByJson(@RequestBody @Valid AuthDTO.LoginDto loginDto){
        String memberID = loginDto.getMemberID();
        String memberPW = loginDto.getMemberPW();

        // Member 등록 및 RefreshToken 저장
        AuthDTO.TokenDto tokenDto=authService.login(loginDto);

        // RefreshToken 저장
        HttpCookie httpCookie= ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+tokenDto.getAccessToken()) // AccessToken 저장
                .build();
    }


//    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public ResponseEntity<?> loginByWWW(@Valid AuthDTO.LoginDto loginDto){
//        String memberID = loginDto.getMemberID();
//        String memberPW = loginDto.getMemberPW();
//
//        // Member 등록 및 RefreshToken 저장
//        AuthDTO.TokenDto tokenDto=authService.login(loginDto);
//
//        // RefreshToken 저장
//        HttpCookie httpCookie= ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
//                .maxAge(COOKIE_EXPIRATION)
//                .httpOnly(true)
//                .secure(true)
//                .build();
//
//        HttpHeaders headers=new HttpHeaders();
//        headers.add(HttpHeaders.LOCATION,"/validate");
//
//
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, httpCookie.toString())
//                .headers(headers)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer "+tokenDto.getAccessToken()) // AccessToken 저장
//                .build();
//    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginByWWW(@Valid AuthDTO.LoginDto loginDto){

        String memberID = loginDto.getMemberID();
        String memberPW = loginDto.getMemberPW();

        // Member 등록 및 RefreshToken 저장
        AuthDTO.TokenDto tokenDto=authService.login(loginDto);

        // RefreshToken 저장
        HttpCookie httpCookie= ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(true)
                .build();

        HttpHeaders headers=new HttpHeaders();
        headers.add(HttpHeaders.LOCATION,"/validate");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+tokenDto.getAccessToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                .headers(headers)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer "+tokenDto.getAccessToken()) // AccessToken 저장
                .build();

//        ModelAndView mv=new ModelAndView();
//        mv.setView(new RedirectView("/validate"));   // validate 페이지로 이동

//        return mv;
    }

    @RequestMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken){
        if(!authService.validate(requestAccessToken))
            return ResponseEntity.status(HttpStatus.OK).build();    // 재발급 필요 없음
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();   // 재발급 필요
    }


    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name="refresh-token") String requestRefreshToken,
                                    @RequestHeader("Authorization") String requestAccessToken) {
        AuthDTO.TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenDto != null) {     // 토큰 재발급 성공
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION).httpOnly(true).secure(true).build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())  // AccessToken 저장
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken()).build();
        } else {     // Refresh Token 탈취 가능성
            // Cookie 삭제 후 재로그인 유도
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0).path("/").build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString()).build();
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String requestAccessToken){
        authService.logout(requestAccessToken);
        ResponseCookie responseCookie=ResponseCookie.from("refresh-token","")
                .maxAge(0).path("/").build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString()).build();
    }


    @GetMapping("/user/list")
    public ResponseEntity<MemberResponseDTO> findAll(){
        final MemberResponseDTO memberResponseDTO=MemberResponseDTO.builder()
                .memberList(memberRepository.findAll()).build();

        return ResponseEntity.ok(memberResponseDTO);
    }


    @GetMapping("login/successLogin")
    public ModelAndView successLogin(ModelAndView mv, @RequestHeader("Authorization") String requestAccessToken){

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
    public ModelAndView loginForm(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("loginForm");
        return modelAndView;
    }

    @GetMapping("/joinForm")
    public ModelAndView joinForm(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("joinForm");
        return modelAndView;
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

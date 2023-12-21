package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.domain.AuthDTO;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.MemberResponseDTO;
import com.example.BoardDBRestAPIBySpring.domain.Message;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.service.AuthService;
import com.example.BoardDBRestAPIBySpring.service.MemberService;
import jakarta.validation.Valid;
import java.util.Collection;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

// https://velog.io/@u-nij/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class LoginController {
    private final MemberService memberService;
    private final AuthService authService;
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

    // 로그인->Token 발급
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginByJson(Model model, @RequestBody @Valid AuthDTO.LoginDto loginDto) {
        String memberID = loginDto.getMemberID();
        String memberPW = loginDto.getMemberPW();

        // Member 등록 및 RefreshToken 저장
        AuthDTO.TokenDto tokenDto = authService.login(loginDto);

        if(tokenDto==null){
            throw new IllegalArgumentException("등록된 정보가 없습니다. 다시 시도해주세요!");
        } else {
            return ResponseEntity.ok()
                    .body(tokenDto);
        }
    }


    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginByWWW(Model model, @Valid AuthDTO.LoginDto loginDto){
        String memberID = loginDto.getMemberID();
        String memberPW = loginDto.getMemberPW();


        // Member 등록 및 RefreshToken 저장
        AuthDTO.TokenDto tokenDto=authService.login(loginDto);

        if(tokenDto==null){
            throw new IllegalArgumentException("등록된 정보가 없습니다. 다시 시도해주세요!");
        }
        else {
            return ResponseEntity.ok()
                    .body(tokenDto);
        }
    }

    @RequestMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken){
        if(!authService.validate(requestAccessToken))
            return ResponseEntity.status(HttpStatus.OK).build();    // 재발급 필요 없음
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();   // 재발급 필요
    }

    @GetMapping("/token")
    public ResponseEntity<TokenResponse> token(@RequestHeader("Authorization") String requestAccessToken) {
        if (authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Authentication authentication = authService.getAuthentication(requestAccessToken);
        String memberID = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        GrantedAuthority grantedAuthority = authorities.stream().toList().get(0);
        String authority = grantedAuthority.getAuthority();
        String role = authority.replace("ROLE_", "");

        TokenResponse tokenResponse = TokenResponse.builder()
                .memberID(memberID)
                .role(role)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader(name = "Refresh-Token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {
        AuthDTO.TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenDto != null) {     // 토큰 재발급 성공
            return ResponseEntity.ok()
                    .body(reissuedTokenDto);
        } else {     // Refresh Token 탈취 가능성
            // Cookie 삭제 후 재로그인 유도
            throw new IllegalArgumentException("다시 로그인하세요.");
        }
    }

    @PostMapping("/user/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String requestAccessToken){
        authService.logout(requestAccessToken);

        return ResponseEntity.ok().build();
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

        if(memberRepository.findByMemberID(reqmember.getMemberID())!=null){
            throw new IllegalArgumentException("이미 존재하는 회원입니다! 다시 입력해주세요.");
        }

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
                new Message("회원가입이 완료되었습니다! 로그인을 진행해주세요", "loginForm"));
        modelAndView.setViewName("message");

        return modelAndView;   // member 저장이 완료되면 loginForm으로 되돌아가기
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

    public record TokenResponse(String memberID, String role) {
        @Builder
        public TokenResponse {
        }
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

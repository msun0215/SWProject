package com.example.BoardDBRestAPIBySpring.config.auth;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Security 설정에서 loginProcessUrl("/login");
// login 요청이 들어오면 자동으로 UserDetailsService 타입으로
// IoC되어 있는 loadUserByUsername 함수가 실행됨
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    //@Autowired
    private final MemberRepository memberRepository;

    // Security Session = Authentication = UserDetails
    // Security Session(내부 Authentication(내부 UserDetails))
    @Override
    public UserDetails loadUserByUsername(String memberID) throws UsernameNotFoundException {
        // String username은 loginForm.html에서 넘어온 input name="username"
        System.out.println("PrincipalDetailsService의 loadUserByusername() : "  +memberID);
        Member memberEntity = memberRepository.findByMemberID(memberID);
        System.out.println("PrincipalDetailsService에서 찾은 Member Entity : "+memberEntity);
        if(memberEntity!=null){       // username으로 찾은 userEntity가 존재한다면
            return new PrincipalDetails(memberEntity);
        }
        return null;
    }


}

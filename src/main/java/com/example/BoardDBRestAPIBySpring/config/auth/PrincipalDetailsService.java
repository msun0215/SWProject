package com.example.BoardDBRestAPIBySpring.config.auth;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

// Security 설정에서 loginProcessUrl("/login");
// login 요청이 들어오면 자동으로 UserDetailsService 타입으로
// IoC되어 있는 loadUserByUsername 함수가 실행됨
@Log4j2
@Service
@RequiredArgsConstructor
@Component
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Security Session = Authentication = UserDetails
    // Security Session(내부 Authentication(내부 UserDetails))
    @Override
    public UserDetails loadUserByUsername(String memberID) throws UsernameNotFoundException {
        // String memberID loginForm.html에서 넘어온 input name="memberID"
        System.out.println("PrincipalDetailsService의 loadUserByusername() : "  +memberID);


            Member memberEntity = memberRepository.findByMemberID(memberID);


            System.out.println("PrincipalDetailsService에서 찾은 Member Entity : " + memberEntity);
            if (memberEntity != null) {       // memberID로 찾은 memberEntity가 존재한다면
                System.out.println("memberEntity [[" + memberEntity + "]] return 성공!");
                PrincipalDetails principalDetails = new PrincipalDetails(memberEntity);
                System.out.println("loadUserByUsername에서 찾은 principalDetails : " + principalDetails);
                return principalDetails;
            } else {
                System.out.println(memberID + "의 memberEntity를 찾을 수 없습니다.");
                return null;
            }

    }


}

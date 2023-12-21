package com.example.BoardDBRestAPIBySpring.config.auth;

// Security가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다
// login을 진행이 완료가 되면 Security Session을 만들어 준다(Security ContextHolder)
// Object Type => Authentication Type Object
// Authentication 안에 User의 정보가 있어야 함
// User Object Type => UserDetails Type Object

//import com.cos.security1.model.User;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// Security Session=> Authentication => UserDetails(PrincipalDetails)
@Log4j2
@Data
@AllArgsConstructor
@ToString
public class PrincipalDetails implements UserDetails {

    private final Member member;  // Composition
    private Map<String, Object> attributes;

    // 일반 로그인 객체
    public PrincipalDetails(Member member){
        this.member=member;
    }   // Composition

    public Member getMember(){return member;}

    // 해당 User의 권한을 return하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // User의 Roll을 return하는데, User.getRoll()의 타입은 String
        Collection<GrantedAuthority> collect=new ArrayList<GrantedAuthority>();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(
                "ROLE_".concat(member.getRoles().getRoleName()));
        collect.add(simpleGrantedAuthority);

        return collect;
    }

    // 비밀번호
    @Override
    public String getPassword() {
        return member.getMemberPW();
    }

    // 아이디(PK가 통상적)
    @Override
    public String getUsername() {
        return member.getMemberID();
    }

    // 계정 만료되지 않았는가?
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠기지 않았는가?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // user의 비밀번호가 기간이 지났는가?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 되어있는가?
    @Override
    public boolean isEnabled() {
        // 비활성화가 되는 경우
        // 1년동안 회원이 로그인을 안하면 휴면 계정으로 하기로 함
        // user.getLoginDate();
        // 현재시간 - 마지막 로그인 시간>1년?return false:return true;
        return true;
    }

}

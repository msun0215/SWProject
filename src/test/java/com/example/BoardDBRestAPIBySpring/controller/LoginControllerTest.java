package com.example.BoardDBRestAPIBySpring.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.BoardDBRestAPIBySpring.config.AbstractRestDocsTest;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class LoginControllerTest extends AbstractRestDocsTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void login() throws Exception {
        var memberID = "test@test.com";
        var memberPW = "test";
        var url = "/login";

        var member = new Member();
        member.setMemberID(memberID);
        member.setMemberPW(bCryptPasswordEncoder.encode(memberPW));
        member.setMemberName("test");
        member.setMemberNickname("testk");
        var role = new Role();
        role.setRoleName("USER");
        roleRepository.save(role);
        member.setRoles(role);

        memberRepository.save(member);

        // expected
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("memberID=" + memberID + "&memberPW=" + memberPW))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        formParameters(
                                parameterWithName("memberID").description("이메일"),
                                parameterWithName("memberPW").description("비밀번호")
                        ),
                        responseHeaders(
                                headerWithName("Authorization").description("JWT 토큰")
                        )
                ));
    }
}
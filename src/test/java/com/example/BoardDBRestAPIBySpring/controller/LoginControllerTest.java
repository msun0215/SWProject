package com.example.BoardDBRestAPIBySpring.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.BoardDBRestAPIBySpring.config.AbstractRestDocsTest;
import com.example.BoardDBRestAPIBySpring.domain.AuthDTO;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest extends AbstractRestDocsTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginByWWW() throws Exception {
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
                                headerWithName("Set-Cookie").description("JWT Refresh Token"),
                                headerWithName("Authorization").description("JWT Access Token")
                        )
                ));
    }

    @Test
    void loginByJson() throws Exception {
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

        var loginDto = AuthDTO.LoginDto.builder()
                .memberID(memberID)
                .memberPW(memberPW)
                .build();

        String json = objectMapper.writeValueAsString(loginDto);

        // expected
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("memberID").description("이메일"),
                                fieldWithPath("memberPW").description("비밀번호")
                        ),
                        responseHeaders(
                                headerWithName("Set-Cookie").description("JWT Refresh Token"),
                                headerWithName("Authorization").description("JWT Access Token")
                        )
                ));
    }

    @Test
    void signup() throws Exception {
        // given
        var memberID = "test@test.com";
        var memberPW = "test";
        var memberName = "test";
        var memberNickname = "t";
        String content =
                "memberID=" + memberID + "&memberPW=" + memberPW + "&memberName=" + memberName + "&memberNickname="
                        + memberNickname;
        var url = "/join";

        // expected
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        formParameters(
                                parameterWithName("memberID").description("이메일"),
                                parameterWithName("memberPW").description("비밀번호"),
                                parameterWithName("memberName").description("이름"),
                                parameterWithName("memberNickname").description("별명")
                        )
                ));
    }
}
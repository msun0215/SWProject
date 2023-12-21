package com.example.BoardDBRestAPIBySpring.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.BoardDBRestAPIBySpring.config.AbstractRestDocsTest;
import com.example.BoardDBRestAPIBySpring.config.jwt.JWTProperties;
import com.example.BoardDBRestAPIBySpring.domain.AuthDTO;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
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
    @Autowired
    private RedisService redisService;

    @BeforeEach
    void init() {
        memberRepository.deleteAll();
    }

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
                        responseFields(
                                fieldWithPath("accessToken").description("JWT Access Token"),
                                fieldWithPath("refreshToken").description("JWT Refresh Token")
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
                        responseFields(
                                fieldWithPath("accessToken").description("JWT Access Token"),
                                fieldWithPath("refreshToken").description("JWT Refresh Token")
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

    @Test
    void loginStatusCheck() throws Exception {
        // given
        var memberID = "test@test.com";
        var memberPW = "test";

        Member member = new Member();
        member.setMemberID(memberID);
        member.setMemberPW(bCryptPasswordEncoder.encode(memberPW));
        member.setMemberName("test");
        member.setMemberNickname("testk");
        var role = new Role();
        role.setRoleName("USER");
        roleRepository.save(role);
        member.setRoles(role);

        memberRepository.save(member);

        var url = "/token";

        var accessJwtToken = generateJwtToken(member);
        var authorizationHeader = JWTProperties.TOKEN_PREFIX.concat(accessJwtToken);

        // expected
        mockMvc.perform(get(url)
                        .header(AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description(JWT_TOKEN)
                        ),
                        responseFields(
                                fieldWithPath("memberID").description("로그인한 사용자의 아이디"),
                                fieldWithPath("role").description("로그인한 사용자의 권한")
                        )
                ));
    }


    @Test
    void reissue() throws Exception {
        // given
        var memberID = "test@test.com";
        var memberPW = "test";

        Member member = new Member();
        member.setMemberID(memberID);
        member.setMemberPW(bCryptPasswordEncoder.encode(memberPW));
        member.setMemberName("test");
        member.setMemberNickname("testk");
        var role = new Role();
        role.setRoleName("USER");
        roleRepository.save(role);
        member.setRoles(role);

        memberRepository.save(member);

        var url = "/reissue";

        var accessJwtToken = generateJwtToken(member);
        var authorizationHeader = JWTProperties.TOKEN_PREFIX.concat(accessJwtToken);

        var refreshJwtTokenHeader = generateJwtToken(member);

        redisService.setValuesWithTimeout("RT(Server)" + memberID, refreshJwtTokenHeader,
                Duration.ofHours(1).toMillis());

        // expected
        mockMvc.perform(post(url)
                        .header(AUTHORIZATION, authorizationHeader)
                        .header("Refresh-Token", refreshJwtTokenHeader))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("access jwt token"),
                                headerWithName("Refresh-Token").description("refresh jwt token")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("새로 발급한 accessToken"),
                                fieldWithPath("refreshToken").description("새로 발급한 refreshToken")
                        )
                ));
    }

    private String generateJwtToken(final Member member) {
        String AUTHROITIES_KEY="role";
        String MEMBERID_KEY="memberID";
        String url="http://localhost:8080";

        long now = System.currentTimeMillis();

        String secretKey = "TXlTV1Byb2plY3RVc2luZ0pXVHdpdGhTcHJpbmdCb290QW5kU3ByaW5nU2VjdXJpdHk=TXlTV1Byb2plY3RVc2luZ0pXVHdpdGhTcHJpbmdCb290QW5kU3ByaW5nU2VjdXJpdHk";
        byte[] secretKeyBytes= Decoders.BASE64.decode(secretKey);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + 604800))
                .setSubject("access-token")
                .claim(url, true)
                .claim(MEMBERID_KEY, member.getMemberID())
                .claim(AUTHROITIES_KEY, "ROLE_USER")
                .signWith(Keys.hmacShaKeyFor(secretKeyBytes), SignatureAlgorithm.HS512)
                .compact();
    }
}
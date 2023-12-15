//package com.example.BoardDBRestAPIBySpring.config.jwt;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
//import com.example.BoardDBRestAPIBySpring.controller.handler.CustomLoginSuccessHandler;
//import com.example.BoardDBRestAPIBySpring.domain.Member;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.IncorrectClaimException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.log4j.Log4j2;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.io.*;
//import java.net.URLDecoder;
//import java.util.*;
//
///*
//     Spring Security의 UsernamePasswordAuthenticationFilter 사용
//     /login 요청해서 username, password를 POST로 전송하면
//     UsernamePasswordAuthenticationFilter가 동작함
//     but, formLogin().disable() 설정을 하면서 이 Filter가 동작을 하지 않음
//     따라서 이 Filter를 SecurityConfig에 다시 등록을 해주어야 한다.
//*/
//
//@Slf4j
//@Service
////@RequiredArgsConstructor
//public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private AuthenticationManager authenticationManager;  // 로그인을 실행하기 위한 역할
//
//    private ObjectMapper objectMapper=new ObjectMapper();
//    //@Autowired
//    private final CustomAuthenticationProvider customAuthenticationProvider;
//    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
//
//
//    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, CustomAuthenticationProvider customAuthenticationProvider){
//        this.customAuthenticationProvider=customAuthenticationProvider;
//        this.authenticationManager=authenticationManager;
//
//        super.setAuthenticationManager(authenticationManager);
//    }
//
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        System.out.println("JWTAuthenticationFilter : 로그인 시도중");
//        System.out.println("=========================================");
//
////        System.out.println("memberID : "+request.getAttribute("memberID"));
////        System.out.println("memberPW : "+request.getAttribute("memberPW"));
////
////        // request.getAttribute는 사용 후에 소멸되는 메소드임
////        String loginID = request.getAttribute("memberID").toString();
////        String loginPW = request.getAttribute("memberPW").toString();
////
////        System.out.println("loginID : "+loginID);
////        System.out.println("loginPW : "+loginPW);
//
//
//        // 1. username, password를 받아서
//        try {
//            /*
//            //가장 원초적인 방법
//            System.out.println("getInputStream() : "+request.getInputStream().toString());
//            BufferedReader br=request.getReader();
//            String input=null;
//            Map<String, String> parsedData = new HashMap<>();
//            while((input=br.readLine())!=null){
//                System.out.println(input);
//                String[] keyValue = input.split("&");
//                for (String pair : keyValue) {
//                    // URL 디코딩하여 key와 value 분리
//                    String[] entry = pair.split("=");
//                    String key = URLDecoder.decode(entry[0], "UTF-8");
//                    String value = entry.length > 1 ? URLDecoder.decode(entry[1], "UTF-8") : "";
//                    parsedData.put(key, value);
//                }
//
//            }
//
//            System.out.println(request.getInputStream().toString());
//            */
//
//            // BufferedReader를 사용하여 데이터 읽기
////            StringBuilder requestData = new StringBuilder();
////            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
////                    request.getInputStream(), StandardCharsets.UTF_8))) {
////                String line;
////                while ((line = reader.readLine()) != null) {
////                    requestData.append(line);
////                }
////            }
//            // x-www-form-urlencoded 형식의 데이터를 Member 객체로 변환
////            String formData = requestData.toString();
////            ObjectMapper objectMapper = new ObjectMapper();
////            Member member = objectMapper.readValue(formData, Member.class);
//
//            BufferedReader reader=request.getReader();
//            StringBuilder requestData=new StringBuilder();
//            String line;
//
//            System.out.println("1. reader : "+reader);
//            while((line=reader.readLine())!=null){
//                System.out.println("line : "+line);
//                requestData.append(line);
//            }
//            System.out.println("2. requestData : "+requestData);
//
//            // StringBuilder->String
//            String requestDataString = requestData.toString();
//            Map<String, String> dataMap = splitFormData(requestDataString);
//
//            String memberID = dataMap.get("memberID");
//            String memberPW = dataMap.get("memberPW");
//
//            System.out.println("memberID : "+memberID);
//            System.out.println("memberPW : "+memberPW);
//
//            Member member=new Member();
//            member.setMemberID(memberID);
//            member.setMemberPW(memberPW);
//
////            ObjectMapper oj=new ObjectMapper();
////            Member member=oj.readValue(requestData.toString(), Member.class);
//
//
//            // 변환된 Member 객체 사용
//            System.out.println("3. Member : " + member);
//
//            //=> request가 x-www-form-urlencoded 방식으로 넘어오면 &를 사용해서 parsing하면 되지만
//            //다른 방식으로 넘어오면 parsing하는 방식이 바뀌기 때문에 사용하지 않음
//
//
//            System.out.println("member.getMemberPW() : "+member.getMemberPW());
//            // Token 생성
//            UsernamePasswordAuthenticationToken authenticationToken
//                    =new UsernamePasswordAuthenticationToken(member.getMemberID(), member.getMemberPW());
//
//            // PrincipalDetailsService의 loadUserByUsername() 함수가 실행된다.
//            // 함수 실행 이후 정상이면 authentication이 return됨.
//            // DB에 있는 username과 password가 일치한다.
//            System.out.println("authenticationToken : "+authenticationToken);
//
//            //CustomAuthenticationProvider customAuthenticationProvider=new CustomAuthenticationProvider();
//            Authentication authentication=customAuthenticationProvider.authenticate(authenticationToken);
//            //Authentication authentication = getAuthenticationManager().authenticate(authenticationToken);
//            System.out.println("authentication : "+authentication);
//
//            System.out.println("authenticate : "+authentication.getPrincipal());
//            // PrincipalDetails 객체로 받아와서 getUser가 출력된다는 것은 로그인에 성공했다는 뜻임.
//            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//            System.out.println("principalDetails : "+principalDetails);
//            System.out.println("getUserName() : "+principalDetails.getUsername());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
////            setDetails(request,authenticationToken);
//            log.debug("Save authentication in SecurityContextHolder");
//
//            return authentication;
//
////            // https://velog.io/@on5949/SpringSecurity-Authentication-%EA%B3%BC%EC%A0%95-%EC%A0%95%EB%A6%AC
////            try{
////                Authentication authentication=authenticationManager.authenticate(authenticationToken);  // login 정보
////                System.out.println("authentication : "+authentication);
////                // Principal 객체로 받아와서 getUser가 출력이 된다는 것은 로그인에 성공했다는 뜻임.
////                PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
////                System.out.println("principalDetails : "+principalDetails);
////                System.out.println("getMemberName() : "+principalDetails.getMember().getMemberName());   // 로그인이 정상적으로 되었다는 뜻
////                // authentication 객체가 session 영역에 저장을 해야하고 그 방법이 return 해주면 됨
////                // return 이유는 권한관리를 security가 대신 해주기 때문에 편하려고 하는 것.
////                // 굳이 JWT Token을 사용하면서 Session을 만들 필요는 없으나, 권한 처리를 위해서 JWT Token을 사용함
////
////                // authentication 객체가 session 영역에 저장이 된다.
////                return authentication;
////            }   catch(AuthenticationException e){
////                e.printStackTrace();
////                System.out.println("Authentication Failed : "+e.getMessage());
////            }
//
//
//        } catch (IncorrectClaimException e) {   // 잘못된 토큰인 경우
//            SecurityContextHolder.clearContext();
//            log.debug("Invalid JWT Token");
//            try {
//                response.sendError(403);
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        }catch(UsernameNotFoundException e){
//            SecurityContextHolder.clearContext();
//            log.debug("Can't find user");
//            try {
//                response.sendError(403);
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 2. 정상인지 authenticationManager로 로그인 시도.
//        // 3. PrincipalDetailsService가 호출됨 -> loadUserByUsername(String username)이 실행됨
//        // 4. PrincipalDetails를 Session에 담고 ▶ 권한 관리를 위해서
//        // 5. JWT Token을 만들어서 응답해주면 된다
//        return null;    // error 발생시 null return
//
//        //https://devbksheen.tistory.com/entry/Spring-Security-JWT
//
////        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;
//
//    }
//
//    // attemptAuthentication 실행 이후 인증이 정상적으로 되었다면
//    // successfulAuthentication에서 JWT Token을 생성해서
//    // request 요청한 user에게 JWT Token을 response 하면 된다.
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        System.out.println("=========================================");
//        System.out.println("Authentication이 실행됨 : 인증이 완료되었다는 뜻임");
//        PrincipalDetails principalDetails=(PrincipalDetails)authResult.getPrincipal();
//
//
//
//        //RSA방식이 아닌, Hash암호방식
//        String jwtToken = JWT.create()
//                .withSubject(principalDetails.getUsername())    // token 별명 느낌?
//                        .withExpiresAt(new Date(System.currentTimeMillis()+JWTProperties.EXPIRATION_TIME))  // Token 만료 시간 -> 현재시간 + 만료시간
//                                .withClaim("id", principalDetails.getMember().getMemberID())    // 비공개 Claim -> 넣고싶은거 아무거나 넣으면 됨
//                                        .withClaim("username", principalDetails.getMember().getMemberName())    // 비공개 Claim
//                                                .sign(Algorithm.HMAC512(JWTProperties.SECRET));  // HMAC512는 SECRET KEY를 필요로 함
//        //String jwtToken =TokenUtils.generateJwtToken(principalDetails.getMember());
//        response.addHeader(JWTProperties.HEADER_STRING, JWTProperties.TOKEN_PREFIX+jwtToken);
//        response.setHeader(JWTProperties.HEADER_STRING, JWTProperties.TOKEN_PREFIX+jwtToken);
//        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//        System.out.println("response : "+response);
//        System.out.println("JWTAuthenticationFilter에서의 response.getHeader('Authorization')) : "+response.getHeader(JWTProperties.HEADER_STRING));
//
//        //response.addHeader("X-Redirect", "/successLogin");
//        // Client에게 JWT Token을 응답
//        //response.getWriter().write("<script>window.location='/';</script>");
//        //response.getWriter().flush();
//        //response.getWriter().close();
//
//        // Client를 /엔드포인트로 리다이렉트
//        //response.getOutputStream().write(objectMapper.writeValueAsBytes(principalDetails));
//        response.sendRedirect("/login/successLogin");
//
//    }
//
//    private static Map<String, String> splitFormData(String formData){
//        Map<String, String> dataMap = new HashMap<>();
//        String[] keyValuePairs = formData.split("&"); // "&"를 기준으로 memberID=~ 와 memberPW=~를 나눔
//
//        for(String pair : keyValuePairs){
//            String[] entry = pair.split("=");   // memberID=~를 =기준으로 나눠서 memberID와 ~를 가짐
//            String key = entry[0];
//            String value = entry.length>1?entry[1]:"";
//
//            // URL Decoding
//            value = urlDecode(value);
//            dataMap.put(key, value);
//        }
//        return dataMap;
//    }
//
//    // URL Decoding Method
//    private static String urlDecode(String value){
//        try{
//            return URLDecoder.decode(value, "UTF-8");
//        }catch (UnsupportedEncodingException e){
//            e.printStackTrace();
//            return value;
//        }
//    }
//
//    private String resolveToken(HttpServletRequest request){
//        String bearerToken=request.getHeader(JWTProperties.HEADER_STRING);
//        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith(JWTProperties.TOKEN_PREFIX))
//            return bearerToken.substring(7);
//
//        return null;
//    }
//    /*
//    <Spring Security>
//    username, password 로그인 정상
//    Server쪽 SessionID 생성 -> client Cookie SessionID 를 응답
//    request를 보낼때마다 Cookie SessionID를 항상 들고 Server로 request하기 때문에
//    Server는 SessionID가 유효한지 판단해서, 유효하면 인증이 필요한 페이지로 접근하게 하면 된다.
//
//    <JWT>
//    username, password 로그인 정상
//    JWT Token을 생성하여 client 쪽으로 응답
//    request할 때마다 JWT Token을 가지고 요청한다.
//    Server는 JWT Token이 유효한지를 판단한다.(Filter를 만들어야 함)
//     */
//}

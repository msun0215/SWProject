package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.service.exception.BusinessLogicException;
import com.example.BoardDBRestAPIBySpring.service.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public void sendEmail(String toEmail, String title, String text){
        SimpleMailMessage simpleMailMessage=createEmailForm(toEmail,title,text);
        System.out.println("get SimpleMailMessage : "+simpleMailMessage);
        try{
            javaMailSender.send(simpleMailMessage);
        }catch (RuntimeException e){
            e.printStackTrace();
            log.debug("MailService.sendEmail exception occur toEmail: {}, title: {}, text: {}", toEmail, title, text);
            throw new BusinessLogicException(ExceptionCode.UNABLE_TO_SEND_EMAIL);
        }

    }

    // 발신할 email data 셋팅
    private SimpleMailMessage createEmailForm(String toEmail, String title, String text){
        System.out.println("EmailForm을 생성합니다");
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(title);
        mailMessage.setText(text);
        System.out.println("created Email Form! : "+mailMessage);
        return mailMessage;
    }

    //@Transactional
    public void sendCodeToEmail(String toEmail){

        System.out.println("보내야 할 대상 : "+toEmail);
        checkDuplicatedEmail(toEmail); // 중복 확인
        System.out.println("Complete to Check DuplicatedEmail");
        String title = "SWProject 이메일 인증번호입니다.";
        String authCode=createCode();
        System.out.println("sendCodeToEmail에서 authCode : "+authCode);
        sendEmail(toEmail, title, authCode);

        // 이메일 인증 요청 시 인정번호를 Redis에 저장함.
        // key = "AuthCode " + Email/value=AuthCode
        redisService.setValuesWithTimeout(AUTH_CODE_PREFIX+toEmail,authCode,authCodeExpirationMillis);
    }

    @Transactional
    public void checkDuplicatedEmail(String email){
        Member member=memberRepository.findByMemberID(email);

        if(member!=null){
            log.debug("checkDuplicatedEmail exception occur email : {}", email);
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    private String createCode(){
        int length=6;
        try{
            Random random= SecureRandom.getInstanceStrong();
            StringBuilder stringBuilder=new StringBuilder();

            for(int i=0;i<length;i++)
                stringBuilder.append(random.nextInt(10));
            return stringBuilder.toString();
        }catch (NoSuchAlgorithmException e){
            throw new BusinessLogicException(ExceptionCode.NO_SUCH_ALGORITHM);
        }
    }

    public void verifiedCode(String email, String authCode) {
        checkDuplicatedEmail(email);
        System.out.println("Complete to Check DuplicatedEmail");
        System.out.println("Email : "+email);
        System.out.println("authCode : "+authCode);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        System.out.println("RedisAuthCode : "+redisAuthCode);
        boolean authResult = redisService.checkExistsValue(redisAuthCode, (AUTH_CODE_PREFIX+email)) && redisAuthCode.equals(authCode);

        if (!authResult) {
            throw new BusinessLogicException(ExceptionCode.AUTH_CODE_IS_NOT_SAME);
        }
    }
}

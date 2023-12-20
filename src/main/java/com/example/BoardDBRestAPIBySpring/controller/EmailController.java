package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.controller.Custom.CustomAnnotation;
import com.example.BoardDBRestAPIBySpring.service.MailService;
import com.example.BoardDBRestAPIBySpring.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController

@RequiredArgsConstructor
public class EmailController {
    private final MemberService memberService;
    private final MailService mailService;

    @PostMapping("/emails/verification-requests")
    public ResponseEntity<?> sendMessage(@RequestParam String memberID) {

        System.out.println("Request Controller memberID : "+memberID);
        mailService.sendCodeToEmail(memberID);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/emails/verifications")
    public ResponseEntity<?> verificationEmail(@RequestParam @Valid String memberID,
                                            @RequestParam String authCode) {
//        String request=memberID.split("&")[0].substring(9).replace("%40","@");
//        System.out.println("Controller : "+request);
//        System.out.println("Controller AuthCode : " +authCode);
        System.out.println("Controller memberID : "+memberID);
        System.out.println("Controller authCode : "+authCode);
        mailService.verifiedCode(memberID, authCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
package com.example.BoardDBRestAPIBySpring.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

// alert를 통해 메세지를 보내주기 위한 용도
@Data
@NoArgsConstructor
public class Message {
    String message="";  // alert 띄워줄 문구
    String href="";     // 이동해야 할 페이지

    public Message(String message, String href){
        this.message=message;
        this.href=href;
    }

}

package com.socket.socketjava.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class aiController {

    private final ChatClient client;

    @RequestMapping("/chat")
    public String chat(String prompt) {
       return client.prompt()
               .user(prompt)
               .call()
               .content();
    }


}

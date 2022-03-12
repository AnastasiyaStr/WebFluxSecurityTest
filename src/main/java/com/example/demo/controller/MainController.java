package com.example.demo.controller;


import com.example.demo.domain.Message;
import com.example.demo.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/controller")
public class MainController {

    private final MessageService messageService;

    private MainController(MessageService messageService) {
        this.messageService = messageService;
    }


    @GetMapping
    public Flux<Message> list(@RequestParam(defaultValue = "0") Long start,
                              @RequestParam(defaultValue = "3") Long count) {

        return Flux.just(
                "First post",
                "Second post",
                "Third post"
        ).skip(start).take(count).map(Message::new);

//                return messageService.list();
    }


}

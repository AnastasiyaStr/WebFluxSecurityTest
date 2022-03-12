package com.example.demo.handlers;

import com.example.demo.domain.Message;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GreetingHandler {

    public Mono<ServerResponse> hello(ServerRequest request) {
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters
//                        .fromValue(new Greeting("Hello, Spring!")));

        var flux = Flux.just("First post", "Second post", "Third post").map(Message::new);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(flux, Message.class);
    }

    public Mono<ServerResponse> index
            (ServerRequest serverRequest) {
        String user = serverRequest
                .queryParam("user")
                .orElse("nobody");

        return ServerResponse.ok()
                .render("index",
                        Map.of("user", user));

    }

}

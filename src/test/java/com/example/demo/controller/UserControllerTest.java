package com.example.demo.controller;

import com.example.demo.config.JWTUtil;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.FixtureFromResource;
import com.example.demo.utils.Json;
import com.example.demo.utils.TestResource;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {UserController.class})
//@Import(UserMapperImpl.class)
@TestResource(path = "fixtures/controllers/user-controller-test.yml")
public class UserControllerTest extends AbstractControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    JWTUtil jwtUtil;

    @ParameterizedTest
    @FixtureFromResource
    public void signUp_validUserSignUpForm_userProfileReturned(User validUser,
                                                               @Json String validUserSignUpForm) {
        when(userService.findByUsername(any())).thenReturn(Mono.just(validUser));

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/login")

                .body(BodyInserters.fromFormData("username", "Admin")
                        .with("password", "Passw0rd"))
                .exchange()
                .expectStatus()
                .isFound();
    }

}

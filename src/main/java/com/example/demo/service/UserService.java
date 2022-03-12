package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String username) {

        return Flux.just(
                new User(1L, "Admin", "Passw0rd", UserRole.ROLE_ADMIN),
                new User(2L, "Enduser", "Passw0rd", UserRole.ROLE_USER)
        ).filter(user ->
                user.getUsername()
                        .equals(username)).cast(UserDetails.class).next();
    }

}

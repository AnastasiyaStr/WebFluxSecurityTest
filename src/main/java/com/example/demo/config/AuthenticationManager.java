package com.example.demo.config;


import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        String username;

        username = jwtUtil.extractUsername(token);

        if (username != null && jwtUtil.validateToken(token)) {

            Claims claimsFromToken = jwtUtil.getClaimsFromToken(token);

            List<String> role = claimsFromToken.get("role", List.class);

            List<SimpleGrantedAuthority> authorities = role.stream().map(SimpleGrantedAuthority::new).toList();

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);

            return Mono.just(authenticationToken);
        }

        return Mono.empty();
    }
}

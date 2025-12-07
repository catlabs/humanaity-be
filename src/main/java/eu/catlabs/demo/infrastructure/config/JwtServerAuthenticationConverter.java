package eu.catlabs.demo.infrastructure.config;

import eu.catlabs.demo.auth.infrastructure.security.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtService jwtService;

    public JwtServerAuthenticationConverter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }
        
        String token = authHeader.substring(7);
        
        if (!jwtService.validateToken(token)) {
            return Mono.empty();
        }
        
        try {
            String email = jwtService.extractEmail(token);
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            return Mono.just(new UsernamePasswordAuthenticationToken(email, null, authorities));
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}

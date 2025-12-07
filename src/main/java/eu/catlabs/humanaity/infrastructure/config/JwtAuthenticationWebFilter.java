package eu.catlabs.humanaity.infrastructure.config;

import eu.catlabs.humanaity.auth.infrastructure.security.JwtService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {

    public JwtAuthenticationWebFilter(JwtService jwtService) {
        super(new ReactiveAuthenticationManager() {
            @Override
            public reactor.core.publisher.Mono<Authentication> authenticate(Authentication authentication) {
                return reactor.core.publisher.Mono.just(authentication);
            }
        });
        setRequiresAuthenticationMatcher(
            new NegatedServerWebExchangeMatcher(
                ServerWebExchangeMatchers.pathMatchers("/auth/**")
            )
        );
        setServerAuthenticationConverter(new JwtServerAuthenticationConverter(jwtService));
    }
}

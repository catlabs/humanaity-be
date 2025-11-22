package eu.catlabs.demo.config;

import eu.catlabs.demo.services.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final Environment environment;

    public SecurityConfig(JwtService jwtService, Environment environment) {
        this.jwtService = jwtService;
        this.environment = environment;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        boolean isDevProfile = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> {
                    // Public endpoints - authentication
                    exchanges.pathMatchers("/auth/**").permitAll();
                    
                    // Public endpoints - OpenAPI/Swagger documentation (DEV uniquement)
                    if (isDevProfile) {
                        exchanges
                                .pathMatchers("/v3/api-docs").permitAll()
                                .pathMatchers("/v3/api-docs/**").permitAll()
                                .pathMatchers("/swagger-ui.html").permitAll()
                                .pathMatchers("/swagger-ui/**").permitAll()
                                .pathMatchers("/webjars/**").permitAll();
                    }
                    
                    // CORS preflight requests
                    exchanges.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    
                    // All other endpoints require authentication
                    exchanges.anyExchange().authenticated();
                })
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public AuthenticationWebFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationWebFilter(jwtService);
    }
}


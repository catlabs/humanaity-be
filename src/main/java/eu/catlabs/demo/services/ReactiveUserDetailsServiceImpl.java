package eu.catlabs.demo.services;

import eu.catlabs.demo.entity.User;
import eu.catlabs.demo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public ReactiveUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return Mono.fromCallable(() -> userRepository.findByEmail(email))
                .flatMap(optionalUser -> {
                    if (optionalUser.isEmpty()) {
                        return Mono.error(new UsernameNotFoundException("User not found: " + email));
                    }
                    User user = optionalUser.get();
                    return Mono.just(org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .authorities(user.getRoles().stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList()))
                            .build());
                });
    }
}


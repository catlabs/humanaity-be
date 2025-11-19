package eu.catlabs.demo.services;

import eu.catlabs.demo.dto.auth.AuthResponse;
import eu.catlabs.demo.dto.auth.LoginRequest;
import eu.catlabs.demo.dto.auth.RegisterRequest;
import eu.catlabs.demo.entity.AppUser;
import eu.catlabs.demo.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userService.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already taken");
        }

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        userService.save(user);

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, jwtService.getExpirationSeconds());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
        return new AuthResponse(token, jwtService.getExpirationSeconds());
    }
}

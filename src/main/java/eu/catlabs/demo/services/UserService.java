package eu.catlabs.demo.services;

import eu.catlabs.demo.entity.AppUser;
import eu.catlabs.demo.repository.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public UserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser save(AppUser user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = findByUsername(username);
        return new User(user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }
}

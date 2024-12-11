package mkoutra.birthdaykeeper.security.authentication;

import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * A custom implementation of `UserDetailsService`.
 * `UserDetailsService` is used to retrieve information about the `UserDetail` object.
 * In our implementation `User` implements `UserDetails`.
 * It is used for authentication purposes in JwtAuthenticationFilter.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " was not found."));
    }
}

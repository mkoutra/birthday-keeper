package mkoutra.birthdaykeeper.security.authentication;

import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.UserNotAuthenticatedException;
import mkoutra.birthdaykeeper.dto.authDTOs.AuthenticationRequestDTO;
import mkoutra.birthdaykeeper.dto.authDTOs.AuthenticationResponseDTO;
import mkoutra.birthdaykeeper.model.User;
import mkoutra.birthdaykeeper.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO)
            throws UserNotAuthenticatedException {

        String username = authenticationRequestDTO.getUsername();
        String password = authenticationRequestDTO.getPassword();

        // Use the authentication manager bean specified in the SecurityConfiguration class
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Retrieve user from database
        User user = userRepository
                .findUserByUsername(username)
                .orElseThrow( () -> new UserNotAuthenticatedException(
                                "User with username: " + username + " is not authenticated."));

        // Generate the JWT token using the username and role
        String generatedToken = jwtService.generateToken(authentication.getName(), user.getRole().name());

        return new AuthenticationResponseDTO(username, generatedToken);
    }
}

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

        // Use the authentication manager bean specified in the SecurityConfiguration class
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword())
        );

        // Retrieve user from database
        User user = userRepository.findUserByUsername(authenticationRequestDTO.getUsername()).orElseThrow(
                () -> new UserNotAuthenticatedException("User", "User " + authenticationRequestDTO.getUsername() + " is not authenticated.")
        );

        // Generate the JWT token using the username and role
        String generatedToken = jwtService.generateToken(authentication.getName(), user.getRole().name());

        return new AuthenticationResponseDTO(user.getUsername(), generatedToken);
    }
}

package mkoutra.birthdaykeeper.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.UserNotAuthenticatedException;
import mkoutra.birthdaykeeper.core.exceptions.ValidationException;
import mkoutra.birthdaykeeper.dto.authDTOs.AuthenticationRequestDTO;
import mkoutra.birthdaykeeper.dto.authDTOs.AuthenticationResponseDTO;
import mkoutra.birthdaykeeper.security.authentication.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthRestController {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthRestController.class);
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user using their credentials and returns a JWT token if successful."
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticateUser(
            @Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO,
            BindingResult bindingResult)
            throws ValidationException, UserNotAuthenticatedException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(authenticationRequestDTO);
        LOGGER.info("User with username: {} authenticated successfully.", authenticationRequestDTO.getUsername());
        return new ResponseEntity<>(authenticationResponseDTO, HttpStatus.OK);
    }
}

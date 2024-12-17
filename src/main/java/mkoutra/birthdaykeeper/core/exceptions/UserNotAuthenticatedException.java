package mkoutra.birthdaykeeper.core.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserNotAuthenticatedException extends AuthenticationException {

    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}

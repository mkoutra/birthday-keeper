package mkoutra.birthdaykeeper.core.exceptions;

public class UserNotAuthenticatedException extends EntityGenericException {
    private static final String DEFAULT_CODE = "NotAuthenticated";

    public UserNotAuthenticatedException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}

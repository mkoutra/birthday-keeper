package mkoutra.birthdaykeeper.core.exceptions;

public class AppServerException extends EntityGenericException {
    private final static String DEFAULT_CODE = "AppServer";

    public AppServerException(String code, String message) {
        super(code, message);
    }
}

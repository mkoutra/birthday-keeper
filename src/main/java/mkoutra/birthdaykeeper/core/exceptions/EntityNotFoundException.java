package mkoutra.birthdaykeeper.core.exceptions;

public class EntityNotFoundException extends EntityGenericException {
    private final static String DEFAULT_CODE = "NotFound";

    public EntityNotFoundException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}

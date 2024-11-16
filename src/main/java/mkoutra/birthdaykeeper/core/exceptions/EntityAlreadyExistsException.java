package mkoutra.birthdaykeeper.core.exceptions;

public class EntityAlreadyExistsException extends EntityGenericException {
    private final static String DEFAULT_CODE = "AlreadyExists";

    public EntityAlreadyExistsException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}

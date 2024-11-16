package mkoutra.birthdaykeeper.core.exceptions;

public class EntityInvalidArgumentException extends EntityGenericException {
    private final static String DEFAULT_CODE = "InvalidArgument";

    public EntityInvalidArgumentException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}

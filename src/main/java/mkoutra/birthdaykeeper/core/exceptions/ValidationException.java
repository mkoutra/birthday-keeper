package mkoutra.birthdaykeeper.core.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ValidationException extends Exception {
    private final BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        super("Validation Exception");
        this.bindingResult = bindingResult;
    }
}

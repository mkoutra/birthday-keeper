package mkoutra.birthdaykeeper.core;

import mkoutra.birthdaykeeper.core.exceptions.*;
import mkoutra.birthdaykeeper.dto.errorDTO.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppServerException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<ErrorResponseDTO> handleAppServerException(AppServerException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.CONFLICT)
    ResponseEntity<ErrorResponseDTO> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityInvalidArgumentException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponseDTO> handleEntityInvalidArgumentException(EntityInvalidArgumentException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    @ResponseStatus(code = HttpStatus.FORBIDDEN)
//    ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
//        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("AccessDenied", ex.getMessage());
//        return new ResponseEntity<>(errorResponseDTO, HttpStatus.FORBIDDEN);
//    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("AuthenticationException", ex.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Helper method to build a ResponseEntity for exception responses.
     *
     * @param ex     The exception containing the error details.
     * @param status The HTTP status to be returned.
     * @return       A ResponseEntity containing the ErrorResponseDTO and status.
     */
    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(EntityGenericException ex, HttpStatus status) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponseDTO, status);
    }
}

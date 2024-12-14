package mkoutra.birthdaykeeper.core;

import mkoutra.birthdaykeeper.core.exceptions.*;
import mkoutra.birthdaykeeper.dto.errorDTO.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(EntityGenericException.class)
    ResponseEntity<ErrorResponseDTO> handleEntityGenericException(EntityGenericException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(ex.getCode(), ex.getMessage());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        if (ex instanceof AppServerException) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof EntityAlreadyExistsException) {
            httpStatus = HttpStatus.CONFLICT;
        } else if (ex instanceof EntityInvalidArgumentException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof EntityNotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof UserNotAuthenticatedException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(errorResponseDTO, httpStatus);
    }

// TODO

//    @ExceptionHandler(AppServerException.class)
//    public ResponseEntity<ErrorResponseDTO> handleAppServerException(AppServerException ex) {
//        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(EntityAlreadyExistsException.class)
//    public ResponseEntity<ErrorResponseDTO> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
//        return buildErrorResponse(ex, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(EntityInvalidArgumentException.class)
//    public ResponseEntity<ErrorResponseDTO> handleEntityInvalidArgumentException(EntityInvalidArgumentException ex) {
//        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
//        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(UserNotAuthenticatedException.class)
//    public ResponseEntity<ErrorResponseDTO> handleUserNotAuthenticatedException(UserNotAuthenticatedException ex) {
//        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
//    }
//
//    @ExceptionHandler(EntityGenericException.class)
//    public ResponseEntity<ErrorResponseDTO> handleEntityGenericException(EntityGenericException ex) {
//        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * Helper method to build a ResponseEntity for exception responses.
//     *
//     * @param ex     The exception containing the error details.
//     * @param status The HTTP status to be returned.
//     * @return       A ResponseEntity containing the ErrorResponseDTO and status.
//     */
//    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(EntityGenericException ex, HttpStatus status) {
//        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(ex.getCode(), ex.getMessage());
//        return new ResponseEntity<>(errorResponseDTO, status);
//    }

}

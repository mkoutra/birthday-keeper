package mkoutra.birthdaykeeper.core;

import mkoutra.birthdaykeeper.core.exceptions.*;
import mkoutra.birthdaykeeper.dto.errorDTO.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

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
        }

        return new ResponseEntity<ErrorResponseDTO>(errorResponseDTO, httpStatus);
    }

}

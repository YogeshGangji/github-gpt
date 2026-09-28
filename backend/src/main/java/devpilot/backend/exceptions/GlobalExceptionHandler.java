package devpilot.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex){
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(BadRequestExceptions.class)
    ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestExceptions ex){
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex){
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map( err -> err.getField()  + ": "+ err.getDefaultMessage())
                .orElse("Validation Failed");
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> handleException(Exception ex){
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "Unexpected Error");
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus  status,String message){
        return ResponseEntity.status(status)
                .body(Map.of(
                        "status",status.value(),
                        "error",status.getReasonPhrase(),
                        "message",message,
                        "timstamp", Instant.now().toString()
                ));
    }
}

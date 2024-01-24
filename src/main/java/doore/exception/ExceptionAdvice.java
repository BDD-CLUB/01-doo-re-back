package doore.exception;

import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = getErrorMessage(e);
        return ResponseEntity.badRequest().body(new ExceptionResponse(errorMessage));
    }

    private static String getErrorMessage(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        return bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> getErrorMessage(
                        fieldError.getField(),
                        (String) fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage())
                )
                .collect(Collectors.joining(", "));
    }

    public static String getErrorMessage(
            String errorField,
            String invalidValue,
            String errorMessage
    ) {
        return "[%s] %s : %s".formatted(errorField, invalidValue, errorMessage);
    }
}

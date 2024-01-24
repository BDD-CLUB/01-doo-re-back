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
    public ResponseEntity<ExceptionResponse> methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final String errorMessage = getErrorMessage(e);
        return ResponseEntity.badRequest().body(new ExceptionResponse(errorMessage));
    }

    private static String getErrorMessage(final BindException e) {
        final BindingResult bindingResult = e.getBindingResult();
        return bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> getErrorMessage(
                        fieldError.getField(),
                        (String) fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage())
                )
                .collect(Collectors.joining(", "));
    }

    private static String getErrorMessage(
            final String errorField,
            final String invalidValue,
            final String errorMessage
    ) {
        return "[%s] %s : %s".formatted(errorField, invalidValue, errorMessage);
    }
}

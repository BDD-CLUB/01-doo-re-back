package doore.login.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum LoginExceptionType implements BaseExceptionType {
    NOT_FOUND_GOOGLE_ACCESS_TOKEN(HttpStatus.NOT_FOUND, "응답 값이 비어있습니다.");
    // TODO: 1/23/24

    private final HttpStatus httpStatus;
    private final String errorMessage;

    LoginExceptionType(final HttpStatus httpStatus, final String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}

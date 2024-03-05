package doore.attendance.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AttendanceExceptionType implements BaseExceptionType {
    ALREADY_ATTENDED(HttpStatus.BAD_REQUEST, "이미 출석한 회원입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    AttendanceExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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

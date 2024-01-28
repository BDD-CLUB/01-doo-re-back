package doore.study.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum StudyExceptionType implements BaseExceptionType {
    NOT_FOUND_STUDY(HttpStatus.NOT_FOUND, "스터디를 찾을 수 없습니다."),
    NOT_FOUND_STATUS(HttpStatus.BAD_REQUEST, "존재하지 않는 스터디 상태입니다."),
    ALREADY_TERMINATED_STUDY(HttpStatus.FORBIDDEN, "이미 종료된 스터디입니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    StudyExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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

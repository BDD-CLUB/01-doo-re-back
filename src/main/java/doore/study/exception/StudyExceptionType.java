package doore.study.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum StudyExceptionType implements BaseExceptionType {
    NOT_FOUND_STUDY(HttpStatus.NOT_FOUND, "스터디를 찾을 수 없습니다."),
    NOT_FOUND_STATUS(HttpStatus.BAD_REQUEST, "존재하지 않는 스터디 상태입니다."),
    INVALID_ENDDATE(HttpStatus.BAD_REQUEST, "시작일보다 빠른 날짜에 종료할 수 없습니다.");

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

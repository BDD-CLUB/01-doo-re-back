package doore.member.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ParticipantExceptionType implements BaseExceptionType {

    CANNOT_DELETE_STUDY_LEADER(HttpStatus.BAD_REQUEST, "스터디장 본인은 팀을 탈퇴할 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    ParticipantExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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

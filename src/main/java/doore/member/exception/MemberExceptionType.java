package doore.member.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements BaseExceptionType {
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    NOT_FOUND_MEMBER_ROLE_IN_TEAM(HttpStatus.NOT_FOUND, "팀 내 해당 회원의 직위를 찾을 수 없습니다."),
    NOT_FOUND_MEMBER_ROLE_IN_STUDY(HttpStatus.NOT_FOUND, "스터디 내 해당 회원의 직위를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    MemberExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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


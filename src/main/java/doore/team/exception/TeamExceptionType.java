package doore.team.exception;

import doore.base.BaseExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TeamExceptionType implements BaseExceptionType {

    NOT_FOUND_TEAM(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."),
    NOT_MATCH_LINK(HttpStatus.BAD_REQUEST, "초대 링크가 일치하지 않습니다."),
    EXPIRED_LINK(HttpStatus.BAD_REQUEST, "만료된 초대 링크입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}

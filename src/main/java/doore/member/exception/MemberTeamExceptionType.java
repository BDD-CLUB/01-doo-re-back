package doore.member.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberTeamExceptionType implements BaseExceptionType {

    CANNOT_DELETE_TEAM_LEADER_SELF(HttpStatus.BAD_REQUEST, "팀장 본인은 팀을 탈퇴할 수 없습니다."),
    CANNOT_DELETE_STUDY_LEADER(HttpStatus.BAD_REQUEST, "스터디장을 맡고 있다면 팀원 삭제 및 탈퇴가 불가능합니다.(%s)"),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    MemberTeamExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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

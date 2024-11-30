package doore.member.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class MemberTeamException extends BaseException {
    private final MemberTeamExceptionType exceptionType;

    public MemberTeamException(final MemberTeamExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

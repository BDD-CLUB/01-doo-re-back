package doore.member.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class MemberException extends BaseException {
    private final MemberExceptionType exceptionType;

    public MemberException(final MemberExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

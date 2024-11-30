package doore.member.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class ParticipantException extends BaseException {

    private final ParticipantExceptionType exceptionType;

    public ParticipantException(final ParticipantExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

package doore.team.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class TeamException extends BaseException {

    private final TeamExceptionType exceptionType;

    public TeamException(final TeamExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

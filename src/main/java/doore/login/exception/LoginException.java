package doore.login.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class LoginException extends BaseException {
    private final LoginExceptionType exceptionType;

    public LoginException(final LoginExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

package doore.study.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class StudyException extends BaseException {
    private final StudyExceptionType exceptionType;

    public StudyException(final StudyExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

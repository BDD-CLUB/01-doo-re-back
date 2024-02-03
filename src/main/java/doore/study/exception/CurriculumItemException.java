package doore.study.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class CurriculumItemException extends BaseException {

    private final CurriculumItemExceptionType exceptionType;

    public CurriculumItemException(CurriculumItemExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

package doore.document.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class DocumentException extends BaseException {
    private final DocumentExceptionType exceptionType;

    public DocumentException(final DocumentExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

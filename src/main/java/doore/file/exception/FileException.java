package doore.file.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class FileException extends BaseException {

    private final FileExceptionType exceptionType;

    public FileException(final FileExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

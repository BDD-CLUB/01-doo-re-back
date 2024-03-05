package doore.crop.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class CropException extends BaseException {
    private final CropExceptionType exceptionType;

    public CropException(final CropExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

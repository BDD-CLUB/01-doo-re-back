package doore.crop.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum CropExceptionType implements BaseExceptionType {
    NOT_FOUND_CROP(HttpStatus.NOT_FOUND, "작물을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    CropExceptionType(final HttpStatus httpStatus, final String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}

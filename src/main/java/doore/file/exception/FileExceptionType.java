package doore.file.exception;

import doore.base.BaseExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FileExceptionType implements BaseExceptionType {

    INVALID_IMAGE_FILE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다."),
    FAIL_UPLOAD_IMAGE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지를 S3에 업로드하지 못했습니다."),
    FAIL_UPLOAD_DOCUMENT_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 S3에 업로드하지 못했습니다."),
    FILE_IS_NULL(HttpStatus.BAD_REQUEST, "파일은 Null일 수 없습니다."),
    INVALID_FILE_ACCESS(HttpStatus.BAD_REQUEST, "파일에 접근할 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}

package doore.document.exception;

import doore.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum DocumentExceptionType implements BaseExceptionType {
    NOT_FOUND_DOCUMENT(HttpStatus.NOT_FOUND, "존재하지 않는 자료입니다."),
    INVALID_DOCUMENT_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 자료 유형입니다."),
    LINK_DOCUMENT_NEEDS_FILE(HttpStatus.BAD_REQUEST,"링크 학습자료에는 링크가 입력되어야 합니다."),
    NO_FILE_ATTACHED(HttpStatus.BAD_REQUEST, "학습자료에 첨부된 파일이 없습니다."),
    NOT_FOUND_GROUP_TYPE(HttpStatus.BAD_REQUEST, "존재하지 않는 그룹입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    DocumentExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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

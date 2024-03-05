package doore.study.exception;

import doore.base.BaseExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CurriculumItemExceptionType implements BaseExceptionType {

    NOT_FOUND_CURRICULUM_ITEM(HttpStatus.NOT_FOUND, "존재하지 않는 커리큘럼입니다."),
    INVALID_ITEM_ORDER(HttpStatus.BAD_REQUEST, "유효하지 않은 아이템 순서입니다."),
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

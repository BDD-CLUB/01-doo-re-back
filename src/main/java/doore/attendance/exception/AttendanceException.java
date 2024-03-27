package doore.attendance.exception;

import doore.base.BaseException;
import doore.base.BaseExceptionType;

public class AttendanceException extends BaseException {

    private final AttendanceExceptionType exceptionType;

    public AttendanceException(AttendanceExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

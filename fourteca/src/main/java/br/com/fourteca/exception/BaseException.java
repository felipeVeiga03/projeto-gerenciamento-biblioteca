package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;
import lombok.Generated;

public class BaseException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public BaseException(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMessage());
        this.errorEnum = errorEnum;
    }

    @Generated
    public ErrorEnum getErrorEnum() {
        return this.errorEnum;
    }
}

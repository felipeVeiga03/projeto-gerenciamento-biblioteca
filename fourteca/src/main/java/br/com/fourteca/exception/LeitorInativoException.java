package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class LeitorInativoException extends BaseException {
    public LeitorInativoException() {
        super(ErrorEnum.LEITOR_INATIVO);
    }
}

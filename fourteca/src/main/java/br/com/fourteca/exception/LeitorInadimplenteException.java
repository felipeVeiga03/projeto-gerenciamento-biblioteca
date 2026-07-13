package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class LeitorInadimplenteException extends BaseException {
    public LeitorInadimplenteException() {
        super(ErrorEnum.LEITOR_INADIMPLENTE);
    }
}

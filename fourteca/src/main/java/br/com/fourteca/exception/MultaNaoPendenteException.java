package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class MultaNaoPendenteException extends BaseException {
    public MultaNaoPendenteException() {
        super(ErrorEnum.MULTA_NAO_PENDENTE);
    }
}

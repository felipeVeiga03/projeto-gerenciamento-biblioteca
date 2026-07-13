package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class ReservaJaExistenteException extends BaseException {
    public ReservaJaExistenteException() {
        super(ErrorEnum.RESERVA_JA_EXISTENTE);
    }
}

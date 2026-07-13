package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class ReservaNaoPodeSerCanceladaException extends BaseException {
    public ReservaNaoPodeSerCanceladaException() {
        super(ErrorEnum.RESERVA_NAO_PODE_SER_CANCELADA);
    }
}

package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class LeitorNaoEncontradoException extends BaseException {
    public LeitorNaoEncontradoException() {
        super(ErrorEnum.LEITOR_NAO_ENCONTRADO);
    }
}

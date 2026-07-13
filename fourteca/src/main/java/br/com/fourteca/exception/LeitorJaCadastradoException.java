package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class LeitorJaCadastradoException extends BaseException {
    public LeitorJaCadastradoException() {
        super(ErrorEnum.LEITOR_JA_CADASTRADO);
    }
}

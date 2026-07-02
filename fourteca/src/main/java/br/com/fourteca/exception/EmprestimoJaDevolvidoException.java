package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class EmprestimoJaDevolvidoException extends BaseException {
    public EmprestimoJaDevolvidoException() {
        super(ErrorEnum.EMPRESTIMO_JA_DEVOLVIDO);
    }
}

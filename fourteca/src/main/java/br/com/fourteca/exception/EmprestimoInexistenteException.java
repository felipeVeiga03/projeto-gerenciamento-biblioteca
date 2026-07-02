package br.com.fourteca.exception;

import static br.com.fourteca.exceptionHandlers.ErrorEnum.EMPRESTIMO_INEXISTENTE;

public class EmprestimoInexistenteException extends BaseException {
    public EmprestimoInexistenteException( ) {
        super(EMPRESTIMO_INEXISTENTE);
    }
}

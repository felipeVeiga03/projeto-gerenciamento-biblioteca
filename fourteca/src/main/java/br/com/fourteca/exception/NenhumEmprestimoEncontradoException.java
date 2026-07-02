package br.com.fourteca.exception;

import static br.com.fourteca.exceptionHandlers.ErrorEnum.NENHUM_EMPRESTIMO;

public class NenhumEmprestimoEncontradoException extends BaseException {
    public NenhumEmprestimoEncontradoException() {super(NENHUM_EMPRESTIMO);}
}

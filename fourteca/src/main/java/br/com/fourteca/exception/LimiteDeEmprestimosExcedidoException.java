package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class LimiteDeEmprestimosExcedidoException extends BaseException {
    public LimiteDeEmprestimosExcedidoException() {
        super(ErrorEnum.LIMITE_EMPRESTIMOS_EXCEDIDO);
    }
}

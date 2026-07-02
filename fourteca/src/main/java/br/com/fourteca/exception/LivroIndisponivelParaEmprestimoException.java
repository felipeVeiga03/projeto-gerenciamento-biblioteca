package br.com.fourteca.exception;

import static br.com.fourteca.exceptionHandlers.ErrorEnum.LIVRO_INDISPONIVEL;

public class LivroIndisponivelParaEmprestimoException extends BaseException {
    public LivroIndisponivelParaEmprestimoException( ) {
        super(LIVRO_INDISPONIVEL);
    }
}

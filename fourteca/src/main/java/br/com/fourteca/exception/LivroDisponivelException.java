package br.com.fourteca.exception;

import br.com.fourteca.exceptionHandlers.ErrorEnum;

public class LivroDisponivelException extends BaseException {
    public LivroDisponivelException() {
        super(ErrorEnum.LIVRO_DISPONIVEL);
    }
}

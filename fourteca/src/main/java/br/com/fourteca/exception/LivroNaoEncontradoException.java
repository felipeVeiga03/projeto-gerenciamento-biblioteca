package br.com.fourteca.exception;

import static br.com.fourteca.exceptionHandlers.ErrorEnum.LIVRO_INEXISTENTE;

public class LivroNaoEncontradoException extends BaseException {
    public LivroNaoEncontradoException() {
        super(LIVRO_INEXISTENTE);
    }
}

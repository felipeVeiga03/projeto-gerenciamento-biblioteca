package br.com.fourteca.exception;

import static br.com.fourteca.exceptionHandlers.ErrorEnum.LIVRO_EXISTENTE;

public class LivroJaCadastradoException extends BaseException {
    public LivroJaCadastradoException() {
        super(LIVRO_EXISTENTE);
    }//409
}

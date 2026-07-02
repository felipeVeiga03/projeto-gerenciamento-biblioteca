package br.com.fourteca.exception;

import static br.com.fourteca.exceptionHandlers.ErrorEnum.LIVRO_EXISTENTE;

public class LivroJaCadastroadoException extends BaseException {
    public LivroJaCadastroadoException() {
        super(LIVRO_EXISTENTE);
    }//409
}

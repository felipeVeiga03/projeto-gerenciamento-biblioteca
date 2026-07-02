package br.com.fourteca.exceptionHandlers;

import lombok.Generated;

public enum ErrorEnum {
    ERRO_GENERICO(500, 1, "Algo deu errado, tente novamente mais tarde"),
    LIVRO_EXISTENTE(400,100,"Livro já existente"),
    LIVRO_INEXISTENTE(404,100,"Livro inexistente"),
    LIVRO_INDISPONIVEL(400,100,"Livro não está disponível para empréstimo"),
    EMPRESTIMO_INEXISTENTE(404,100,"Empréstimo não encontrado"),
    NENHUM_EMPRESTIMO(404,100,"Nenhum empréstimo encontrado"),
    EMPRESTIMO_JA_DEVOLVIDO(400, 100, "Este empréstimo já foi devolvido"),
    ERRO_VALIDACAO(400, 100,"Erro de validação nos dados enviados");

    private final int httpStatus;
    private final int errorCode;
    private final String errorMessage;

    private ErrorEnum(int httpStatus, int errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Generated
    public int getHttpStatus() {
        return this.httpStatus;
    }

    @Generated
    public int getErrorCode() {
        return this.errorCode;
    }

    @Generated
    public String getErrorMessage() {
        return this.errorMessage;
    }
}

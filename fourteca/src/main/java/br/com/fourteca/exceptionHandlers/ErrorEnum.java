package br.com.fourteca.exceptionHandlers;

import lombok.Generated;

public enum ErrorEnum {
    ERRO_GENERICO(500, 1, "Algo deu errado, tente novamente mais tarde"),
    LIVRO_EXISTENTE(400,100,"Livro já existente"),
    LIVRO_INEXISTENTE(404,100,"Livro inexistente"),
    LIVRO_INDISPONIVEL(400,100,"Livro não está disponível para empréstimo"),
    LIVRO_DISPONIVEL(400, 101, "Livro já está disponível e não pode ser reservado"),
    RESERVA_JA_EXISTENTE(400, 400, "O leitor já possui uma reserva ativa para este livro"),
    RESERVA_NAO_PODE_SER_CANCELADA(400, 401, "A reserva não pode ser cancelada pois já está aguardando retirada ou foi atendida"),
    EMPRESTIMO_INEXISTENTE(404,100,"Empréstimo não encontrado"),
    NENHUM_EMPRESTIMO(404,100,"Nenhum empréstimo encontrado"),
    EMPRESTIMO_JA_DEVOLVIDO(400, 100, "Este empréstimo já foi devolvido"),
    ERRO_VALIDACAO(400, 100,"Erro de validação nos dados enviados"),
    LEITOR_NAO_ENCONTRADO(404, 200, "Leitor não encontrado"),
    LEITOR_JA_CADASTRADO(400, 201, "Leitor já cadastrado com este documento ou email"),
    LEITOR_INATIVO(400, 202, "O leitor está inativo e não pode realizar empréstimos"),
    LIMITE_EMPRESTIMOS_EXCEDIDO(400, 203, "O leitor atingiu o número máximo de empréstimos simultâneos"),
    MULTA_NAO_PENDENTE(400, 300, "Não há multa pendente para este empréstimo"),
    LEITOR_INADIMPLENTE(400, 204, "O leitor possui multas pendentes e não pode realizar novos empréstimos");


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

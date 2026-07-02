package br.com.fourteca.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmprestimoResponse {
    private Integer idEmprestimo;
    private String nomeLeitor;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataEfetivaDevolucao;
    private Integer idLivro;
}

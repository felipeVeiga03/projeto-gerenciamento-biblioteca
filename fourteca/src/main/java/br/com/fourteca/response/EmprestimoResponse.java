package br.com.fourteca.response;

import br.com.fourteca.enums.StatusMulta;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmprestimoResponse {
    private Long idEmprestimo;
    private Long idLeitor;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataEfetivaDevolucao;
    private Long idLivro;
    private Integer diasAtraso;
    private BigDecimal valorMulta;
    private StatusMulta statusMulta;
}

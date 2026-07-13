package br.com.fourteca.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoDetalheResponse {
    private Long idEmprestimo;
    private String tituloLivro;
    private LocalDate dataPrevistaDevolucao;
    private Integer diasAtraso;
    private BigDecimal valorMulta;
}

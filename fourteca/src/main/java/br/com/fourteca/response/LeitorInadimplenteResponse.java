package br.com.fourteca.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeitorInadimplenteResponse {
    private Long idLeitor;
    private String nome;
    private String documento;
    private String email;
    private BigDecimal valorTotalMultaPendente;
    private List<EmprestimoDetalheResponse> emprestimosAtrasados;
}

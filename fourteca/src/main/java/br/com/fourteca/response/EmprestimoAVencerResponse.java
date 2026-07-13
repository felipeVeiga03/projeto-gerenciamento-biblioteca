package br.com.fourteca.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoAVencerResponse {
    private Long idEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private String tituloLivro;
    private String nomeLeitor;
    private String emailLeitor;
}

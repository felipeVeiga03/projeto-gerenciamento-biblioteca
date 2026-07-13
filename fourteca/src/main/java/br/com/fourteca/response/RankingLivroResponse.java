package br.com.fourteca.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingLivroResponse {
    private Long idLivro;
    private String titulo;
    private String autor;
    private Long totalEmprestimos;
}

package br.com.fourteca.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoRequest {
    @NotBlank(message = "O nome do leitor não pode estar em branco")
    private String nomeLeitor;

    @NotNull(message = "O ID do livro é obrigatório")
    private Integer idLivro;
}

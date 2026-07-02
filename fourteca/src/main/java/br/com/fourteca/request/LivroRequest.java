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
public class LivroRequest {
    @NotBlank(message = "O autor não pode estar em branco")
    private String autor;

    @NotBlank(message = "O título não pode estar em branco")
    private String titulo;

    @NotBlank(message = "O ISBN não pode estar em branco")
    private String isbn;

    @NotNull(message = "O campo 'disponivel' é obrigatório")
    private Boolean disponivel;
}

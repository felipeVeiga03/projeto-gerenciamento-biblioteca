package br.com.fourteca.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {
    @NotNull(message = "O ID do livro é obrigatório")
    private Long idLivro;

    @NotNull(message = "O ID do leitor é obrigatório")
    private Long idLeitor;
}

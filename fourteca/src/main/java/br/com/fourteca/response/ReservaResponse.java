package br.com.fourteca.response;

import br.com.fourteca.enums.StatusReserva;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservaResponse {
    private Long idReserva;
    private Long idLivro;
    private String tituloLivro;
    private Long idLeitor;
    private LocalDateTime dataCriacao;
    private StatusReserva status;
    private LocalDateTime dataAtendimento;
}

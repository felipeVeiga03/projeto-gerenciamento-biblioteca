package br.com.fourteca.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditoriaResponse {
    private Long id;
    private Long usuarioId;
    private String acao;
    private String nomeEntidade;
    private Long entidadeId;
    private LocalDateTime timestamp;
}

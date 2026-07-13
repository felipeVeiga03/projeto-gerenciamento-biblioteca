package br.com.fourteca.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Não inclui campos nulos no JSON final
public class ErrorResponse {
    private String codError;
    private String msgError;
    private Object details;
    private LocalDateTime timestamp;
    private String path;
}

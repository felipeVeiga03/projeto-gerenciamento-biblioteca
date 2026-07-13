package br.com.fourteca.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationErrorDetail {
    private String campo;
    private String mensagem;
}

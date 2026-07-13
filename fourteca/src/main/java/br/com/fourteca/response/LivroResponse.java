package br.com.fourteca.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LivroResponse {
    private Long idLivro;
    private String titulo;
    private String autor;
    private String isbn;
    private boolean disponivel;
}

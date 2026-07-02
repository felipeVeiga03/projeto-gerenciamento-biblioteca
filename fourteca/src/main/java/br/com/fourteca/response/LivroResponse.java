package br.com.fourteca.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LivroResponse {
    private Integer idLivro;
    private String autor;
    private String titulo;
    private String isbn;
    private boolean disponivel;

}

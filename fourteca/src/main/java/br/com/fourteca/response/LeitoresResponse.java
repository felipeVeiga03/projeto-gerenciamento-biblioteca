package br.com.fourteca.response;

import br.com.fourteca.enums.StatusLeitor;
import br.com.fourteca.enums.TipoLeitor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeitoresResponse {
    private Long idLeitor;
    private String documento;
    private String email;
    private TipoLeitor tipo;
    private StatusLeitor status;
    private Integer emprestimosAtivos;
}

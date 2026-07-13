package br.com.fourteca.request;

import br.com.fourteca.enums.StatusLeitor;
import br.com.fourteca.enums.TipoLeitor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeitoresRequest {

    @NotBlank(message = "O documento não pode estar em branco")
    private String documento;

    @NotBlank(message = "O email não pode estar em branco")
    @Email(message = "O formato do email é inválido")
    private String email;

    @NotNull(message = "O tipo de leitor é obrigatório")
    private TipoLeitor tipo;

    @NotNull(message = "O status do leitor é obrigatório")
    private StatusLeitor status;
}

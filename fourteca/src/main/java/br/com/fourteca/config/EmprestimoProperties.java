package br.com.fourteca.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "fourteca.emprestimo")
public class EmprestimoProperties {
    private Map<String, Integer> diasPorTipoLeitor;
    private Map<String, Integer> maxLivrosPorTipoLeitor;
    private BigDecimal taxaMultaDiaria;
}

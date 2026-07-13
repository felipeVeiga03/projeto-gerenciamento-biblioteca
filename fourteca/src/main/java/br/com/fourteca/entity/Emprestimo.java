package br.com.fourteca.entity;

import br.com.fourteca.enums.StatusMulta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emprestimo")
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_emprestimo")
    private LocalDate dataEmprestimo;

    @Column(name = "data_prevista_devolucao")
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_efetiva_devolucao")
    private LocalDate dataEfetivaDevolucao;

    @ManyToOne
    @JoinColumn(name = "id_livro")
    private Livro livro;

    @ManyToOne
    @JoinColumn(name = "id_leitor")
    private Leitores leitor;

    @Column(name = "dias_atraso")
    private Integer diasAtraso;

    @Column(name = "valor_multa")
    private BigDecimal valorMulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_multa")
    private StatusMulta statusMulta;

}

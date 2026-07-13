package br.com.fourteca.entity;

import br.com.fourteca.enums.StatusLeitor;
import br.com.fourteca.enums.TipoLeitor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leitores")
public class Leitores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String documento;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLeitor tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLeitor status;

    @OneToMany(mappedBy = "leitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emprestimo> emprestimos;

}

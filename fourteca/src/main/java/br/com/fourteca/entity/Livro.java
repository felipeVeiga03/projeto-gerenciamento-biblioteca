package br.com.fourteca.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "livro")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "autor")
    private String autor;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "disponivel")
    private boolean disponivel;

    @OneToMany(mappedBy = "livro")
    private List<Emprestimo> emprestimos;

    public Livro(String livroTeste, String autorTeste, String number, boolean b) {
    }
}

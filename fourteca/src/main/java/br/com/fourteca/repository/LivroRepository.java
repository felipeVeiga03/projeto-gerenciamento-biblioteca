package br.com.fourteca.repository;

import br.com.fourteca.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {
    boolean existsByIsbn(String isbnRecebido);

    List<Livro> findByAutor(String autor);

    List<Livro> findByDisponivel(boolean disponivel);

    List<Livro> findByAutorAndDisponivel(String autor, boolean disponivel);
}

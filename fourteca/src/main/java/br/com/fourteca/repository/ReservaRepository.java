package br.com.fourteca.repository;

import br.com.fourteca.entity.Leitores;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.entity.Reserva;
import br.com.fourteca.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByLivroAndLeitorAndStatus(Livro livro, Leitores leitor, StatusReserva status);
    List<Reserva> findByLivroAndStatusOrderByDataCriacaoAsc(Livro livro, StatusReserva status);
}

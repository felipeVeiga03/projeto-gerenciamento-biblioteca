package br.com.fourteca.repository;

import br.com.fourteca.entity.Leitores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeitoresRepository extends JpaRepository<Leitores, Long> {

    boolean existsByDocumento(String documento);

    boolean existsByEmail(String email);

    Optional<Leitores> findByDocumento(String documento);

    Optional<Leitores> findByEmail(String email);
}

package br.com.fourteca.repository;

import br.com.fourteca.entity.Role;
import br.com.fourteca.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNome(Perfil nome);
}

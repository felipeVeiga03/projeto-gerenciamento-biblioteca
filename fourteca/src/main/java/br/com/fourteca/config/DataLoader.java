package br.com.fourteca.config;

import br.com.fourteca.entity.Leitores;
import br.com.fourteca.entity.Role;
import br.com.fourteca.entity.Usuario;
import br.com.fourteca.enums.Perfil;
import br.com.fourteca.enums.StatusLeitor;
import br.com.fourteca.enums.TipoLeitor;
import br.com.fourteca.repository.LeitoresRepository;
import br.com.fourteca.repository.RoleRepository;
import br.com.fourteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private LeitoresRepository leitoresRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, Perfil.ROLE_ADMIN));
            roleRepository.save(new Role(null, Perfil.ROLE_BIBLIOTECARIO));
            roleRepository.save(new Role(null, Perfil.ROLE_LEITOR));
        }

        if (usuarioRepository.count() == 0) {
            // Cria um Leitor para o Admin
            Leitores adminLeitor = new Leitores();
            adminLeitor.setNome("Administrador");
            adminLeitor.setEmail("admin@fourteca.com.br");
            adminLeitor.setDocumento("00000000000");
            adminLeitor.setDataNascimento(LocalDate.now());
            adminLeitor.setTipo(TipoLeitor.PROFESSOR); // Apenas um tipo padrão
            adminLeitor.setStatus(StatusLeitor.ATIVO);
            leitoresRepository.save(adminLeitor);

            // Cria o Usuário Admin e associa ao Leitor
            Role adminRole = roleRepository.findByNome(Perfil.ROLE_ADMIN).orElseThrow();
            Usuario usuario = new Usuario();
            usuario.setLogin("admin");
            usuario.setSenha(passwordEncoder.encode("password"));
            usuario.setRoles(Set.of(adminRole));
            usuario.setLeitor(adminLeitor);
            usuarioRepository.save(usuario);
        }
    }
}

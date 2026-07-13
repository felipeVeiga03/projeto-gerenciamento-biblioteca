package br.com.fourteca.service;

import br.com.fourteca.config.EmprestimoProperties;
import br.com.fourteca.config.WithMockCustomUser;
import br.com.fourteca.entity.Emprestimo;
import br.com.fourteca.entity.Leitores;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.entity.Reserva;
import br.com.fourteca.enums.StatusLeitor;
import br.com.fourteca.enums.StatusMulta;
import br.com.fourteca.enums.StatusReserva;
import br.com.fourteca.enums.TipoLeitor;
import br.com.fourteca.exception.LimiteDeEmprestimosExcedidoException;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.repository.LeitoresRepository;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.repository.ReservaRepository;
import br.com.fourteca.request.EmprestimoRequest;
import br.com.fourteca.response.EmprestimoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@WithMockCustomUser // Adiciona um usuário mockado no contexto de segurança
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EmprestimoServiceIntegrationTest {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private LeitoresRepository leitoresRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EmprestimoProperties emprestimoProperties;

    private Leitores leitor;
    private Livro livro;

    @BeforeEach
    void setUp() {
        leitor = new Leitores();
        leitor.setNome("Leitor Teste");
        leitor.setEmail("leitor@teste.com");
        leitor.setDocumento("123456");
        leitor.setTipo(TipoLeitor.ALUNO);
        leitor.setStatus(StatusLeitor.ATIVO);
        leitor.setDataNascimento(LocalDate.of(1990, 1, 1));
        leitor = leitoresRepository.save(leitor);

        livro = new Livro();
        livro.setTitulo("Livro Teste");
        livro.setAutor("Autor Teste");
        livro.setIsbn("123456");
        livro.setDisponivel(true);
        livro = livroRepository.save(livro);
    }

    @Test
    void deveRealizarEmprestimoComSucesso() {
        EmprestimoRequest request = new EmprestimoRequest(leitor.getId(), livro.getId());
        EmprestimoResponse response = emprestimoService.registrarEmprestimo(request);

        assertNotNull(response);
        assertEquals(livro.getId(), response.getIdLivro());
        assertEquals(leitor.getId(), response.getIdLeitor());

        var emprestimo = emprestimoRepository.findById(response.getIdEmprestimo()).orElseThrow();
        assertFalse(emprestimo.getLivro().isDisponivel());
        
        Integer dias = emprestimoProperties.getDiasPorTipoLeitor().get(leitor.getTipo().name());
        assertEquals(LocalDate.now().plusDays(dias), emprestimo.getDataPrevistaDevolucao());
    }

    @Test
    void deveCalcularMultaParaDevolucaoEmAtraso() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLeitor(leitor);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDate.now().minusDays(15));
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(5)); // 5 dias de atraso
        emprestimo = emprestimoRepository.save(emprestimo);

        EmprestimoResponse response = emprestimoService.devolverLivro(emprestimo.getId());

        assertEquals(StatusMulta.MULTA_PENDENTE, response.getStatusMulta());
        assertEquals(5, response.getDiasAtraso());
        assertTrue(response.getValorMulta().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void deveBloquearEmprestimoQuandoLimiteExcedido() {
        Integer limite = emprestimoProperties.getMaxLivrosPorTipoLeitor().get(leitor.getTipo().name());
        
        // Cria empréstimos até atingir o limite
        for (int i = 0; i < limite; i++) {
            Livro novoLivro = new Livro();
            novoLivro.setTitulo("Livro Extra " + i);
            novoLivro.setAutor("Autor");
            novoLivro.setIsbn("ISBN" + i);
            novoLivro.setDisponivel(true);
            novoLivro = livroRepository.save(novoLivro);
            emprestimoService.registrarEmprestimo(new EmprestimoRequest(leitor.getId(), novoLivro.getId()));
        }

        Livro livroExtra = new Livro();
        livroExtra.setTitulo("Livro Extra");
        livroExtra.setAutor("Autor");
        livroExtra.setIsbn("ISBN-EXTRA");
        livroExtra.setDisponivel(true);
        livroExtra = livroRepository.save(livroExtra);

        EmprestimoRequest request = new EmprestimoRequest(leitor.getId(), livroExtra.getId());
        assertThrows(LimiteDeEmprestimosExcedidoException.class, () -> {
            emprestimoService.registrarEmprestimo(request);
        });
    }

    @Test
    void deveAtenderReservaAposDevolucao() {
        EmprestimoResponse emprestimoResponse = emprestimoService.registrarEmprestimo(new EmprestimoRequest(leitor.getId(), livro.getId()));
        assertFalse(livroRepository.findById(livro.getId()).get().isDisponivel());

        Leitores leitorReservante = new Leitores();
        leitorReservante.setNome("Leitor Reservante");
        leitorReservante.setEmail("reservante@teste.com");
        leitorReservante.setDocumento("654321");
        leitorReservante.setTipo(TipoLeitor.PROFESSOR);
        leitorReservante.setStatus(StatusLeitor.ATIVO);
        leitorReservante.setDataNascimento(LocalDate.of(1980, 1, 1));
        leitorReservante = leitoresRepository.save(leitorReservante);

        Reserva reserva = new Reserva();
        reserva.setLeitor(leitorReservante);
        reserva.setLivro(livro);
        reserva.setStatus(StatusReserva.ATIVA);
        reservaRepository.save(reserva);

        emprestimoService.devolverLivro(emprestimoResponse.getIdEmprestimo());

        Reserva reservaAtendida = reservaRepository.findAll().get(0);
        assertEquals(StatusReserva.ATENDIDA, reservaAtendida.getStatus());
        assertNotNull(reservaAtendida.getDataAtendimento());
        assertFalse(livroRepository.findById(livro.getId()).get().isDisponivel());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaEmprestimos() {
        emprestimoRepository.deleteAll();
        List<EmprestimoResponse> emprestimos = emprestimoService.listarEmprestimos();
        assertTrue(emprestimos.isEmpty());
    }
}

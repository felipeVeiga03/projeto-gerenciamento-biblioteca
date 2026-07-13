package br.com.fourteca.service;

import br.com.fourteca.config.EmprestimoProperties;
import br.com.fourteca.entity.Emprestimo;
import br.com.fourteca.entity.Leitores;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.entity.Reserva;
import br.com.fourteca.enums.*;
import br.com.fourteca.exception.*;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.repository.LeitoresRepository;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.repository.ReservaRepository;
import br.com.fourteca.request.EmprestimoRequest;
import br.com.fourteca.response.EmprestimoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock private EmprestimoRepository emprestimoRepository;
    @Mock private LivroRepository livroRepository;
    @Mock private LeitoresRepository leitoresRepository;
    @Mock private ReservaRepository reservaRepository;
    @Mock private EmprestimoProperties emprestimoProperties;
    @InjectMocks private EmprestimoService emprestimoService;

    private Livro livro;
    private Leitores leitor;
    private EmprestimoRequest emprestimoRequest;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setId(1L);
        livro.setDisponivel(true);

        leitor = new Leitores();
        leitor.setId(1L);
        leitor.setTipo(TipoLeitor.ALUNO);
        leitor.setStatus(StatusLeitor.ATIVO);

        emprestimoRequest = new EmprestimoRequest(1L, 1L);
    }

    @ParameterizedTest
    @CsvSource({"ALUNO, 7", "PROFESSOR, 30", "COMUNIDADE, 14"})
    @DisplayName("Deve registrar empréstimo e calcular prazo corretamente para cada tipo de leitor")
    void registrarEmprestimo_ComPrazosDiferentes(TipoLeitor tipoLeitor, int diasDePrazo) {
        leitor.setTipo(tipoLeitor);
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsByLeitorAndStatusMulta(leitor, StatusMulta.MULTA_PENDENTE)).thenReturn(false);
        when(emprestimoProperties.getMaxLivrosPorTipoLeitor()).thenReturn(Map.of(tipoLeitor.name(), 3));
        when(emprestimoRepository.countByLeitorAndDataEfetivaDevolucaoIsNull(leitor)).thenReturn(0L);
        when(emprestimoProperties.getDiasPorTipoLeitor()).thenReturn(Map.of(tipoLeitor.name(), diasDePrazo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(inv -> {
            Emprestimo e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        EmprestimoResponse response = emprestimoService.registrarEmprestimo(emprestimoRequest);

        assertNotNull(response);
        assertEquals(LocalDate.now().plusDays(diasDePrazo), response.getDataPrevistaDevolucao());
    }

    @Test
    void deveLancarExcecaoAoRegistrarParaLivroInexistente() {
        when(livroRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(LivroNaoEncontradoException.class, () -> emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @Test
    void deveLancarExcecaoAoRegistrarParaLeitorInexistente() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(LeitorNaoEncontradoException.class, () -> emprestimoService.registrarEmprestimo(emprestimoRequest));
    }
    
    @Test
    void deveLancarExcecaoAoRegistrarParaLeitorInativo() {
        leitor.setStatus(StatusLeitor.INATIVO);
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        assertThrows(LeitorInativoException.class, () -> emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @Test
    void deveLancarExcecaoAoRegistrarParaLeitorInadimplente() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(emprestimoRepository.existsByLeitorAndStatusMulta(leitor, StatusMulta.MULTA_PENDENTE)).thenReturn(true);
        assertThrows(LeitorInadimplenteException.class, () -> emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @Test
    void deveLancarExcecaoAoRegistrarComLimiteExcedido() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(emprestimoProperties.getMaxLivrosPorTipoLeitor()).thenReturn(Map.of("ALUNO", 2));
        when(emprestimoRepository.countByLeitorAndDataEfetivaDevolucaoIsNull(leitor)).thenReturn(2L);
        assertThrows(LimiteDeEmprestimosExcedidoException.class, () -> emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @Test
    void deveLancarExcecaoAoRegistrarParaLivroIndisponivel() {
        livro.setDisponivel(false);
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(emprestimoProperties.getMaxLivrosPorTipoLeitor()).thenReturn(Map.of("ALUNO", 3));
        assertThrows(LivroIndisponivelParaEmprestimoException.class, () -> emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @Test
    void deveDevolverLivroComAtrasoECalcularMulta() {
        livro.setDisponivel(false);
        Emprestimo emprestimo = new Emprestimo(1L, LocalDate.now().minusDays(15), LocalDate.now().minusDays(5), null, livro, leitor, 0, BigDecimal.ZERO, null);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoProperties.getTaxaMultaDiaria()).thenReturn(new BigDecimal("0.75"));
        when(reservaRepository.findByLivroAndStatusOrderByDataCriacaoAsc(livro, StatusReserva.ATIVA)).thenReturn(Collections.emptyList());
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoResponse response = emprestimoService.devolverLivro(1L);

        assertEquals(StatusMulta.MULTA_PENDENTE, response.getStatusMulta());
        assertEquals(5, response.getDiasAtraso());
        assertEquals(new BigDecimal("3.75"), response.getValorMulta());
        assertTrue(livro.isDisponivel());
    }

    @Test
    void deveDevolverLivroSemAtraso() {
        livro.setDisponivel(false);
        Emprestimo emprestimo = new Emprestimo(1L, LocalDate.now().minusDays(5), LocalDate.now().plusDays(5), null, livro, leitor, 0, BigDecimal.ZERO, null);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(reservaRepository.findByLivroAndStatusOrderByDataCriacaoAsc(livro, StatusReserva.ATIVA)).thenReturn(Collections.emptyList());
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoResponse response = emprestimoService.devolverLivro(1L);

        assertEquals(StatusMulta.SEM_MULTA, response.getStatusMulta());
        assertEquals(0, response.getDiasAtraso());
        assertEquals(BigDecimal.ZERO, response.getValorMulta());
        assertTrue(livro.isDisponivel());
    }
    
    @Test
    void deveLancarExcecaoAoDevolverEmprestimoInexistente() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EmprestimoInexistenteException.class, () -> emprestimoService.devolverLivro(1L));
    }

    @Test
    void deveLancarExcecaoAoDevolverEmprestimoJaDevolvido() {
        Emprestimo emprestimo = new Emprestimo(1L, null, null, LocalDate.now(), livro, leitor, 0, BigDecimal.ZERO, null);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        assertThrows(EmprestimoJaDevolvidoException.class, () -> emprestimoService.devolverLivro(1L));
    }

    @Test
    void deveAtenderReservaAoDevolverLivro() {
        livro.setDisponivel(false);
        Emprestimo emprestimo = new Emprestimo(1L, null, LocalDate.now().plusDays(1), null, livro, leitor, 0, BigDecimal.ZERO, null);
        Reserva reserva = new Reserva(1L, livro, new Leitores(), LocalDateTime.now(), StatusReserva.ATIVA, null);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(reservaRepository.findByLivroAndStatusOrderByDataCriacaoAsc(livro, StatusReserva.ATIVA)).thenReturn(List.of(reserva));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        emprestimoService.devolverLivro(1L);

        assertFalse(livro.isDisponivel());
        assertEquals(StatusReserva.ATENDIDA, reserva.getStatus());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void devePagarMultaComSucesso() {
        Emprestimo emprestimo = new Emprestimo(1L, null, null, null, livro, leitor, 5, BigDecimal.TEN, StatusMulta.MULTA_PENDENTE);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoResponse response = emprestimoService.pagarMulta(1L);

        assertEquals(StatusMulta.MULTA_PAGA, response.getStatusMulta());
    }

    @Test
    void deveLancarExcecaoAoPagarMultaDeEmprestimoInexistente() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EmprestimoInexistenteException.class, () -> emprestimoService.pagarMulta(1L));
    }

    @Test
    void deveLancarExcecaoAoPagarMultaNaoPendente() {
        Emprestimo emprestimo = new Emprestimo(1L, null, null, null, livro, leitor, 0, BigDecimal.ZERO, StatusMulta.MULTA_PAGA);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        assertThrows(MultaNaoPendenteException.class, () -> emprestimoService.pagarMulta(1L));
    }

    @Test
    void deveListarEmprestimos() {
        Emprestimo emprestimo = new Emprestimo(1L, null, null, null, livro, leitor, 0, BigDecimal.ZERO, null);
        when(emprestimoRepository.findAll()).thenReturn(List.of(emprestimo));
        assertFalse(emprestimoService.listarEmprestimos().isEmpty());
    }

    @Test
    void deveListarEmprestimosPorLeitor() {
        Emprestimo emprestimo = new Emprestimo(1L, null, null, null, livro, leitor, 0, BigDecimal.ZERO, null);
        when(emprestimoRepository.findByLeitorId(1L)).thenReturn(List.of(emprestimo));
        assertFalse(emprestimoService.listarEmprestimosPorLeitor(1L).isEmpty());
    }
}

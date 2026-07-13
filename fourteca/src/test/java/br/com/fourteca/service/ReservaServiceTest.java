package br.com.fourteca.service;

import br.com.fourteca.entity.Leitores;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.entity.Reserva;
import br.com.fourteca.enums.StatusReserva;
import br.com.fourteca.exception.*;
import br.com.fourteca.repository.LeitoresRepository;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.repository.ReservaRepository;
import br.com.fourteca.request.ReservaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private LeitoresRepository leitoresRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Livro livro;
    private Leitores leitor;
    private ReservaRequest reservaRequest;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setId(1L);
        livro.setDisponivel(false);

        leitor = new Leitores();
        leitor.setId(1L);

        reservaRequest = new ReservaRequest(1L, 1L);
    }

    @Test
    void deveCriarReservaComSucesso() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(reservaRepository.existsByLivroAndLeitorAndStatus(livro, leitor, StatusReserva.ATIVA)).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        var response = reservaService.criarReserva(reservaRequest);

        assertNotNull(response);
        assertEquals(StatusReserva.ATIVA, response.getStatus());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deveLancarExcecaoAoCriarReservaComLivroInexistente() {
        when(livroRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(LivroNaoEncontradoException.class, () -> reservaService.criarReserva(reservaRequest));
    }

    @Test
    void deveLancarExcecaoAoCriarReservaComLeitorInexistente() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(LeitorNaoEncontradoException.class, () -> reservaService.criarReserva(reservaRequest));
    }

    @Test
    void deveLancarExcecaoAoCriarReservaParaLivroDisponivel() {
        livro.setDisponivel(true);
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));

        assertThrows(LivroDisponivelException.class, () -> reservaService.criarReserva(reservaRequest));
    }

    @Test
    void deveLancarExcecaoAoCriarReservaDuplicada() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(reservaRepository.existsByLivroAndLeitorAndStatus(livro, leitor, StatusReserva.ATIVA)).thenReturn(true);

        assertThrows(ReservaJaExistenteException.class, () -> reservaService.criarReserva(reservaRequest));
    }

    @Test
    void deveCancelarReservaComSucesso() {
        Reserva reserva = new Reserva();
        reserva.setStatus(StatusReserva.ATIVA);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        reservaService.cancelarReserva(1L);

        assertEquals(StatusReserva.CANCELADA, reserva.getStatus());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void deveLancarExcecaoAoCancelarReservaInexistente() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reservaService.cancelarReserva(1L));
    }

    @Test
    void deveLancarExcecaoAoCancelarReservaNaoAtiva() {
        Reserva reserva = new Reserva();
        reserva.setStatus(StatusReserva.ATENDIDA);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(ReservaNaoPodeSerCanceladaException.class, () -> reservaService.cancelarReserva(1L));
    }

    @Test
    void deveListarFilaDeEsperaPorLivro() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(reservaRepository.findByLivroAndStatusOrderByDataCriacaoAsc(livro, StatusReserva.ATIVA)).thenReturn(Collections.emptyList());

        var response = reservaService.listarFilaDeEsperaPorLivro(1L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveVerificarSeReservaPertenceAoUsuario() {
        Reserva reserva = new Reserva();
        reserva.setLeitor(leitor);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertTrue(reservaService.isReservaDoUsuario(1L, 1L));
    }

    @Test
    void deveVerificarSeReservaNaoPertenceAoUsuario() {
        Reserva reserva = new Reserva();
        reserva.setLeitor(leitor);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertFalse(reservaService.isReservaDoUsuario(1L, 2L));
    }

    @Test
    void deveRetornarFalsoSeReservaNaoExisteAoVerificarUsuario() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());
        assertFalse(reservaService.isReservaDoUsuario(1L, 1L));
    }
}

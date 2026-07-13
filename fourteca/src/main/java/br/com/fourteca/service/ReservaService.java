package br.com.fourteca.service;

import br.com.fourteca.config.Auditable;
import br.com.fourteca.entity.Leitores;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.entity.Reserva;
import br.com.fourteca.enums.StatusReserva;
import br.com.fourteca.exception.*;
import br.com.fourteca.repository.LeitoresRepository;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.repository.ReservaRepository;
import br.com.fourteca.request.ReservaRequest;
import br.com.fourteca.response.ReservaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LivroRepository livroRepository;
    private final LeitoresRepository leitoresRepository;

    @Transactional
    @Auditable(acao = "CREATE")
    public ReservaResponse criarReserva(ReservaRequest request) {
        Livro livro = livroRepository.findById(request.getIdLivro())
                .orElseThrow(LivroNaoEncontradoException::new);

        Leitores leitor = leitoresRepository.findById(request.getIdLeitor())
                .orElseThrow(LeitorNaoEncontradoException::new);

        if (livro.isDisponivel()) {
            throw new LivroDisponivelException();
        }

        if (reservaRepository.existsByLivroAndLeitorAndStatus(livro, leitor, StatusReserva.ATIVA)) {
            throw new ReservaJaExistenteException();
        }

        Reserva reserva = new Reserva();
        reserva.setLivro(livro);
        reserva.setLeitor(leitor);
        reserva.setStatus(StatusReserva.ATIVA);

        Reserva reservaSalva = reservaRepository.save(reserva);

        return toResponse(reservaSalva);
    }

    @Transactional
    @Auditable(acao = "CANCEL")
    public void cancelarReserva(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new ReservaNaoPodeSerCanceladaException();
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarFilaDeEsperaPorLivro(Long idLivro) {
        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(LivroNaoEncontradoException::new);

        List<Reserva> fila = reservaRepository.findByLivroAndStatusOrderByDataCriacaoAsc(livro, StatusReserva.ATIVA);

        return fila.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public boolean isReservaDoUsuario(Long idReserva, Long idUsuario) {
        return reservaRepository.findById(idReserva)
                .map(reserva -> reserva.getLeitor().getId().equals(idUsuario))
                .orElse(false);
    }

    private ReservaResponse toResponse(Reserva reserva) {
        return ReservaResponse.builder()
                .idReserva((long) Math.toIntExact(reserva.getId()))
                .idLivro((long) Math.toIntExact(reserva.getLivro().getId()))
                .tituloLivro(reserva.getLivro().getTitulo())
                .idLeitor((long) Math.toIntExact(reserva.getLeitor().getId()))
                .dataCriacao(reserva.getDataCriacao())
                .status(reserva.getStatus())
                .dataAtendimento(reserva.getDataAtendimento())
                .build();
    }
}

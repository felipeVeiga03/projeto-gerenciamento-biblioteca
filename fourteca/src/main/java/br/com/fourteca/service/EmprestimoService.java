package br.com.fourteca.service;

import br.com.fourteca.config.Auditable;
import br.com.fourteca.config.EmprestimoProperties;
import br.com.fourteca.entity.Emprestimo;
import br.com.fourteca.entity.Leitores;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.entity.Reserva;
import br.com.fourteca.enums.StatusLeitor;
import br.com.fourteca.enums.StatusMulta;
import br.com.fourteca.enums.StatusReserva;
import br.com.fourteca.exception.*;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.repository.LeitoresRepository;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.repository.ReservaRepository;
import br.com.fourteca.request.EmprestimoRequest;
import br.com.fourteca.response.EmprestimoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final LeitoresRepository leitoresRepository;
    private final ReservaRepository reservaRepository;
    private final EmprestimoProperties emprestimoProperties;

    @Transactional
    @Auditable(acao = "CREATE")
    public EmprestimoResponse registrarEmprestimo(EmprestimoRequest emprestimoRequest) {
        Livro livro = livroRepository.findById(emprestimoRequest.getIdLivro())
                .orElseThrow(LivroNaoEncontradoException::new);

        Leitores leitor = leitoresRepository.findById(emprestimoRequest.getIdLeitor())
                .orElseThrow(LeitorNaoEncontradoException::new);

        if (leitor.getStatus() == StatusLeitor.INATIVO) {
            throw new LeitorInativoException();
        }

        if (emprestimoRepository.existsByLeitorAndStatusMulta(leitor, StatusMulta.MULTA_PENDENTE)) {
            throw new LeitorInadimplenteException();
        }

        Integer maxLivros = emprestimoProperties.getMaxLivrosPorTipoLeitor().get(leitor.getTipo().name());
        if (maxLivros != null) {
            long emprestimosAtivos = emprestimoRepository.countByLeitorAndDataEfetivaDevolucaoIsNull(leitor);
            if (emprestimosAtivos >= maxLivros) {
                throw new LimiteDeEmprestimosExcedidoException();
            }
        }

        if (!livro.isDisponivel()) {
            throw new LivroIndisponivelParaEmprestimoException();
        }

        livro.setDisponivel(false);
        livroRepository.save(livro);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setLeitor(leitor);
        emprestimo.setDataEmprestimo(LocalDate.now());

        Integer diasEmprestimo = emprestimoProperties.getDiasPorTipoLeitor().get(leitor.getTipo().name());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(diasEmprestimo));

        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);

        return toResponse(emprestimoSalvo);
    }

    @Transactional
    @Auditable(acao = "UPDATE")
    public EmprestimoResponse devolverLivro(Long idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo)
                .orElseThrow(EmprestimoInexistenteException::new);

        if (emprestimo.getDataEfetivaDevolucao() != null) {
            throw new EmprestimoJaDevolvidoException();
        }

        LocalDate dataDevolucao = LocalDate.now();
        emprestimo.setDataEfetivaDevolucao(dataDevolucao);

        if (dataDevolucao.isAfter(emprestimo.getDataPrevistaDevolucao())) {
            long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataPrevistaDevolucao(), dataDevolucao);
            BigDecimal valorMulta = emprestimoProperties.getTaxaMultaDiaria().multiply(new BigDecimal(diasAtraso));
            emprestimo.setDiasAtraso((int) diasAtraso);
            emprestimo.setValorMulta(valorMulta);
            emprestimo.setStatusMulta(StatusMulta.MULTA_PENDENTE);
        } else {
            emprestimo.setDiasAtraso(0);
            emprestimo.setValorMulta(BigDecimal.ZERO);
            emprestimo.setStatusMulta(StatusMulta.SEM_MULTA);
        }

        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);
        Livro livro = emprestimo.getLivro();

        List<Reserva> filaDeEspera = reservaRepository.findByLivroAndStatusOrderByDataCriacaoAsc(livro, StatusReserva.ATIVA);

        if (!filaDeEspera.isEmpty()) {
            Reserva proximaReserva = filaDeEspera.get(0);
            proximaReserva.setStatus(StatusReserva.ATENDIDA);
            proximaReserva.setDataAtendimento(LocalDateTime.now());
            reservaRepository.save(proximaReserva);
        } else {
            livro.setDisponivel(true);
            livroRepository.save(livro);
        }

        return toResponse(emprestimoSalvo);
    }

    @Transactional
    @Auditable(acao = "UPDATE")
    public EmprestimoResponse pagarMulta(Long idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo)
                .orElseThrow(EmprestimoInexistenteException::new);

        if (emprestimo.getStatusMulta() != StatusMulta.MULTA_PENDENTE) {
            throw new MultaNaoPendenteException();
        }

        emprestimo.setStatusMulta(StatusMulta.MULTA_PAGA);
        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);

        return toResponse(emprestimoSalvo);
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponse> listarEmprestimos() {
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();
        return emprestimos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponse> listarEmprestimosPorLeitor(Long leitorId) {
        List<Emprestimo> emprestimos = emprestimoRepository.findByLeitorId(leitorId);
        return emprestimos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EmprestimoResponse toResponse(Emprestimo emprestimo) {
        return EmprestimoResponse.builder()
                .idEmprestimo((long) Math.toIntExact(emprestimo.getId()))
                .dataEmprestimo(emprestimo.getDataEmprestimo())
                .dataPrevistaDevolucao(emprestimo.getDataPrevistaDevolucao())
                .dataEfetivaDevolucao(emprestimo.getDataEfetivaDevolucao())
                .idLivro((long) Math.toIntExact(emprestimo.getLivro().getId()))
                .idLeitor((long) Math.toIntExact(emprestimo.getLeitor().getId()))
                .diasAtraso(emprestimo.getDiasAtraso())
                .valorMulta(emprestimo.getValorMulta())
                .statusMulta(emprestimo.getStatusMulta())
                .build();
    }
}

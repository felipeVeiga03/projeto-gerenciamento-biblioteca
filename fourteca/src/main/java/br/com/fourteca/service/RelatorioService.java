package br.com.fourteca.service;

import br.com.fourteca.entity.Emprestimo;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.response.EmprestimoAVencerResponse;
import br.com.fourteca.response.EmprestimoDetalheResponse;
import br.com.fourteca.response.LeitorInadimplenteResponse;
import br.com.fourteca.response.RankingLivroResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final EmprestimoRepository emprestimoRepository;

    public List<RankingLivroResponse> gerarRankingLivrosMaisEmprestados(LocalDate dataInicio, LocalDate dataFim, int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        return emprestimoRepository.findLivrosMaisEmprestados(dataInicio, dataFim, pageable);
    }

    @Transactional(readOnly = true)
    public Page<LeitorInadimplenteResponse> gerarRelatorioLeitoresInadimplentes(Integer diasAtrasoMinimo, Pageable pageable) {
        Page<LeitorInadimplenteResponse> relatorioPage = emprestimoRepository.findLeitoresInadimplentes(diasAtrasoMinimo, pageable);

        List<Long> leitorIds = relatorioPage.getContent().stream()
                .map(LeitorInadimplenteResponse::getIdLeitor)
                .collect(Collectors.toList());

        if (leitorIds.isEmpty()) {
            return relatorioPage;
        }

        List<Emprestimo> emprestimosPendentes = emprestimoRepository.findEmprestimosPendentesByLeitorIds(leitorIds);

        Map<Long, List<EmprestimoDetalheResponse>> emprestimosPorLeitor = emprestimosPendentes.stream()
                .collect(Collectors.groupingBy(
                        emprestimo -> emprestimo.getLeitor().getId(),
                        Collectors.mapping(this::toEmprestimoDetalheResponse, Collectors.toList())
                ));

        relatorioPage.getContent().forEach(leitorResponse -> 
            leitorResponse.setEmprestimosAtrasados(emprestimosPorLeitor.get(leitorResponse.getIdLeitor()))
        );

        return relatorioPage;
    }

    public List<EmprestimoAVencerResponse> gerarRelatorioEmprestimosAVencer(int dias) {
        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusDays(dias);
        return emprestimoRepository.findEmprestimosAVencer(dataInicio, dataFim);
    }

    private EmprestimoDetalheResponse toEmprestimoDetalheResponse(Emprestimo emprestimo) {
        return new EmprestimoDetalheResponse(
                emprestimo.getId(),
                emprestimo.getLivro().getTitulo(),
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getDiasAtraso(),
                emprestimo.getValorMulta()
        );
    }
}

package br.com.fourteca.controller;

import br.com.fourteca.response.EmprestimoAVencerResponse;
import br.com.fourteca.response.LeitorInadimplenteResponse;
import br.com.fourteca.response.RankingLivroResponse;
import br.com.fourteca.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @Operation(summary = "Gera um ranking dos livros mais emprestados",
               description = "Retorna uma lista dos livros mais emprestados, com filtros opcionais por período e um limite de resultados. Acesso: ADMIN, BIBLIOTECARIO")
    @GetMapping("/livros-mais-emprestados")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<List<RankingLivroResponse>> getRankingLivros(
            @RequestParam(required = false, name = "de") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false, name = "ate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "10") int limite) {
        
        List<RankingLivroResponse> ranking = relatorioService.gerarRankingLivrosMaisEmprestados(dataInicio, dataFim, limite);
        return ResponseEntity.ok(ranking);
    }

    @Operation(summary = "Lista os leitores inadimplentes",
               description = "Retorna uma lista paginada de leitores com multas pendentes, com filtro opcional por dias mínimos de atraso. Acesso: ADMIN, BIBLIOTECARIO")
    @GetMapping("/leitores-inadimplentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Page<LeitorInadimplenteResponse>> getLeitoresInadimplentes(
            @RequestParam(required = false) Integer diasAtrasoMinimo,
            @PageableDefault(size = 20, sort = "totalMultasPendentes,desc") Pageable pageable) {
        
        Page<LeitorInadimplenteResponse> relatorio = relatorioService.gerarRelatorioLeitoresInadimplentes(diasAtrasoMinimo, pageable);
        return ResponseEntity.ok(relatorio);
    }

    @Operation(summary = "Lista os empréstimos a vencer",
               description = "Retorna uma lista de empréstimos que vencerão nos próximos N dias. Acesso: ADMIN, BIBLIOTECARIO")
    @GetMapping("/emprestimos-a-vencer")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<List<EmprestimoAVencerResponse>> getEmprestimosAVencer(
            @RequestParam(defaultValue = "7") int dias) {
        
        List<EmprestimoAVencerResponse> relatorio = relatorioService.gerarRelatorioEmprestimosAVencer(dias);
        return ResponseEntity.ok(relatorio);
    }
}

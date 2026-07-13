package br.com.fourteca.service;

import br.com.fourteca.entity.Emprestimo;
import br.com.fourteca.entity.Leitores;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.response.LeitorInadimplenteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void deveGerarRankingLivrosMaisEmprestados() {
        // Cenário
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        LocalDate dataFim = LocalDate.now();
        int limite = 5;
        Pageable pageable = PageRequest.of(0, limite);

        when(emprestimoRepository.findLivrosMaisEmprestados(dataInicio, dataFim, pageable)).thenReturn(Collections.emptyList());

        // Ação
        relatorioService.gerarRankingLivrosMaisEmprestados(dataInicio, dataFim, limite);

        // Validação
        verify(emprestimoRepository).findLivrosMaisEmprestados(dataInicio, dataFim, pageable);
    }

    @Test
    void deveGerarRelatorioLeitoresInadimplentes() {
        // Cenário
        Pageable pageable = PageRequest.of(0, 10);
        LeitorInadimplenteResponse leitorResponse = new LeitorInadimplenteResponse(1L, "Leitor Teste", "123456", "teste@teste.com", BigDecimal.TEN, null);
        Page<LeitorInadimplenteResponse> page = new PageImpl<>(List.of(leitorResponse));

        when(emprestimoRepository.findLeitoresInadimplentes(any(), any(Pageable.class))).thenReturn(page);
        
        Leitores leitor = new Leitores();
        leitor.setId(1L);
        Livro livro = new Livro();
        livro.setTitulo("Livro Atrasado");
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLeitor(leitor);
        emprestimo.setLivro(livro);
        
        when(emprestimoRepository.findEmprestimosPendentesByLeitorIds(List.of(1L))).thenReturn(List.of(emprestimo));

        // Ação
        Page<LeitorInadimplenteResponse> result = relatorioService.gerarRelatorioLeitoresInadimplentes(null, pageable);

        // Validação
        assertFalse(result.getContent().isEmpty());
        assertNotNull(result.getContent().get(0).getEmprestimosAtrasados());
        assertEquals(1, result.getContent().get(0).getEmprestimosAtrasados().size());
        assertEquals("Livro Atrasado", result.getContent().get(0).getEmprestimosAtrasados().get(0).getTituloLivro());
        verify(emprestimoRepository).findLeitoresInadimplentes(null, pageable);
        verify(emprestimoRepository).findEmprestimosPendentesByLeitorIds(List.of(1L));
    }
    
    @Test
    void deveGerarRelatorioLeitoresInadimplentesComListaVazia() {
        // Cenário
        Pageable pageable = PageRequest.of(0, 10);
        when(emprestimoRepository.findLeitoresInadimplentes(any(), any(Pageable.class))).thenReturn(Page.empty());

        // Ação
        Page<LeitorInadimplenteResponse> result = relatorioService.gerarRelatorioLeitoresInadimplentes(null, pageable);

        // Validação
        assertTrue(result.isEmpty());
    }

    @Test
    void deveGerarRelatorioEmprestimosAVencer() {
        // Cenário
        int dias = 7;
        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusDays(dias);

        when(emprestimoRepository.findEmprestimosAVencer(dataInicio, dataFim)).thenReturn(Collections.emptyList());

        // Ação
        relatorioService.gerarRelatorioEmprestimosAVencer(dias);

        // Validação
        verify(emprestimoRepository).findEmprestimosAVencer(dataInicio, dataFim);
    }
}

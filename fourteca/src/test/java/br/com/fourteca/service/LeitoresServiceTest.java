package br.com.fourteca.service;

import br.com.fourteca.entity.Leitores;
import br.com.fourteca.enums.StatusLeitor;
import br.com.fourteca.enums.TipoLeitor;
import br.com.fourteca.exception.LeitorJaCadastradoException;
import br.com.fourteca.exception.LeitorNaoEncontradoException;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.repository.LeitoresRepository;
import br.com.fourteca.request.LeitoresRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LeitoresServiceTest {

    @Mock
    private LeitoresRepository leitoresRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @InjectMocks
    private LeitoresService leitoresService;

    private Leitores leitor;
    private LeitoresRequest leitoresRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        leitor = new Leitores();
        leitor.setId(1L);
        leitor.setDocumento("12345678900");
        leitor.setEmail("teste@teste.com");
        leitor.setTipo(TipoLeitor.ALUNO);
        leitor.setStatus(StatusLeitor.ATIVO);

        leitoresRequest = new LeitoresRequest();
        leitoresRequest.setDocumento("12345678900");
        leitoresRequest.setEmail("teste@teste.com");
        leitoresRequest.setTipo(TipoLeitor.ALUNO);
        leitoresRequest.setStatus(StatusLeitor.ATIVO);
    }

    @Test
    void deveAdicionarLeitorComSucesso() {
        when(leitoresRepository.existsByDocumento(anyString())).thenReturn(false);
        when(leitoresRepository.existsByEmail(anyString())).thenReturn(false);
        when(leitoresRepository.save(any(Leitores.class))).thenReturn(leitor);
        when(emprestimoRepository.countByLeitorAndDataEfetivaDevolucaoIsNull(any(Leitores.class))).thenReturn(0L);

        var response = leitoresService.adicionarLeitor(leitoresRequest);

        assertNotNull(response);
        assertEquals(leitor.getEmail(), response.getEmail());
        verify(leitoresRepository, times(1)).save(any(Leitores.class));
    }

    @Test
    void deveLancarExcecaoAoAdicionarLeitorComDocumentoExistente() {
        when(leitoresRepository.existsByDocumento(anyString())).thenReturn(true);
        assertThrows(LeitorJaCadastradoException.class, () -> leitoresService.adicionarLeitor(leitoresRequest));
        verify(leitoresRepository, never()).save(any(Leitores.class));
    }
    
    @Test
    void deveLancarExcecaoAoAdicionarLeitorComEmailExistente() {
        when(leitoresRepository.existsByDocumento(anyString())).thenReturn(false);
        when(leitoresRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(LeitorJaCadastradoException.class, () -> leitoresService.adicionarLeitor(leitoresRequest));
        verify(leitoresRepository, never()).save(any(Leitores.class));
    }

    @Test
    void deveListarLeitores() {
        when(leitoresRepository.findAll()).thenReturn(Collections.singletonList(leitor));
        when(emprestimoRepository.countEmprestimosAtivosByLeitorIn(anyList())).thenReturn(
                List.of(Map.of("leitorId", 1L, "total", 1L))
        );

        var response = leitoresService.listarLeitores();

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(1, response.get(0).getEmprestimosAtivos());
    }
    
    @Test
    void deveListarLeitoresComListaVazia() {
        when(leitoresRepository.findAll()).thenReturn(Collections.emptyList());
        var response = leitoresService.listarLeitores();
        assertTrue(response.isEmpty());
    }

    @Test
    void deveBuscarLeitorPorIdComSucesso() {
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(emprestimoRepository.countByLeitorAndDataEfetivaDevolucaoIsNull(any(Leitores.class))).thenReturn(0L);

        var response = leitoresService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(leitor.getId(), response.getIdLeitor());
    }

    @Test
    void deveLancarExcecaoAoBuscarLeitorPorIdInexistente() {
        when(leitoresRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(LeitorNaoEncontradoException.class, () -> leitoresService.buscarPorId(99L));
    }

    @Test
    void deveBuscarLeitorPorDocumento() {
        when(leitoresRepository.findByDocumento("12345678900")).thenReturn(Optional.of(leitor));
        var response = leitoresService.buscar("12345678900", null);
        assertFalse(response.isEmpty());
        assertEquals("12345678900", response.get(0).getDocumento());
    }

    @Test
    void deveBuscarLeitorPorEmail() {
        when(leitoresRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(leitor));
        var response = leitoresService.buscar(null, "teste@teste.com");
        assertFalse(response.isEmpty());
        assertEquals("teste@teste.com", response.get(0).getEmail());
    }
    
    @Test
    void deveRetornarListaVaziaQuandoBuscaSemParametros() {
        var response = leitoresService.buscar(null, null);
        assertTrue(response.isEmpty());
    }
    
    @Test
    void deveRetornarListaVaziaQuandoBuscaNaoEncontra() {
        when(leitoresRepository.findByDocumento(anyString())).thenReturn(Optional.empty());
        var response = leitoresService.buscar("documento_inexistente", null);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveAlterarLeitorComSucesso() {
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(leitoresRepository.save(any(Leitores.class))).thenReturn(leitor);
        when(emprestimoRepository.countByLeitorAndDataEfetivaDevolucaoIsNull(any(Leitores.class))).thenReturn(0L);

        var response = leitoresService.alterarLeitor(1L, leitoresRequest);

        assertNotNull(response);
        verify(leitoresRepository).save(leitor);
    }
    
    @Test
    void deveLancarExcecaoAoAlterarLeitorInexistente() {
        when(leitoresRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(LeitorNaoEncontradoException.class, () -> leitoresService.alterarLeitor(99L, leitoresRequest));
    }

    @Test
    void deveInativarLeitorComSucesso() {
        when(leitoresRepository.findById(1L)).thenReturn(Optional.of(leitor));
        
        leitoresService.inativarLeitor(1L);

        assertEquals(StatusLeitor.INATIVO, leitor.getStatus());
        verify(leitoresRepository).save(leitor);
    }
    
    @Test
    void deveLancarExcecaoAoInativarLeitorInexistente() {
        when(leitoresRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(LeitorNaoEncontradoException.class, () -> leitoresService.inativarLeitor(99L));
    }
}

package br.com.fourteca.service;

import br.com.fourteca.entity.Auditoria;
import br.com.fourteca.repository.AuditoriaRepository;
import br.com.fourteca.response.AuditoriaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @InjectMocks
    private AuditoriaService auditoriaService;
    
    private Auditoria auditoria;

    @BeforeEach
    void setUp() {
        auditoria = new Auditoria();
        auditoria.setId(1L);
        auditoria.setUsuarioId(1L);
        auditoria.setAcao("CREATE");
        auditoria.setNomeEntidade("Livro");
        auditoria.setEntidadeId(10L);
        auditoria.setTimestamp(LocalDateTime.now());
    }

    @Test
    void deveRegistrarAuditoriaComSucesso() {
        // Ação
        auditoriaService.registrarAuditoria(1L, "CREATE", "Livro", 10L);

        // Validação
        ArgumentCaptor<Auditoria> auditoriaCaptor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository).save(auditoriaCaptor.capture());

        Auditoria auditoriaSalva = auditoriaCaptor.getValue();
        assertEquals(1L, auditoriaSalva.getUsuarioId());
        assertEquals("CREATE", auditoriaSalva.getAcao());
        assertEquals("Livro", auditoriaSalva.getNomeEntidade());
        assertEquals(10L, auditoriaSalva.getEntidadeId());
    }

    @Test
    void deveListarAuditoriasEValidarMapeamento() {
        // Cenário
        Pageable pageable = PageRequest.of(0, 10);
        Page<Auditoria> paginaDeAuditoria = new PageImpl<>(List.of(auditoria), pageable, 1);
        
        when(auditoriaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(paginaDeAuditoria);

        // Ação
        Page<AuditoriaResponse> result = auditoriaService.listarAuditorias(null, null, null, null, pageable);

        // Validação
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        AuditoriaResponse response = result.getContent().get(0);
        assertEquals(auditoria.getId(), response.getId());
        assertEquals(auditoria.getAcao(), response.getAcao());
        assertEquals(auditoria.getUsuarioId(), response.getUsuarioId());
        verify(auditoriaRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void deveListarAuditoriasComTodosOsFiltros() {
        Pageable pageable = PageRequest.of(0, 10);
        when(auditoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        auditoriaService.listarAuditorias(LocalDateTime.now(), LocalDateTime.now(), 1L, "UPDATE", pageable);
        verify(auditoriaRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void deveListarAuditoriasApenasComFiltroDataInicio() {
        Pageable pageable = PageRequest.of(0, 10);
        when(auditoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        auditoriaService.listarAuditorias(LocalDateTime.now(), null, null, null, pageable);
        verify(auditoriaRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void deveListarAuditoriasApenasComFiltroDataFim() {
        Pageable pageable = PageRequest.of(0, 10);
        when(auditoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        auditoriaService.listarAuditorias(null, LocalDateTime.now(), null, null, pageable);
        verify(auditoriaRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void deveListarAuditoriasApenasComFiltroUsuarioId() {
        Pageable pageable = PageRequest.of(0, 10);
        when(auditoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        auditoriaService.listarAuditorias(null, null, 1L, null, pageable);
        verify(auditoriaRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void deveListarAuditoriasApenasComFiltroTipoAcao() {
        Pageable pageable = PageRequest.of(0, 10);
        when(auditoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        auditoriaService.listarAuditorias(null, null, null, "CREATE", pageable);
        verify(auditoriaRepository).findAll(any(Specification.class), any(Pageable.class));
    }
    
    @Test
    void deveListarAuditoriasComFiltroTipoAcaoVazio() {
        Pageable pageable = PageRequest.of(0, 10);
        when(auditoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        auditoriaService.listarAuditorias(null, null, null, "", pageable);
        verify(auditoriaRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}

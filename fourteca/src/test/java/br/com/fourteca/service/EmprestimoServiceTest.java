package br.com.fourteca.service;

import br.com.fourteca.entity.Emprestimo;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.exception.EmprestimoInexistenteException;
import br.com.fourteca.exception.EmprestimoJaDevolvidoException;
import br.com.fourteca.exception.LivroIndisponivelParaEmprestimoException;
import br.com.fourteca.exception.LivroNaoEncontradoException;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.request.EmprestimoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private EmprestimoRequest emprestimoRequest;
    private Livro livro;
    private Emprestimo emprestimo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        livro = new Livro("O Senhor dos Anéis", "J.R.R. Tolkien", "978-85-9508-080-0", true);
        livro.setIdLivro(1);

        emprestimoRequest = EmprestimoRequest.builder()
                .idLivro(1)
                .nomeLeitor("Leitor Teste")
                .build();

        emprestimo = new Emprestimo();
        emprestimo.setIdEmprestimo(1);
        emprestimo.setLivro(livro);
        emprestimo.setNomeLeitor("Leitor Teste");
        emprestimo.setDataEmprestimo(LocalDate.now());
    }

    @Test
    void deveRegistrarUmEmprestimoComSucesso() {
        when(livroRepository.findById(anyInt())).thenReturn(Optional.of(livro));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        var response = emprestimoService.registrarEmprestimo(emprestimoRequest);

        assertNotNull(response);
        assertEquals(emprestimo.getIdEmprestimo(), response.getIdEmprestimo());
        verify(livroRepository, times(1)).save(any(Livro.class));
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontrado() {
        when(livroRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(LivroNaoEncontradoException.class, () -> emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @Test
    void registrarEmprestimo_deveLancarExcecaoQuandoLivroIndisponivel() {
        livro.setDisponivel(false);
        when(livroRepository.findById(anyInt())).thenReturn(Optional.of(livro));

        assertThrows(LivroIndisponivelParaEmprestimoException.class, () -> emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @Test
    void deveDevolverUmLivroComSucesso() {
        when(emprestimoRepository.findById(anyInt())).thenReturn(Optional.of(emprestimo));

        emprestimoService.devolverLivro(1);

        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmprestimoNaoEncontrado() {
        when(emprestimoRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EmprestimoInexistenteException.class, () -> emprestimoService.devolverLivro(1));
    }

    @Test
    void deveLancarExcecaoQuandoEmprestimoJaDevolvido() {
        emprestimo.setDataEfetivaDevolucao(LocalDate.now());
        when(emprestimoRepository.findById(1)).thenReturn(Optional.of(emprestimo));
        assertThrows(EmprestimoJaDevolvidoException.class, () -> {
            emprestimoService.devolverLivro(1);
        });
    }

    @Test
    void deveRetornarTodosOsEmprestimos() {
        when(emprestimoRepository.findAll()).thenReturn(Collections.singletonList(emprestimo));

        var response = emprestimoService.listarEmprestimos();

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
    }
}

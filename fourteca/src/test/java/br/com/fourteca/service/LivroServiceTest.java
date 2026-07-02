package br.com.fourteca.service;

import br.com.fourteca.entity.Livro;
import br.com.fourteca.exception.LivroJaCadastroadoException;
import br.com.fourteca.exception.LivroNaoEncontradoException;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.request.LivroRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    private LivroRequest livroRequest;
    private Livro livro;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        livroRequest = LivroRequest.builder()
                .titulo("O Senhor dos Anéis")
                .autor("J.R.R. Tolkien")
                .isbn("978-85-9508-080-0")
                .disponivel(true)
                .build();
        livro = new Livro("O Senhor dos Anéis", "J.R.R. Tolkien", "978-85-9508-080-0", true);
        livro.setIdLivro(1);
    }

    @Test
    void deveCadastrarUmLivroComSucesso() {
        when(livroRepository.existsByIsbn(anyString())).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        var response = livroService.cadastrarLivro(livroRequest);

        assertNotNull(response);
        assertEquals(livro.getTitulo(), response.getTitulo());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoLivroJaExiste() {
        when(livroRepository.existsByIsbn(anyString())).thenReturn(true);

        assertThrows(LivroJaCadastroadoException.class, () -> livroService.cadastrarLivro(livroRequest));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveRetornarTodosOsLivros() {
        when(livroRepository.findAll()).thenReturn(Collections.singletonList(livro));

        var response = livroService.listarLivros(null, null);

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
    }

    @Test
    void deveRetornarUmLivroComSucesso() {
        when(livroRepository.findById(anyInt())).thenReturn(Optional.of(livro));

        var response = livroService.buscarLivroPorId(1);

        assertNotNull(response);
        assertEquals(livro.getTitulo(), response.getTitulo());
    }

    @Test
    void DeveLancarExcecaoQuandoLivroNaoEncontrado() {
        when(livroRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(LivroNaoEncontradoException.class, () -> livroService.buscarLivroPorId(1));
    }

    @Test
    void deveAtualizarUmLivroComSucesso() {
        when(livroRepository.findById(anyInt())).thenReturn(Optional.of(livro));
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        var response = livroService.atualizarLivro(1, livroRequest);

        assertNotNull(response);
        assertEquals(livro.getTitulo(), response.getTitulo());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveDeletarUmLivroComSucesso() {
        when(livroRepository.existsById(anyInt())).thenReturn(true);
        doNothing().when(livroRepository).deleteById(anyInt());

        livroService.deletarLivro(1);

        verify(livroRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontrado() {
        when(livroRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(LivroNaoEncontradoException.class, () -> livroService.deletarLivro(1));
        verify(livroRepository, never()).deleteById(anyInt());
    }
}

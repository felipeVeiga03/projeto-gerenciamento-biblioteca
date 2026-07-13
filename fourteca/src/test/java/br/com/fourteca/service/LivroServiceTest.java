package br.com.fourteca.service;

import br.com.fourteca.entity.Livro;
import br.com.fourteca.exception.LivroJaCadastradoException;
import br.com.fourteca.exception.LivroNaoEncontradoException;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.request.LivroRequest;
import br.com.fourteca.response.LivroResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
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

        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("O Senhor dos Anéis");
        livro.setAutor("J.R.R. Tolkien");
        livro.setIsbn("978-85-9508-080-0");
        livro.setDisponivel(true);
    }

    @Test
    void deveCadastrarUmLivroComSucesso() {
        when(livroRepository.existsByIsbn(anyString())).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        LivroResponse response = livroService.cadastrarLivro(livroRequest);

        assertNotNull(response);
        assertEquals(livro.getTitulo(), response.getTitulo());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoLivroJaExiste() {
        when(livroRepository.existsByIsbn(anyString())).thenReturn(true);

        assertThrows(LivroJaCadastradoException.class, () -> livroService.cadastrarLivro(livroRequest));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveListarTodosOsLivros() {
        when(livroRepository.findAll()).thenReturn(Collections.singletonList(livro));
        var response = livroService.listarLivros(null, null);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
    }

    @Test
    void deveListarLivrosPorAutor() {
        when(livroRepository.findByAutor("J.R.R. Tolkien")).thenReturn(List.of(livro));
        var response = livroService.listarLivros("J.R.R. Tolkien", null);
        assertFalse(response.isEmpty());
        assertEquals("J.R.R. Tolkien", response.get(0).getAutor());
    }

    @Test
    void deveListarLivrosPorDisponibilidade() {
        when(livroRepository.findByDisponivel(true)).thenReturn(List.of(livro));
        var response = livroService.listarLivros(null, true);
        assertFalse(response.isEmpty());
        assertTrue(response.get(0).isDisponivel());
    }

    @Test
    void deveListarLivrosPorAutorEDisponibilidade() {
        when(livroRepository.findByAutorAndDisponivel("J.R.R. Tolkien", true)).thenReturn(List.of(livro));
        var response = livroService.listarLivros("J.R.R. Tolkien", true);
        assertFalse(response.isEmpty());
        assertEquals("J.R.R. Tolkien", response.get(0).getAutor());
        assertTrue(response.get(0).isDisponivel());
    }

    @Test
    void deveBuscarLivroPorIdComSucesso() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        var response = livroService.buscarLivroPorId(1L);
        assertNotNull(response);
        assertEquals(livro.getTitulo(), response.getTitulo());
    }

    @Test
    void deveLancarExcecaoAoBuscarLivroPorIdInexistente() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(LivroNaoEncontradoException.class, () -> livroService.buscarLivroPorId(1L));
    }

    @Test
    void deveAtualizarUmLivroComSucessoSemAlterarIsbn() {
        LivroRequest requestDeAtualizacao = LivroRequest.builder()
                .titulo("O Hobbit")
                .autor("J.R.R. Tolkien")
                .disponivel(false)
                .build();

        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(livroRepository.save(any(Livro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        livroService.atualizarLivro(1L, requestDeAtualizacao);

        ArgumentCaptor<Livro> livroCaptor = ArgumentCaptor.forClass(Livro.class);
        verify(livroRepository).save(livroCaptor.capture());
        Livro livroSalvo = livroCaptor.getValue();

        assertEquals("O Hobbit", livroSalvo.getTitulo());
        assertEquals("978-85-9508-080-0", livroSalvo.getIsbn()); // Garante que o ISBN não foi alterado
    }
    
    @Test
    void deveLancarExcecaoAoAtualizarLivroInexistente() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(LivroNaoEncontradoException.class, () -> livroService.atualizarLivro(99L, livroRequest));
    }

    @Test
    void deveDeletarUmLivroComSucesso() {
        when(livroRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(livroRepository).deleteById(anyLong());
        livroService.deletarLivro(1L);
        verify(livroRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deveLancarExcecaoAoDeletarLivroInexistente() {
        when(livroRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(LivroNaoEncontradoException.class, () -> livroService.deletarLivro(1L));
        verify(livroRepository, never()).deleteById(anyLong());
    }
}

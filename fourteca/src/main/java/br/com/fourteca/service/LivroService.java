package br.com.fourteca.service;

import br.com.fourteca.config.Auditable;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.exception.LivroJaCadastradoException;
import br.com.fourteca.exception.LivroNaoEncontradoException;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.request.LivroRequest;
import br.com.fourteca.response.LivroResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    @Transactional
    @Auditable(acao = "CREATE")
    public LivroResponse cadastrarLivro(LivroRequest livroRequest) {
        if (livroRepository.existsByIsbn(livroRequest.getIsbn())) {
            throw new LivroJaCadastradoException();
        }
        Livro livro = new Livro();
        livro.setTitulo(livroRequest.getTitulo());
        livro.setAutor(livroRequest.getAutor());
        livro.setIsbn(livroRequest.getIsbn());
        livro.setDisponivel(livroRequest.getDisponivel());
        
        return toResponse(livroRepository.save(livro));
    }

    @Transactional(readOnly = true)
    public List<LivroResponse> listarLivros(String autor, Boolean disponivel) {
        List<Livro> livros;
        if (autor != null && disponivel != null) {
            livros = livroRepository.findByAutorAndDisponivel(autor, disponivel);
        } else if (autor != null) {
            livros = livroRepository.findByAutor(autor);
        } else if (disponivel != null) {
            livros = livroRepository.findByDisponivel(disponivel);
        } else {
            livros = livroRepository.findAll();
        }
        return livros.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LivroResponse buscarLivroPorId(Long id) {
        return livroRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(LivroNaoEncontradoException::new);
    }

    @Transactional
    @Auditable(acao = "UPDATE")
    public LivroResponse atualizarLivro(Long id, LivroRequest livroRequest) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.setTitulo(livroRequest.getTitulo());
                    livro.setAutor(livroRequest.getAutor());
                    livro.setDisponivel(livroRequest.getDisponivel());
                    return toResponse(livroRepository.save(livro));
                })
                .orElseThrow(LivroNaoEncontradoException::new);
    }

    @Transactional
    @Auditable(acao = "DELETE")
    public void deletarLivro(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNaoEncontradoException();
        }
        livroRepository.deleteById(id);
    }

    private LivroResponse toResponse(Livro livro) {
        return LivroResponse.builder()
                .idLivro(livro.getId())
                .autor(livro.getAutor())
                .titulo(livro.getTitulo())
                .isbn(livro.getIsbn())
                .disponivel(livro.isDisponivel())
                .build();
    }
}

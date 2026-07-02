package br.com.fourteca.service;

import br.com.fourteca.entity.Livro;
import br.com.fourteca.exception.LivroJaCadastroadoException;
import br.com.fourteca.exception.LivroNaoEncontradoException;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.request.LivroRequest;
import br.com.fourteca.response.LivroResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroResponse cadastrarLivro (LivroRequest livroRequest){
        if (this.livroRepository.existsByIsbn(livroRequest.getIsbn())) {
            throw new LivroJaCadastroadoException();
        }
        Livro livro = new Livro(livroRequest.getTitulo(),
                livroRequest.getAutor(),livroRequest.getIsbn(), livroRequest.getDisponivel());
        var livroSalvo = livroRepository.save(livro);
        return LivroResponse.builder().idLivro(livroSalvo.getIdLivro()).autor(livroSalvo.getAutor())
                .titulo(livroSalvo.getTitulo()).isbn(livroSalvo.getIsbn()).disponivel(livroSalvo.getDisponivel()).build();
    }

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
        return livros.stream()
                .map(livro -> LivroResponse.builder()
                        .idLivro(livro.getIdLivro())
                        .autor(livro.getAutor())
                        .titulo(livro.getTitulo())
                        .isbn(livro.getIsbn())
                        .disponivel(livro.getDisponivel())
                        .build())
                .collect(Collectors.toList());
    }

    public LivroResponse buscarLivroPorId(Integer id) {
        return livroRepository.findById(id)
                .map(livro -> LivroResponse.builder()
                        .idLivro(livro.getIdLivro())
                        .autor(livro.getAutor())
                        .titulo(livro.getTitulo())
                        .isbn(livro.getIsbn())
                        .disponivel(livro.getDisponivel())
                        .build())
                .orElseThrow(LivroNaoEncontradoException::new);
    }

    @Transactional
    public LivroResponse atualizarLivro(Integer id, LivroRequest livroRequest) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.setTitulo(livroRequest.getTitulo());
                    livro.setAutor(livroRequest.getAutor());
                    livro.setIsbn(livroRequest.getIsbn());
                    livro.setDisponivel(livroRequest.getDisponivel());
                    Livro livroAtualizado = livroRepository.save(livro);
                    return LivroResponse.builder()
                            .idLivro(livroAtualizado.getIdLivro())
                            .autor(livroAtualizado.getAutor())
                            .titulo(livroAtualizado.getTitulo())
                            .isbn(livroAtualizado.getIsbn())
                            .disponivel(livroAtualizado.getDisponivel())
                            .build();
                })
                .orElseThrow(LivroNaoEncontradoException::new);

    }

    public void deletarLivro(Integer id) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNaoEncontradoException();
        }
        livroRepository.deleteById(id);
    }
}

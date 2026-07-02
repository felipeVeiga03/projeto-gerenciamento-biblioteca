package br.com.fourteca.controller;

import br.com.fourteca.request.LivroRequest;
import br.com.fourteca.response.LivroResponse;
import br.com.fourteca.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequestMapping
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping("/")
    public RedirectView root() {
        return new RedirectView("/livros");
    }

    @PostMapping("/livros")
    public ResponseEntity<LivroResponse> cadastrarLivro(@Valid @RequestBody LivroRequest livroRequest) {
        return ResponseEntity.ok(livroService.cadastrarLivro(livroRequest));
    }

    @GetMapping("/livros")
    public ResponseEntity<List<LivroResponse>> listarLivros(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) Boolean disponivel) {
        return ResponseEntity.ok(livroService.listarLivros(autor, disponivel));
    }

    @GetMapping("/livros/{id}")
    public ResponseEntity<LivroResponse> buscarLivroPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(livroService.buscarLivroPorId(id));
    }

    @PutMapping("/livros/{id}")
    public ResponseEntity<LivroResponse> atualizarLivro(@PathVariable Integer id, @Valid @RequestBody LivroRequest livroRequest) {
        return ResponseEntity.ok(livroService.atualizarLivro(id, livroRequest));
    }

    @DeleteMapping("/livros/{id}")
    public ResponseEntity<Void> deletarLivro(@PathVariable Integer id) {
        livroService.deletarLivro(id);
        return ResponseEntity.noContent().build();
    }
}

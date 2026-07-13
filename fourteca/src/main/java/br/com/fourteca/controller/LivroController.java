package br.com.fourteca.controller;

import br.com.fourteca.request.LivroRequest;
import br.com.fourteca.response.LivroResponse;
import br.com.fourteca.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Cadastra um novo livro", description = "Cria um novo livro no sistema. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Livro cadastrado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping("/livros")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<LivroResponse> cadastrarLivro(@Valid @RequestBody LivroRequest livroRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.cadastrarLivro(livroRequest));
    }

    @Operation(summary = "Lista todos os livros", description = "Retorna uma lista de todos os livros. Acesso: TODOS")
    @GetMapping("/livros")
    public ResponseEntity<List<LivroResponse>> listarLivros(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) Boolean disponivel) {
        return ResponseEntity.ok(livroService.listarLivros(autor, disponivel));
    }

    @Operation(summary = "Busca um livro por ID", description = "Retorna um único livro pelo seu ID. Acesso: TODOS")
    @GetMapping("/livros/{id}")
    public ResponseEntity<LivroResponse> buscarLivroPorId(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarLivroPorId(id));
    }

    @Operation(summary = "Atualiza um livro existente", description = "Atualiza os dados de um livro. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    @PutMapping("/livros/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<LivroResponse> atualizarLivro(@PathVariable Long id, @Valid @RequestBody LivroRequest livroRequest) {
        return ResponseEntity.ok(livroService.atualizarLivro(id, livroRequest));
    }

    @Operation(summary = "Deleta um livro", description = "Remove um livro do sistema. Acesso: ADMIN")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Livro deletado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    @DeleteMapping("/livros/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarLivro(@PathVariable Long id) {
        livroService.deletarLivro(id);
        return ResponseEntity.noContent().build();
    }
}

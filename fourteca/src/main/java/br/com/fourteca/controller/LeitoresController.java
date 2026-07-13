package br.com.fourteca.controller;

import br.com.fourteca.request.LeitoresRequest;
import br.com.fourteca.response.LeitoresResponse;
import br.com.fourteca.service.LeitoresService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leitores")
@RequiredArgsConstructor
public class LeitoresController {

    private final LeitoresService leitoresService;

    @Operation(summary = "Adiciona um novo leitor", description = "Cria um novo leitor no sistema. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Leitor adicionado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Leitor já cadastrado")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<LeitoresResponse> adicionarLeitor
            (@Valid @RequestBody LeitoresRequest request) {
        LeitoresResponse response = leitoresService.adicionarLeitor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lista todos os leitores", description = "Retorna uma lista de todos os leitores. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de leitores retornada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<List<LeitoresResponse>> listarLeitores() {
        return ResponseEntity.ok(leitoresService.listarLeitores());
    }

    @Operation(summary = "Busca um leitor por ID", description = "Retorna um único leitor. Acesso: ADMIN, BIBLIOTECARIO ou o próprio LEITOR")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Leitor encontrado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Leitor não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO') or (hasRole('LEITOR') and #id == authentication.principal.leitor.id)")
    public ResponseEntity<LeitoresResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(leitoresService.buscarPorId(id));
    }

    @Operation(summary = "Busca leitores por documento ou e-mail", description = "Busca leitores. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Leitores encontrados com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/busca")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<List<LeitoresResponse>> buscar
            (@RequestParam(required = false) String documento,
             @RequestParam(required = false) String email) {
        return ResponseEntity.ok(leitoresService.buscar(documento, email));
    }

    @Operation(summary = "Atualiza um leitor existente", description = "Atualiza os dados de um leitor. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Leitor atualizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Leitor não encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Leitor já cadastrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<LeitoresResponse> alterarLeitor
            (@PathVariable Long id, @Valid @RequestBody LeitoresRequest request) {
        return ResponseEntity.ok(leitoresService.alterarLeitor(id, request));
    }

    @Operation(summary = "Inativa um leitor", description = "Inativa um leitor do sistema. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Leitor inativado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Leitor não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Void> inativarLeitor(@PathVariable Long id) {
        leitoresService.inativarLeitor(id);
        return ResponseEntity.noContent().build();
    }
}

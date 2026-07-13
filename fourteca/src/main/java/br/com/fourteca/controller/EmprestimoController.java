package br.com.fourteca.controller;

import br.com.fourteca.request.EmprestimoRequest;
import br.com.fourteca.response.EmprestimoResponse;
import br.com.fourteca.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @Operation(summary = "Registra um novo empréstimo", description = "Cria um novo empréstimo. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Empréstimo registrado com sucesso", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = EmprestimoResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflito de regras de negócio")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<EmprestimoResponse> registrarEmprestimo(@Valid @RequestBody EmprestimoRequest emprestimoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @Operation(summary = "Devolve um livro emprestado", description = "Registra a devolução de um livro. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Livro devolvido com sucesso", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = EmprestimoResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    @PatchMapping("/{idEmprestimo}/devolver")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<EmprestimoResponse> devolverLivro(@PathVariable Long idEmprestimo) {
        EmprestimoResponse response = emprestimoService.devolverLivro(idEmprestimo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Paga a multa de um empréstimo", description = "Registra o pagamento da multa. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Multa paga com sucesso", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = EmprestimoResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Empréstimo não encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Não há multa para este empréstimo")
    })
    @PatchMapping("/{idEmprestimo}/pagar-multa")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<EmprestimoResponse> pagarMulta(@PathVariable Long idEmprestimo) {
        EmprestimoResponse response = emprestimoService.pagarMulta(idEmprestimo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista todos os empréstimos", description = "Retorna uma lista de todos os empréstimos. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de empréstimos retornada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<List<EmprestimoResponse>> listarTodosEmprestimos() {
        return ResponseEntity.ok(emprestimoService.listarEmprestimos());
    }

    @Operation(summary = "Lista os meus empréstimos", description = "Retorna o histórico de empréstimos do usuário logado. Acesso: LEITOR")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de empréstimos retornada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/meus")
    @PreAuthorize("hasRole('LEITOR')")
    public ResponseEntity<List<EmprestimoResponse>> listarMeusEmprestimos(Authentication authentication) {
        br.com.fourteca.entity.Usuario usuario = (br.com.fourteca.entity.Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(emprestimoService.listarEmprestimosPorLeitor(usuario.getId()));
    }
}

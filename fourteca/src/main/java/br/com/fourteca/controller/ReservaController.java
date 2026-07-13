package br.com.fourteca.controller;

import br.com.fourteca.request.ReservaRequest;
import br.com.fourteca.response.ReservaResponse;
import br.com.fourteca.service.ReservaService;
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
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(summary = "Cria uma nova reserva", description = "Cria uma nova reserva de livro. Acesso: TODOS")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflito de regras de negócio")
    })
    @PostMapping
    public ResponseEntity<ReservaResponse> criarReserva(@Valid @RequestBody ReservaRequest request) {
        ReservaResponse response = reservaService.criarReserva(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lista a fila de espera de um livro", description = "Retorna a lista de reservas. Acesso: ADMIN, BIBLIOTECARIO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fila de espera retornada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    @GetMapping("/livro/{idLivro}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<List<ReservaResponse>> listarFilaDeEspera(@PathVariable Long idLivro) {
        List<ReservaResponse> fila = reservaService.listarFilaDeEsperaPorLivro(idLivro);
        return ResponseEntity.ok(fila);
    }

    @Operation(summary = "Cancela uma reserva", description = "Cancela uma reserva. Acesso: ADMIN, BIBLIOTECARIO ou o próprio LEITOR")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Reserva cancelada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    })
    @PatchMapping("/{idReserva}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO') or @reservaService.isReservaDoUsuario(#idReserva, authentication.principal.id)")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long idReserva) {
        reservaService.cancelarReserva(idReserva);
        return ResponseEntity.noContent().build();
    }
}

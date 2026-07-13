package br.com.fourteca.controller;

import br.com.fourteca.response.AuditoriaResponse;
import br.com.fourteca.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Operation(summary = "Lista os registros de auditoria", description = "Retorna uma lista paginada de todos os eventos de auditoria, com filtros opcionais. Acesso: ADMIN, BIBLIOTECARIO")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Page<AuditoriaResponse>> listarAuditorias(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String tipoAcao,
            @PageableDefault(size = 20, sort = "timestamp,desc") Pageable pageable) {
        return ResponseEntity.ok(auditoriaService.listarAuditorias(dataInicio, dataFim, usuarioId, tipoAcao, pageable));
    }
}

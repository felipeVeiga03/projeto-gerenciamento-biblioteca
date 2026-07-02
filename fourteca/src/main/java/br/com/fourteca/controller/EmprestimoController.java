package br.com.fourteca.controller;

import br.com.fourteca.request.EmprestimoRequest;
import br.com.fourteca.response.EmprestimoResponse;
import br.com.fourteca.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoResponse> registrarEmprestimo(@Valid @RequestBody EmprestimoRequest emprestimoRequest) {
        return ResponseEntity.ok(emprestimoService.registrarEmprestimo(emprestimoRequest));
    }

    @PatchMapping("/{idEmprestimo}/devolver")
    public ResponseEntity<Void> devolverLivro(@PathVariable Integer idEmprestimo) {
        emprestimoService.devolverLivro(idEmprestimo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoResponse>> listarEmprestimos() {
        return ResponseEntity.ok(emprestimoService.listarEmprestimos());
    }
}

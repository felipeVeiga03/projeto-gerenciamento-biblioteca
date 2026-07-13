package br.com.fourteca.service;

import br.com.fourteca.entity.Auditoria;
import br.com.fourteca.repository.AuditoriaRepository;
import br.com.fourteca.response.AuditoriaResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public void registrarAuditoria(Long usuarioId, String acao, String nomeEntidade, Long entidadeId) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuarioId(usuarioId);
        auditoria.setAcao(acao);
        auditoria.setNomeEntidade(nomeEntidade);
        auditoria.setEntidadeId(entidadeId);
        auditoriaRepository.save(auditoria);
    }

    public Page<AuditoriaResponse> listarAuditorias(LocalDateTime dataInicio, LocalDateTime dataFim, Long usuarioId, String tipoAcao, Pageable pageable) {
        Specification<Auditoria> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dataInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), dataFim));
            }
            if (usuarioId != null) {
                predicates.add(criteriaBuilder.equal(root.get("usuarioId"), usuarioId));
            }
            if (tipoAcao != null && !tipoAcao.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("acao"), tipoAcao));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return auditoriaRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private AuditoriaResponse toResponse(Auditoria auditoria) {
        return AuditoriaResponse.builder()
                .id(auditoria.getId())
                .usuarioId(auditoria.getUsuarioId())
                .acao(auditoria.getAcao())
                .nomeEntidade(auditoria.getNomeEntidade())
                .entidadeId(auditoria.getEntidadeId())
                .timestamp(auditoria.getTimestamp())
                .build();
    }
}

package br.com.fourteca.service;

import br.com.fourteca.config.Auditable;
import br.com.fourteca.entity.Leitores;
import br.com.fourteca.enums.StatusLeitor;
import br.com.fourteca.exception.LeitorJaCadastradoException;
import br.com.fourteca.exception.LeitorNaoEncontradoException;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.repository.LeitoresRepository;
import br.com.fourteca.request.LeitoresRequest;
import br.com.fourteca.response.LeitoresResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeitoresService {

    private final LeitoresRepository leitoresRepository;
    private final EmprestimoRepository emprestimoRepository;

    @Transactional
    @Auditable(acao = "CREATE")
    public LeitoresResponse adicionarLeitor(LeitoresRequest request) {
        if (leitoresRepository.existsByDocumento(request.getDocumento()) || leitoresRepository.existsByEmail(request.getEmail())) {
            throw new LeitorJaCadastradoException();
        }

        Leitores leitor = new Leitores();
        leitor.setDocumento(request.getDocumento());
        leitor.setEmail(request.getEmail());
        leitor.setTipo(request.getTipo());
        leitor.setStatus(StatusLeitor.ATIVO);

        Leitores leitorSalvo = leitoresRepository.save(leitor);
        return toResponse(leitorSalvo);
    }

    @Transactional(readOnly = true)
    public List<LeitoresResponse> listarLeitores() {
        List<Leitores> leitores = leitoresRepository.findAll();
        if (leitores.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> contagemEmprestimos = emprestimoRepository.countEmprestimosAtivosByLeitorIn(leitores)
                .stream()
                .collect(Collectors.toMap(
                        map -> (Long) map.get("leitorId"),
                        map -> (Long) map.get("total")
                ));

        return leitores.stream()
                .map(leitor -> toResponse(leitor, contagemEmprestimos.getOrDefault(leitor.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LeitoresResponse buscarPorId(Long id) {
        Leitores leitor = leitoresRepository.findById(id)
                .orElseThrow(LeitorNaoEncontradoException::new);
        return toResponse(leitor);
    }

    @Transactional(readOnly = true)
    public List<LeitoresResponse> buscar(String documento, String email) {
        Optional<Leitores> leitorOpt;
        if (StringUtils.hasText(documento)) {
            leitorOpt = leitoresRepository.findByDocumento(documento);
        } else if (StringUtils.hasText(email)) {
            leitorOpt = leitoresRepository.findByEmail(email);
        } else {
            return Collections.emptyList();
        }
        return leitorOpt.map(leitor -> Collections.singletonList(toResponse(leitor)))
                .orElse(Collections.emptyList());
    }

    @Transactional
    @Auditable(acao = "UPDATE")
    public LeitoresResponse alterarLeitor(Long id, LeitoresRequest request) {
        Leitores leitor = leitoresRepository.findById(id)
                .orElseThrow(LeitorNaoEncontradoException::new);
        leitor.setTipo(request.getTipo());
        leitor.setStatus(request.getStatus());

        Leitores leitorAtualizado = leitoresRepository.save(leitor);
        return toResponse(leitorAtualizado);
    }

    @Transactional
    @Auditable(acao = "INACTIVATE")
    public void inativarLeitor(Long id) {
        Leitores leitor = leitoresRepository.findById(id)
                .orElseThrow(LeitorNaoEncontradoException::new);

        leitor.setStatus(StatusLeitor.INATIVO);
        leitoresRepository.save(leitor);
    }

    private LeitoresResponse toResponse(Leitores leitor) {
        long emprestimosAtivos = emprestimoRepository.countByLeitorAndDataEfetivaDevolucaoIsNull(leitor);
        return toResponse(leitor, emprestimosAtivos);
    }

    private LeitoresResponse toResponse(Leitores leitor, long emprestimosAtivos) {
        return LeitoresResponse.builder()
                .idLeitor((long) Math.toIntExact(leitor.getId()))
                .documento(leitor.getDocumento())
                .email(leitor.getEmail())
                .tipo(leitor.getTipo())
                .status(leitor.getStatus())
                .emprestimosAtivos((int) emprestimosAtivos)
                .build();
    }
}

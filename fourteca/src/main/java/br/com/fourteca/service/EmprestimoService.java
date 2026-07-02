package br.com.fourteca.service;

import br.com.fourteca.entity.Emprestimo;
import br.com.fourteca.entity.Livro;
import br.com.fourteca.exception.EmprestimoInexistenteException;
import br.com.fourteca.exception.EmprestimoJaDevolvidoException;
import br.com.fourteca.exception.LivroIndisponivelParaEmprestimoException;
import br.com.fourteca.exception.LivroNaoEncontradoException;
import br.com.fourteca.exception.NenhumEmprestimoEncontradoException;
import br.com.fourteca.repository.EmprestimoRepository;
import br.com.fourteca.repository.LivroRepository;
import br.com.fourteca.request.EmprestimoRequest;
import br.com.fourteca.response.EmprestimoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;

    public EmprestimoResponse registrarEmprestimo(EmprestimoRequest emprestimoRequest) {
        Livro livro = livroRepository.findById(emprestimoRequest.getIdLivro())
                .orElseThrow(LivroNaoEncontradoException::new);

        if (!livro.getDisponivel()) {
            throw new LivroIndisponivelParaEmprestimoException();
        }

        livro.setDisponivel(false);
        livroRepository.save(livro);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setNomeLeitor(emprestimoRequest.getNomeLeitor());
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(14));

        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);

        return EmprestimoResponse.builder()
                .idEmprestimo(emprestimoSalvo.getIdEmprestimo())
                .nomeLeitor(emprestimoSalvo.getNomeLeitor())
                .dataEmprestimo(emprestimoSalvo.getDataEmprestimo())
                .dataPrevistaDevolucao(emprestimoSalvo.getDataPrevistaDevolucao())
                .idLivro(emprestimoSalvo.getLivro().getIdLivro())
                .build();
    }

    public void devolverLivro(Integer idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo)
                .orElseThrow(EmprestimoInexistenteException::new);

        if (emprestimo.getDataEfetivaDevolucao() != null) {
            throw new EmprestimoJaDevolvidoException();
        }

        emprestimo.setDataEfetivaDevolucao(LocalDate.now());
        emprestimoRepository.save(emprestimo);

        Livro livro = emprestimo.getLivro();
        livro.setDisponivel(true);
        livroRepository.save(livro);
    }

    public List<EmprestimoResponse> listarEmprestimos() {
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();
        if (emprestimos.isEmpty()) {
            throw new NenhumEmprestimoEncontradoException();
        }
        return emprestimos.stream()
                .map(emprestimo -> EmprestimoResponse.builder()
                        .idEmprestimo(emprestimo.getIdEmprestimo())
                        .nomeLeitor(emprestimo.getNomeLeitor())
                        .dataEmprestimo(emprestimo.getDataEmprestimo())
                        .dataPrevistaDevolucao(emprestimo.getDataPrevistaDevolucao())
                        .dataEfetivaDevolucao(emprestimo.getDataEfetivaDevolucao())
                        .idLivro(emprestimo.getLivro().getIdLivro())
                        .build())
                .collect(Collectors.toList());
    }
}

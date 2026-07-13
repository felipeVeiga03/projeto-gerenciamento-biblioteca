package br.com.fourteca.repository;

import br.com.fourteca.entity.Emprestimo;
import br.com.fourteca.entity.Leitores;
import br.com.fourteca.enums.StatusMulta;
import br.com.fourteca.response.EmprestimoAVencerResponse;
import br.com.fourteca.response.LeitorInadimplenteResponse;
import br.com.fourteca.response.RankingLivroResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    boolean existsByLeitorAndStatusMulta(Leitores leitor, StatusMulta statusMulta);
    long countByLeitorAndDataEfetivaDevolucaoIsNull(Leitores leitor);
    List<Emprestimo> findByLeitorId(Long leitorId);

    @Query("SELECT new br.com.fourteca.response.RankingLivroResponse(e.livro.id, e.livro.titulo, e.livro.autor, COUNT(e.livro)) " +
           "FROM Emprestimo e " +
           "WHERE (:dataInicio IS NULL OR e.dataEmprestimo >= :dataInicio) " +
           "AND (:dataFim IS NULL OR e.dataEmprestimo <= :dataFim) " +
           "GROUP BY e.livro.id, e.livro.titulo, e.livro.autor " +
           "ORDER BY COUNT(e.livro) DESC")
    List<RankingLivroResponse> findLivrosMaisEmprestados(@Param("dataInicio") LocalDate dataInicio,
                                                         @Param("dataFim") LocalDate dataFim,
                                                         Pageable pageable);

    @Query("SELECT new br.com.fourteca.response.LeitorInadimplenteResponse(e.leitor.id, e.leitor.nome, e.leitor.documento, e.leitor.email, SUM(e.valorMulta), null) " +
           "FROM Emprestimo e " +
           "WHERE (e.statusMulta = 'MULTA_PENDENTE' OR (e.dataEfetivaDevolucao IS NULL AND e.dataPrevistaDevolucao < CURRENT_DATE)) " +
           "AND (:diasAtrasoMinimo IS NULL OR e.diasAtraso >= :diasAtrasoMinimo) " +
           "GROUP BY e.leitor.id, e.leitor.nome, e.leitor.documento, e.leitor.email " +
           "ORDER BY SUM(e.valorMulta) DESC")
    Page<LeitorInadimplenteResponse> findLeitoresInadimplentes(@Param("diasAtrasoMinimo") Integer diasAtrasoMinimo, Pageable pageable);

    @Query("SELECT e FROM Emprestimo e WHERE e.leitor.id IN :leitorIds AND (e.statusMulta = 'MULTA_PENDENTE' OR (e.dataEfetivaDevolucao IS NULL AND e.dataPrevistaDevolucao < CURRENT_DATE))")
    List<Emprestimo> findEmprestimosPendentesByLeitorIds(@Param("leitorIds") List<Long> leitorIds);

    @Query("SELECT new br.com.fourteca.response.EmprestimoAVencerResponse(e.id, e.dataPrevistaDevolucao, e.livro.titulo, e.leitor.nome, e.leitor.email) " +
           "FROM Emprestimo e " +
           "WHERE e.dataEfetivaDevolucao IS NULL " +
           "AND e.dataPrevistaDevolucao BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY e.dataPrevistaDevolucao ASC")
    List<EmprestimoAVencerResponse> findEmprestimosAVencer(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT e.leitor.id as leitorId, COUNT(e.id) as total " +
           "FROM Emprestimo e " +
           "WHERE e.dataEfetivaDevolucao IS NULL AND e.leitor IN :leitores " +
           "GROUP BY e.leitor.id")
    List<Map<String, Long>> countEmprestimosAtivosByLeitorIn(@Param("leitores") List<Leitores> leitores);
}

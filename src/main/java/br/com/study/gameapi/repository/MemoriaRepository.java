package br.com.study.gameapi.repository;

import br.com.study.gameapi.model.memoria.Memoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoriaRepository extends JpaRepository<Memoria, Long> {

    List<Memoria> findByPersonagemId(Long personagemId);

    /** Busca memórias ainda disponíveis (não queimadas) */
    List<Memoria> findByPersonagemIdAndQueimadaFalse(Long personagemId);

    /** Primeira memória disponível para queimar no crítico */
    Optional<Memoria> findFirstByPersonagemIdAndQueimadaFalse(Long personagemId);

    /** Verifica se ainda há memórias disponíveis */
    boolean existsByPersonagemIdAndQueimadaFalse(Long personagemId);
}
package br.com.study.gameapi.repository;

import br.com.study.gameapi.model.personagem.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonagemRepository extends JpaRepository<Personagem, Long> {

    /**
     * Lista todos os personagens de um jogador
     */
    List<Personagem> findByUsuarioId(Long usuarioId);

    /**
     * Busca um personagem garantindo que pertence ao jogador certo
     */
    Optional<Personagem> findByIdAndUsuarioId(Long id, Long usuarioId);

    /**
     * Verifica se o jogador já tem um personagem com esse nome
     */
    boolean existsByNomeAndUsuarioId(String nome, Long usuarioId);
}
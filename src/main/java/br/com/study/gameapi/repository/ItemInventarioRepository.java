package br.com.study.gameapi.repository;

import br.com.study.gameapi.model.enums.SlotEquipamentoType;
import br.com.study.gameapi.model.enums.TipoItemType;
import br.com.study.gameapi.model.inventario.ItemInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemInventarioRepository extends JpaRepository<ItemInventario, Long> {

    /**
     * Lista todos os itens do inventário de um personagem
     */
    List<ItemInventario> findByPersonagemId(Long personagemId);

    /**
     * Lista itens de um tipo específico (ex: só POCAO)
     */
    List<ItemInventario> findByPersonagemIdAndTipo(Long personagemId, TipoItemType tipo);

    /**
     * Lista itens com quantidade maior que zero (disponíveis para uso)
     */
    List<ItemInventario> findByPersonagemIdAndQuantidadeGreaterThan(Long personagemId, int quantidade);

    /**
     * Busca item equipado em um slot específico
     */
    Optional<ItemInventario> findByPersonagemIdAndSlotAndEquipadoTrue(
            Long personagemId, SlotEquipamentoType slot
    );

    /**
     * Lista todos os equipamentos ativos do personagem
     */
    List<ItemInventario> findByPersonagemIdAndEquipadoTrue(Long personagemId);

    /**
     * Garante que o item pertence ao personagem correto
     */
    Optional<ItemInventario> findByIdAndPersonagemId(Long id, Long personagemId);
}
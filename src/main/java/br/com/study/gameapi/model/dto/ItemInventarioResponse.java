package br.com.study.gameapi.model.dto;

import br.com.study.gameapi.model.enums.SlotEquipamentoType;
import br.com.study.gameapi.model.enums.TipoItemType;

public class ItemInventarioResponse {

    /**
     * Resumo do item — usado dentro do PersonagemResponse.Detalhes.
     */
    public record Resumo(
            Long id,
            String nome,
            TipoItemType tipo,
            int quantidade,
            boolean equipado
    ) {}

    /**
     * Detalhes completos do item — usado na tela de inventário.
     */
    public record Detalhes(
            Long id,
            String nome,
            String descricao,
            TipoItemType tipo,
            int quantidade,
            boolean equipado,
            SlotEquipamentoType slot,

            // Consumíveis
            int curaHp,
            int restauraMp,
            int duracaoTurnos,
            int valorEfeito,

            // Equipamentos
            int bonusAtaque,
            int bonusDefesa,
            int bonusHp,
            int bonusVelocidade,
            int bonusSorte
    ) {}
}
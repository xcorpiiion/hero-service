package br.com.study.gameapi.model.dto;

import br.com.study.gameapi.model.enums.SlotEquipamentoType;
import br.com.study.gameapi.model.enums.TipoItemType;
import jakarta.validation.constraints.*;

public class ItemInventarioRequest {

    /**
     * Adiciona um item ao inventário do personagem.
     * Usado pelo sistema quando o personagem encontra um item (drops, lojas, etc).
     */
    public record AdicionarItem(

            @NotNull(message = "ID do personagem é obrigatório")
            Long personagemId,

            @NotBlank(message = "Nome do item é obrigatório")
            @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
            String nome,

            String descricao,

            @NotNull(message = "Tipo do item é obrigatório")
            TipoItemType tipo,

            @Min(value = 1, message = "Quantidade mínima é 1")
            int quantidade,

            // Consumíveis
            int curaHp,
            int restauraMp,
            int duracaoTurnos,
            int valorEfeito,

            // Equipamentos
            SlotEquipamentoType slot,
            int bonusAtaque,
            int bonusDefesa,
            int bonusHp,
            int bonusVelocidade,
            int bonusSorte
    ) {
    }

    /**
     * Equipa ou desequipa um item do personagem.
     */
    public record EquiparItem(
            @NotNull(message = "ID do personagem é obrigatório")
            Long personagemId,

            @NotNull(message = "ID do item é obrigatório")
            Long itemId
    ) {
    }
}
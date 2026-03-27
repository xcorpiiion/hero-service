package br.com.study.gameapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Tipos de item disponíveis no inventário.
 *
 * Consumíveis: usados em batalha, somem após uso.
 * Equipamentos: equipados no personagem, alteram atributos permanentemente.
 */
@Getter
@RequiredArgsConstructor
public enum TipoItemType {

    // ─── Consumíveis ──────────────────────────────────────────────────────────

    /**
     * Restaura HP imediatamente ao usar.
     * Passa o turno — inimigo aproveita a abertura.
     */
    POCAO(true, false),

    /**
     * Restaura MP imediatamente ao usar.
     * Passa o turno.
     */
    ELIXIR(true, false),

    /**
     * Aplica um buff no personagem por X turnos.
     * Ex: +30% ATQ por 3 turnos.
     * Passa o turno.
     */
    PERGAMINHO(true, false),

    /**
     * Aplica DoT (damage over time) no inimigo por X turnos.
     * O inimigo perde HP no início de cada turno dele.
     * Passa o turno.
     */
    VENENO(true, false),

    /**
     * Aumenta ATQ do personagem por 1 batalha inteira.
     * Efeito some ao fim da batalha.
     * Passa o turno.
     */
    PEDRA_DE_AFIAR(true, false),

    // ─── Equipamentos ─────────────────────────────────────────────────────────

    /**
     * Equipado na mão principal. Aumenta ATQ.
     * Ex: Espada Longa, Cajado Arcano, Arco Élfico.
     */
    ARMA(false, true),

    /**
     * Equipado no corpo. Aumenta DEF e HP.
     * Ex: Armadura de Placas, Manto das Sombras.
     */
    ARMADURA(false, true),

    /**
     * Equipado no dedo. Bônus variados.
     * Ex: Anel da Sorte (+Sorte), Anel de Força (+ATQ).
     */
    ANEL(false, true);

    /** Se true, o item some após ser usado */
    private final boolean consumivel;

    /** Se true, o item é equipado no personagem */
    private final boolean equipamento;
}
package br.com.study.gameapi.model.enums;

/**
 * Slots disponíveis no personagem para equipamentos.
 * Cada slot aceita apenas um tipo de item.
 */
public enum SlotEquipamentoType {

    /** Aceita ARMA */
    MAO_PRINCIPAL,

    /** Aceita ARMADURA */
    CORPO,

    /** Aceita ANEL — dois slots disponíveis */
    DEDO_ESQUERDO,
    DEDO_DIREITO
}
package br.com.study.gameapi.model.dto;

import br.com.study.gameapi.model.enums.ClassePersonagemType;

import java.util.List;

public class PersonagemResponse {

    /**
     * Resumo do personagem — usado em listagens.
     */
    public record Resumo(
            Long id,
            String nome,
            ClassePersonagemType classe,
            int nivel,
            int hpAtual,
            int hpMaximo,
            long almas,
            boolean vivo
    ) {}

    /**
     * Detalhes completos — usado na tela de perfil do personagem.
     */
    public record Detalhes(
            Long id,
            String nome,
            ClassePersonagemType classe,
            int nivel,
            long experiencia,
            long almas,

            // Vitais
            int hpAtual,
            int hpMaximo,
            int mpAtual,
            int mpMaximo,
            int apMaximo,

            // Combate
            int ataque,
            int defesa,
            int velocidade,
            int sorte,

            // Inventário
            int slotsUsados,
            int limiteInventario,
            List<ItemInventarioResponse.Resumo> inventario,

            boolean vivo
    ) {}
}
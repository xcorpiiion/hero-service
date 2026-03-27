package br.com.study.gameapi.model.dto;

import br.com.study.gameapi.model.enums.ClassePersonagemType;
import br.com.study.gameapi.model.enums.TipoMemoriaType;

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
            long bitsConsciencia,
            boolean vivo,
            boolean hollow
    ) {
    }

    /**
     * Detalhes completos — usado na tela de perfil do personagem.
     */
    public record Detalhes(
            Long id,
            String nome,
            ClassePersonagemType classe,
            int nivel,
            long experiencia,
            long bitsConsciencia,

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

            // Simulacro do Vazio
            boolean hollow,
            int memoriasDisponiveis,
            int memoriasTotais,

            // Inventário
            int slotsUsados,
            int limiteInventario,
            List<ItemInventarioResponse.Resumo> inventario,

            boolean vivo
    ) {
    }

    /**
     * Resumo de memória — usado dentro do Detalhes.
     */
    public record MemoriaResumo(
            Long id,
            TipoMemoriaType tipo,
            String descricao,
            boolean queimada
    ) {
    }
}
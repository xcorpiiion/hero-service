package br.com.study.gameapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Classes disponíveis para o personagem.
 * Cada classe define os atributos base e o estilo de jogo.
 *
 * Os valores são multiplicados pelo nível durante a progressão.
 */
@Getter
@RequiredArgsConstructor
public enum ClassePersonagemType {

    /**
     * Alto HP, alta força. Jogo agressivo e direto.
     * Ponto fraco: velocidade baixa, sem habilidades mágicas.
     */
    GUERREIRO(
            /* hpBase */        150,
            /* mpBase */        20,
            /* apBase */        4,
            /* atqBase */       18,
            /* defBase */       12,
            /* velocidadeBase */8,
            /* sorteBase */     5
    ),

    /**
     * Equilibrado. Bom pra aprender o sistema.
     * Escudo passivo: 10% de chance de bloquear dano crítico do inimigo.
     */
    CAVALEIRO(
            180,  // hp alto por causa do escudo
            30,
            4,
            14,
            16,   // def mais alto que guerreiro
            9,
            8
    ),

    /**
     * Baixo HP, altíssima velocidade e sorte.
     * Age antes dos inimigos lentos. Críticos frequentes.
     * Ponto fraco: morre fácil se errar a esquiva.
     */
    ASSASSINO(
            90,
            40,
            5,    // ap alto — mais ações por turno
            16,
            7,
            18,   // velocidade máxima
            15    // sorte máxima — críticos constantes
    ),

    /**
     * Baixo HP e DEF, alto MP e ATQ mágico.
     * Habilidades especiais ignoram DEF do inimigo.
     * Ponto fraco: frágil, depende do MP.
     */
    MAGO(
            80,
            120,  // mp alto
            3,
            20,   // atq alto mas é mágico
            5,
            10,
            10
    ),

    /**
     * Distância e sangramento (DoT por turnos).
     * Velocidade média-alta. Causa dano passivo com veneno/sangramento.
     * Ponto fraco: dano direto menor que guerreiro.
     */
    ARQUEIRO(
            100,
            50,
            4,
            15,
            8,
            14,
            12    // sorte decente — chances de aplicar status
    );

    private final int hpBase;
    private final int mpBase;
    private final int apBase;
    private final int atqBase;
    private final int defBase;
    private final int velocidadeBase;
    private final int sorteBase;
}
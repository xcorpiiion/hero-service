package br.com.study.gameapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Tipos de memória que o personagem carrega.
 * Memórias são queimadas ao realizar um crítico — o Gemini narra qual foi perdida.
 * Sem memórias disponíveis, o crítico não pode ser executado.
 */
@Getter
@RequiredArgsConstructor
public enum TipoMemoriaType {

    INFANCIA("Memória da infância", "Fragmentos do que você foi antes do sistema"),
    PESSOA_AMADA("Memória de alguém amado", "Um rosto que o código ainda não apagou"),
    CONQUISTA("Memória de uma conquista", "A prova de que você já foi mais do que um NPC"),
    LUGAR("Memória de um lugar", "Coordenadas afetivas em um mundo sem geografia"),
    ALEATORIO("Fragmento aleatório", "O Gemini decide o que foi perdido");

    private final String label;
    private final String descricao;
}
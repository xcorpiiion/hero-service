package br.com.study.gameapi.service.personagem;


import br.com.study.gameapi.model.dto.MemoriaQueimadaResponse;
import br.com.study.gameapi.model.dto.SoulDropResponse;

public interface PersonagemBatalhaService {

    /** Morte normal — personagem tinha Bits, cria SoulDrop */
    SoulDropResponse notificarMorte(Long personagemId, long bitsConscienciaPerdidos, String localizacao);

    /** Hollow Digital — sem Bits, mente vira NPC */
    void notificarHollow(Long personagemId);

    /** Queima a primeira memória disponível — retorna pra combat-api repassar ao Gemini */
    MemoriaQueimadaResponse queimarMemoria(Long personagemId);

    /** Sincroniza HP, MP e Bits após vitória */
    void sincronizarVitoria(Long personagemId, int hpAtual, int mpAtual, long bitsGanhos, long xpGanha);
}
package br.com.study.gameapi.controller.personagem;

import br.com.study.gameapi.model.dto.MemoriaQueimadaResponse;
import br.com.study.gameapi.model.dto.SoulDropResponse;
import br.com.study.gameapi.service.personagem.PersonagemBatalhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Endpoints internos — chamados pela combat-api via Feign.
 * Não são chamados diretamente pelo front.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/personagens")
@Tag(name = "Batalha", description = "Endpoints internos de sincronização com a combat-api")
public class PersonagemBatalhaController {

    private final PersonagemBatalhaService service;

    @Operation(summary = "Registra morte normal — cria SoulDrop com os Bits perdidos")
    @PostMapping(value = "/{personagemId}/morte",
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SoulDropResponse> notificarMorte(
            @PathVariable Long personagemId,
            @RequestBody MorteRequest request
    ) {
        return ResponseEntity.ok(
                service.notificarMorte(personagemId, request.bitsConscienciaPerdidos(), request.localizacao())
        );
    }

    @Operation(summary = "Registra Hollow Digital — mente do personagem vira NPC permanentemente")
    @PostMapping("/{personagemId}/hollow")
    public ResponseEntity<Void> notificarHollow(@PathVariable Long personagemId) {
        service.notificarHollow(personagemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Queima a primeira memória disponível — retorna pra Gemini narrar")
    @PostMapping(value = "/{personagemId}/memorias/queimar", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<MemoriaQueimadaResponse> queimarMemoria(
            @PathVariable Long personagemId
    ) {
        return ResponseEntity.ok(service.queimarMemoria(personagemId));
    }

    @Operation(summary = "Sincroniza HP, MP e Bits após vitória")
    @PutMapping(value = "/{personagemId}/sincronizar-batalha",
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sincronizarVitoria(
            @PathVariable Long personagemId,
            @RequestBody SincronizarVitoriaRequest request
    ) {
        service.sincronizarVitoria(
                personagemId,
                request.hpAtual(), request.mpAtual(),
                request.bitsConscienciaGanhos(), request.experienciaGanha()
        );
        return ResponseEntity.noContent().build();
    }

    // ─── Request records internos ─────────────────────────────────────────────

    public record MorteRequest(long bitsConscienciaPerdidos, String localizacao) {
    }

    public record SincronizarVitoriaRequest(
            int hpAtual, int mpAtual,
            long bitsConscienciaGanhos,
            long experienciaGanha
    ) {
    }
}
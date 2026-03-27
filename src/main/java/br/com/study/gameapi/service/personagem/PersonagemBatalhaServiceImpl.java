package br.com.study.gameapi.service.personagem;

import br.com.study.gameapi.model.dto.MemoriaQueimadaResponse;
import br.com.study.gameapi.model.dto.SoulDropResponse;
import br.com.study.gameapi.model.memoria.Memoria;
import br.com.study.gameapi.model.personagem.Personagem;
import br.com.study.gameapi.repository.MemoriaRepository;
import br.com.study.gameapi.repository.PersonagemRepository;
import br.com.study.genericcrud.service.exception.DataIntegrityException;
import br.com.study.genericcrud.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonagemBatalhaServiceImpl implements PersonagemBatalhaService {

    private final PersonagemRepository personagemRepositorio;
    private final MemoriaRepository memoriaRepositorio;

    @Transactional
    @Override
    public SoulDropResponse notificarMorte(Long personagemId, long bitsConscienciaPerdidos, String localizacao) {
        Personagem personagem = buscarPersonagem(personagemId);

        if (personagem.isHollow()) {
            throw new DataIntegrityException("Personagem já é um Hollow Digital.");
        }

        // Zera os Bits e marca como morto temporariamente
        personagem.setBitsConsciencia(0);
        personagem.setVivo(false);
        personagemRepositorio.save(personagem);

        // SoulDrop é um conceito narrativo no MVP — ID gerado localmente
        Long soulDropId = System.currentTimeMillis();

        log.info("Morte registrada: {} perdeu {} Bits. SoulDrop: {} em '{}'",
                personagem.getNome(), bitsConscienciaPerdidos, soulDropId, localizacao);

        return new SoulDropResponse(soulDropId, localizacao, bitsConscienciaPerdidos);
    }

    @Transactional
    @Override
    public void notificarHollow(Long personagemId) {
        Personagem personagem = buscarPersonagem(personagemId);

        if (personagem.isHollow()) {
            log.warn("Personagem {} já era Hollow — ignorando", personagem.getNome());
            return;
        }

        personagem.tornarHollow();
        personagemRepositorio.save(personagem);

        log.info("HOLLOW DIGITAL: {} foi corrompido pelo sistema. Run encerrada permanentemente.",
                personagem.getNome());
    }

    @Transactional
    @Override
    public MemoriaQueimadaResponse queimarMemoria(Long personagemId) {
        Personagem personagem = buscarPersonagem(personagemId);

        Memoria memoria = memoriaRepositorio
                .findFirstByPersonagemIdAndQueimadaFalse(personagemId)
                .orElseThrow(() -> new DataIntegrityException(
                        "Sem memórias disponíveis. Crítico impossível."
                ));

        memoria.setQueimada(true);
        memoriaRepositorio.save(memoria);

        log.info("Memória queimada: {} perdeu '{}' ({})",
                personagem.getNome(), memoria.getDescricao(), memoria.getTipo());

        return new MemoriaQueimadaResponse(
                memoria.getId(),
                memoria.getTipo(),
                memoria.getDescricao()
        );
    }

    @Transactional
    @Override
    public void sincronizarVitoria(Long personagemId, int hpAtual, int mpAtual,
                                   long bitsGanhos, long xpGanha) {
        Personagem personagem = buscarPersonagem(personagemId);

        personagem.setHpAtual(Math.min(hpAtual, personagem.getHpMaximo()));
        personagem.setMpAtual(Math.min(mpAtual, personagem.getMpMaximo()));
        personagem.setBitsConsciencia(personagem.getBitsConsciencia() + bitsGanhos);
        personagem.setExperiencia(personagem.getExperiencia() + xpGanha);
        personagem.setVivo(true);

        personagemRepositorio.save(personagem);

        log.info("Vitória sincronizada: {} ganhou {} Bits e {} XP",
                personagem.getNome(), bitsGanhos, xpGanha);
    }

    private Personagem buscarPersonagem(Long personagemId) {
        return personagemRepositorio.findById(personagemId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Personagem não encontrado: " + personagemId
                ));
    }
}
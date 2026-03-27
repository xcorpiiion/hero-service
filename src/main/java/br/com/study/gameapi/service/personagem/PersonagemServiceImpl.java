package br.com.study.gameapi.service.personagem;

import br.com.study.gameapi.model.dto.ItemInventarioResponse;
import br.com.study.gameapi.model.dto.PersonagemRequest;
import br.com.study.gameapi.model.dto.PersonagemResponse;
import br.com.study.gameapi.model.enums.ClassePersonagemType;
import br.com.study.gameapi.model.inventario.ItemInventario;
import br.com.study.gameapi.model.personagem.Personagem;
import br.com.study.gameapi.repository.PersonagemRepository;
import br.com.study.genericcrud.service.exception.DataIntegrityException;
import br.com.study.genericcrud.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonagemServiceImpl implements PersonagemService {

    private final PersonagemRepository repositorio;

    @Transactional
    @Override
    public PersonagemResponse.Detalhes criar(PersonagemRequest.CriarPersonagem request, Long usuarioId) {
        if (repositorio.existsByNomeAndUsuarioId(request.nome(), usuarioId)) {
            throw new DataIntegrityException(
                    "Você já tem um agente com o nome '%s'.".formatted(request.nome())
            );
        }

        Personagem personagem = new Personagem();
        personagem.setNome(request.nome());
        personagem.setClasse(request.classe());
        personagem.setUsuarioId(usuarioId);

        aplicarAtributosBase(personagem, request.classe());

        repositorio.save(personagem);
        log.info("Agente criado: {} ({}) para usuário {}", personagem.getNome(), personagem.getClasse(), usuarioId);

        return toDetalhes(personagem);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonagemResponse.Resumo> listarPorUsuario(Long usuarioId) {
        return repositorio.findByUsuarioId(usuarioId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PersonagemResponse.Detalhes buscarPorId(Long id, Long usuarioId) {
        return repositorio.findByIdAndUsuarioId(id, usuarioId)
                .map(this::toDetalhes)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Agente não encontrado com id: " + id
                ));
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private void aplicarAtributosBase(Personagem personagem, ClassePersonagemType classe) {
        personagem.setHpMaximo(classe.getHpBase());
        personagem.setHpAtual(classe.getHpBase());
        personagem.setMpMaximo(classe.getMpBase());
        personagem.setMpAtual(classe.getMpBase());
        personagem.setApMaximo(classe.getApBase());
        personagem.setAtaque(classe.getAtqBase());
        personagem.setDefesa(classe.getDefBase());
        personagem.setVelocidade(classe.getVelocidadeBase());
        personagem.setSorte(classe.getSorteBase());
    }

    private PersonagemResponse.Resumo toResumo(Personagem p) {
        return new PersonagemResponse.Resumo(
                p.getId(),
                p.getNome(),
                p.getClasse(),
                p.getNivel(),
                p.getHpAtual(),
                p.getHpMaximo(),
                p.getBitsConsciencia(),
                p.isVivo(),
                p.isHollow()
        );
    }

    private PersonagemResponse.Detalhes toDetalhes(Personagem p) {
        int slotsUsados = p.getInventario().stream()
                .mapToInt(ItemInventario::getQuantidade)
                .sum();

        List<ItemInventarioResponse.Resumo> inventario = p.getInventario().stream()
                .map(item -> new ItemInventarioResponse.Resumo(
                        item.getId(),
                        item.getNome(),
                        item.getTipo(),
                        item.getQuantidade(),
                        item.isEquipado()
                ))
                .toList();

        int memoriasDisponiveis = (int) p.getMemorias().stream()
                .filter(m -> !m.isQueimada())
                .count();

        return new PersonagemResponse.Detalhes(
                p.getId(),
                p.getNome(),
                p.getClasse(),
                p.getNivel(),
                p.getExperiencia(),
                p.getBitsConsciencia(),
                p.getHpAtual(),
                p.getHpMaximo(),
                p.getMpAtual(),
                p.getMpMaximo(),
                p.getApMaximo(),
                p.getAtaque(),
                p.getDefesa(),
                p.getVelocidade(),
                p.getSorte(),
                p.isHollow(),
                memoriasDisponiveis,
                p.getMemorias().size(),
                slotsUsados,
                p.getLimiteInventario(),
                inventario,
                p.isVivo()
        );
    }
}
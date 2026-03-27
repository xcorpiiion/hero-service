package br.com.study.gameapi.service.inventario;

import br.com.study.gameapi.model.dto.ItemInventarioRequest;
import br.com.study.gameapi.model.dto.ItemInventarioResponse;
import br.com.study.gameapi.model.inventario.ItemInventario;
import br.com.study.gameapi.model.personagem.Personagem;
import br.com.study.gameapi.repository.ItemInventarioRepository;
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
public class ItemInventarioServiceImpl implements ItemInventarioService {

    private final ItemInventarioRepository repositorio;
    private final PersonagemRepository personagemRepositorio;

    @Transactional
    @Override
    public ItemInventarioResponse.Detalhes adicionarItem(
            ItemInventarioRequest.AdicionarItem request,
            Long usuarioId
    ) {
        Personagem personagem = buscarPersonagem(request.personagemId(), usuarioId);

        validarEspacoInventario(personagem, request.quantidade());
        validarCamposItem(request);

        ItemInventario item = new ItemInventario();
        item.setPersonagem(personagem);
        item.setNome(request.nome());
        item.setDescricao(request.descricao());
        item.setTipo(request.tipo());
        item.setQuantidade(request.quantidade());
        item.setCuraHp(request.curaHp());
        item.setRestauraMp(request.restauraMp());
        item.setDuracaoTurnos(request.duracaoTurnos());
        item.setValorEfeito(request.valorEfeito());
        item.setSlot(request.slot());
        item.setBonusAtaque(request.bonusAtaque());
        item.setBonusDefesa(request.bonusDefesa());
        item.setBonusHp(request.bonusHp());
        item.setBonusVelocidade(request.bonusVelocidade());
        item.setBonusSorte(request.bonusSorte());

        repositorio.save(item);
        log.info("Item '{}' adicionado ao inventário de {}", item.getNome(), personagem.getNome());

        return toDetalhes(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemInventarioResponse.Resumo> listarInventario(Long personagemId, Long usuarioId) {
        buscarPersonagem(personagemId, usuarioId);
        return repositorio.findByPersonagemId(personagemId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ItemInventarioResponse.Detalhes buscarItem(Long itemId, Long personagemId, Long usuarioId) {
        buscarPersonagem(personagemId, usuarioId);
        return repositorio.findByIdAndPersonagemId(itemId, personagemId)
                .map(this::toDetalhes)
                .orElseThrow(() -> new ObjectNotFoundException("Item não encontrado: " + itemId));
    }

    @Transactional
    @Override
    public ItemInventarioResponse.Detalhes equiparItem(
            ItemInventarioRequest.EquiparItem request,
            Long usuarioId
    ) {
        Personagem personagem = buscarPersonagem(request.personagemId(), usuarioId);

        ItemInventario item = repositorio.findByIdAndPersonagemId(request.itemId(), request.personagemId())
                .orElseThrow(() -> new ObjectNotFoundException("Item não encontrado: " + request.itemId()));

        if (!item.podeEquipar()) {
            throw new DataIntegrityException(
                    item.isEquipado()
                            ? "Este item já está equipado."
                            : "Este item não pode ser equipado (é consumível)."
            );
        }

        // Se já tem algo no slot, desequipa o anterior
        repositorio.findByPersonagemIdAndSlotAndEquipadoTrue(personagem.getId(), item.getSlot())
                .ifPresent(anterior -> {
                    anterior.setEquipado(false);
                    aplicarBonusEquipamento(personagem, anterior, false);
                    repositorio.save(anterior);
                    log.info("Desequipado '{}' do slot {}", anterior.getNome(), anterior.getSlot());
                });

        // Equipa o novo item
        item.setEquipado(true);
        aplicarBonusEquipamento(personagem, item, true);

        repositorio.save(item);
        personagemRepositorio.save(personagem);

        log.info("'{}' equipado em {} no slot {}", item.getNome(), personagem.getNome(), item.getSlot());
        return toDetalhes(item);
    }

    @Transactional
    @Override
    public ItemInventarioResponse.Detalhes desequiparItem(
            ItemInventarioRequest.EquiparItem request,
            Long usuarioId
    ) {
        Personagem personagem = buscarPersonagem(request.personagemId(), usuarioId);

        ItemInventario item = repositorio.findByIdAndPersonagemId(request.itemId(), request.personagemId())
                .orElseThrow(() -> new ObjectNotFoundException("Item não encontrado: " + request.itemId()));

        if (!item.isEquipado()) {
            throw new DataIntegrityException("Este item não está equipado.");
        }

        item.setEquipado(false);
        aplicarBonusEquipamento(personagem, item, false);

        repositorio.save(item);
        personagemRepositorio.save(personagem);

        log.info("'{}' desequipado de {}", item.getNome(), personagem.getNome());
        return toDetalhes(item);
    }

    @Transactional
    @Override
    public void removerItem(Long itemId, Long personagemId, Long usuarioId) {
        buscarPersonagem(personagemId, usuarioId);

        ItemInventario item = repositorio.findByIdAndPersonagemId(itemId, personagemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item não encontrado: " + itemId));

        if (item.isEquipado()) {
            throw new DataIntegrityException("Desequipe o item antes de removê-lo.");
        }

        repositorio.delete(item);
        log.info("Item '{}' removido do inventário", item.getNome());
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    /**
     * Aplica ou remove os bônus do equipamento nos atributos do personagem.
     * equipar=true adiciona os bônus, equipar=false remove.
     */
    private void aplicarBonusEquipamento(Personagem p, ItemInventario item, boolean equipar) {
        int fator = equipar ? 1 : -1;
        p.setAtaque(p.getAtaque()       + (item.getBonusAtaque()     * fator));
        p.setDefesa(p.getDefesa()       + (item.getBonusDefesa()     * fator));
        p.setHpMaximo(p.getHpMaximo()   + (item.getBonusHp()         * fator));
        p.setVelocidade(p.getVelocidade()+ (item.getBonusVelocidade() * fator));
        p.setSorte(p.getSorte()         + (item.getBonusSorte()      * fator));

        // Se o equipamento aumenta HP máximo, aumenta o HP atual proporcionalmente
        if (item.getBonusHp() > 0) {
            p.setHpAtual(p.getHpAtual() + (item.getBonusHp() * fator));
        }
    }

    private void validarEspacoInventario(Personagem personagem, int quantidade) {
        int slotsUsados = personagem.getInventario().stream()
                .mapToInt(ItemInventario::getQuantidade)
                .sum();

        if (slotsUsados + quantidade > personagem.getLimiteInventario()) {
            throw new DataIntegrityException(
                    "Inventário cheio! Slots disponíveis: %d, tentando adicionar: %d"
                            .formatted(personagem.getLimiteInventario() - slotsUsados, quantidade)
            );
        }
    }

    private void validarCamposItem(ItemInventarioRequest.AdicionarItem request) {
        if (request.tipo().isEquipamento() && request.slot() == null) {
            throw new DataIntegrityException("Equipamentos precisam de um slot definido.");
        }
        if (request.tipo().isConsumivel() && request.slot() != null) {
            throw new DataIntegrityException("Consumíveis não podem ter slot de equipamento.");
        }
    }

    private Personagem buscarPersonagem(Long personagemId, Long usuarioId) {
        return personagemRepositorio.findByIdAndUsuarioId(personagemId, usuarioId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Personagem não encontrado: " + personagemId
                ));
    }

    private ItemInventarioResponse.Resumo toResumo(ItemInventario item) {
        return new ItemInventarioResponse.Resumo(
                item.getId(),
                item.getNome(),
                item.getTipo(),
                item.getQuantidade(),
                item.isEquipado()
        );
    }

    private ItemInventarioResponse.Detalhes toDetalhes(ItemInventario item) {
        return new ItemInventarioResponse.Detalhes(
                item.getId(),
                item.getNome(),
                item.getDescricao(),
                item.getTipo(),
                item.getQuantidade(),
                item.isEquipado(),
                item.getSlot(),
                item.getCuraHp(),
                item.getRestauraMp(),
                item.getDuracaoTurnos(),
                item.getValorEfeito(),
                item.getBonusAtaque(),
                item.getBonusDefesa(),
                item.getBonusHp(),
                item.getBonusVelocidade(),
                item.getBonusSorte()
        );
    }
}
package br.com.study.gameapi.service.inventario;

import br.com.study.gameapi.model.dto.ItemInventarioRequest;
import br.com.study.gameapi.model.dto.ItemInventarioResponse;
import br.com.study.gameapi.model.enums.ClassePersonagemType;
import br.com.study.gameapi.model.enums.SlotEquipamentoType;
import br.com.study.gameapi.model.enums.TipoItemType;
import br.com.study.gameapi.model.inventario.ItemInventario;
import br.com.study.gameapi.model.personagem.Personagem;
import br.com.study.gameapi.repository.ItemInventarioRepository;
import br.com.study.gameapi.repository.PersonagemRepository;
import br.com.study.genericcrud.service.exception.DataIntegrityException;
import br.com.study.genericcrud.service.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemInventarioServiceImpl")
class ItemInventarioServiceImplTest {

    @Mock
    ItemInventarioRepository repositorio;
    @Mock
    PersonagemRepository personagemRepositorio;
    @InjectMocks
    ItemInventarioServiceImpl service;

    private static final Long USUARIO_ID = 1L;
    private static final Long PERSONAGEM_ID = 1L;

    private Personagem personagem;

    @BeforeEach
    void setUp() {
        personagem = new Personagem();
        personagem.setId(PERSONAGEM_ID);
        personagem.setNome("Solaire");
        personagem.setClasse(ClassePersonagemType.GUERREIRO);
        personagem.setUsuarioId(USUARIO_ID);
        personagem.setHpMaximo(150);
        personagem.setHpAtual(150);
        personagem.setAtaque(18);
        personagem.setDefesa(12);
        personagem.setVelocidade(8);
        personagem.setSorte(5);
        personagem.setLimiteInventario(20);
        personagem.setInventario(new ArrayList<>());
    }

    // ─────────────────────────────────────────────
    // adicionarItem
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("adicionarItem")
    class AdicionarItem {

        @Test
        @DisplayName("adiciona poção com sucesso")
        void pocaoValida_adicionada() {
            var request = new ItemInventarioRequest.AdicionarItem(
                    PERSONAGEM_ID, "Poção de Cura", "Restaura 50 HP",
                    TipoItemType.POCAO, 3,
                    50, 0, 0, 0,
                    null, 0, 0, 0, 0, 0
            );

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));
            when(repositorio.save(any())).thenAnswer(inv -> {
                ItemInventario item = inv.getArgument(0);
                item.setId(1L);
                return item;
            });

            ItemInventarioResponse.Detalhes response = service.adicionarItem(request, USUARIO_ID);

            assertThat(response.nome()).isEqualTo("Poção de Cura");
            assertThat(response.tipo()).isEqualTo(TipoItemType.POCAO);
            assertThat(response.curaHp()).isEqualTo(50);
            verify(repositorio).save(any());
        }

        @Test
        @DisplayName("lança DataIntegrityException quando inventário está cheio")
        void inventarioCheio_lancaExcecao() {
            // Inventário com 20 itens (limite máximo)
            List<ItemInventario> itens = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                ItemInventario item = new ItemInventario();
                item.setQuantidade(1);
                itens.add(item);
            }
            personagem.setInventario(itens);

            var request = new ItemInventarioRequest.AdicionarItem(
                    PERSONAGEM_ID, "Poção", null,
                    TipoItemType.POCAO, 1,
                    50, 0, 0, 0,
                    null, 0, 0, 0, 0, 0
            );

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));

            assertThatThrownBy(() -> service.adicionarItem(request, USUARIO_ID))
                    .isInstanceOf(DataIntegrityException.class)
                    .hasMessageContaining("cheio");

            verify(repositorio, never()).save(any());
        }

        @Test
        @DisplayName("lança DataIntegrityException quando equipamento não tem slot")
        void equipamentoSemSlot_lancaExcecao() {
            var request = new ItemInventarioRequest.AdicionarItem(
                    PERSONAGEM_ID, "Espada Longa", null,
                    TipoItemType.ARMA, 1,
                    0, 0, 0, 0,
                    null, // slot null — inválido para equipamento
                    10, 0, 0, 0, 0
            );

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));

            assertThatThrownBy(() -> service.adicionarItem(request, USUARIO_ID))
                    .isInstanceOf(DataIntegrityException.class)
                    .hasMessageContaining("slot");
        }

        @Test
        @DisplayName("lança DataIntegrityException quando consumível tem slot")
        void consumivelComSlot_lancaExcecao() {
            var request = new ItemInventarioRequest.AdicionarItem(
                    PERSONAGEM_ID, "Poção Estranha", null,
                    TipoItemType.POCAO, 1,
                    50, 0, 0, 0,
                    SlotEquipamentoType.MAO_PRINCIPAL, // slot inválido para consumível
                    0, 0, 0, 0, 0
            );

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));

            assertThatThrownBy(() -> service.adicionarItem(request, USUARIO_ID))
                    .isInstanceOf(DataIntegrityException.class);
        }
    }

    // ─────────────────────────────────────────────
    // equiparItem
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("equiparItem")
    class EquiparItem {

        @Test
        @DisplayName("equipa arma e aplica bônus de ataque no personagem")
        void armaValida_aplicaBonus() {
            ItemInventario espada = criarArma(1L, "Espada Longa", 15, false);

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));
            when(repositorio.findByIdAndPersonagemId(1L, PERSONAGEM_ID))
                    .thenReturn(Optional.of(espada));
            when(repositorio.findByPersonagemIdAndSlotAndEquipadoTrue(PERSONAGEM_ID, SlotEquipamentoType.MAO_PRINCIPAL))
                    .thenReturn(Optional.empty());
            when(repositorio.save(any())).thenReturn(espada);

            int ataqueAntes = personagem.getAtaque();
            service.equiparItem(new ItemInventarioRequest.EquiparItem(PERSONAGEM_ID, 1L), USUARIO_ID);

            assertThat(personagem.getAtaque()).isEqualTo(ataqueAntes + 15);
            verify(personagemRepositorio).save(personagem);
        }

        @Test
        @DisplayName("desequipa arma anterior ao equipar nova no mesmo slot")
        void slotOcupado_desequipaAnterior() {
            ItemInventario espadaAntiga = criarArma(1L, "Espada Velha", 5, true);
            ItemInventario espadaNova = criarArma(2L, "Espada Nova", 20, false);

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));
            when(repositorio.findByIdAndPersonagemId(2L, PERSONAGEM_ID))
                    .thenReturn(Optional.of(espadaNova));
            when(repositorio.findByPersonagemIdAndSlotAndEquipadoTrue(PERSONAGEM_ID, SlotEquipamentoType.MAO_PRINCIPAL))
                    .thenReturn(Optional.of(espadaAntiga));
            when(repositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Aplica bônus da antiga manualmente (simula estado atual)
            personagem.setAtaque(personagem.getAtaque() + 5);

            service.equiparItem(new ItemInventarioRequest.EquiparItem(PERSONAGEM_ID, 2L), USUARIO_ID);

            assertThat(espadaAntiga.isEquipado()).isFalse();
            assertThat(espadaNova.isEquipado()).isTrue();
        }

        @Test
        @DisplayName("lança DataIntegrityException ao tentar equipar consumível")
        void consumivel_lancaExcecao() {
            ItemInventario pocao = new ItemInventario();
            pocao.setId(1L);
            pocao.setTipo(TipoItemType.POCAO);
            pocao.setEquipado(false);

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));
            when(repositorio.findByIdAndPersonagemId(1L, PERSONAGEM_ID))
                    .thenReturn(Optional.of(pocao));

            assertThatThrownBy(() ->
                    service.equiparItem(new ItemInventarioRequest.EquiparItem(PERSONAGEM_ID, 1L), USUARIO_ID)
            ).isInstanceOf(DataIntegrityException.class);
        }
    }

    // ─────────────────────────────────────────────
    // removerItem
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("removerItem")
    class RemoverItem {

        @Test
        @DisplayName("remove item desequipado com sucesso")
        void itemDesequipado_removido() {
            ItemInventario item = new ItemInventario();
            item.setId(1L);
            item.setTipo(TipoItemType.POCAO);
            item.setEquipado(false);

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));
            when(repositorio.findByIdAndPersonagemId(1L, PERSONAGEM_ID))
                    .thenReturn(Optional.of(item));

            service.removerItem(1L, PERSONAGEM_ID, USUARIO_ID);

            verify(repositorio).delete(item);
        }

        @Test
        @DisplayName("lança DataIntegrityException ao tentar remover item equipado")
        void itemEquipado_lancaExcecao() {
            ItemInventario item = criarArma(1L, "Espada", 10, true);

            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));
            when(repositorio.findByIdAndPersonagemId(1L, PERSONAGEM_ID))
                    .thenReturn(Optional.of(item));

            assertThatThrownBy(() -> service.removerItem(1L, PERSONAGEM_ID, USUARIO_ID))
                    .isInstanceOf(DataIntegrityException.class)
                    .hasMessageContaining("Desequipe");

            verify(repositorio, never()).delete(any());
        }

        @Test
        @DisplayName("lança ObjectNotFoundException quando item não existe")
        void itemInexistente_lancaExcecao() {
            when(personagemRepositorio.findByIdAndUsuarioId(PERSONAGEM_ID, USUARIO_ID))
                    .thenReturn(Optional.of(personagem));
            when(repositorio.findByIdAndPersonagemId(99L, PERSONAGEM_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.removerItem(99L, PERSONAGEM_ID, USUARIO_ID))
                    .isInstanceOf(ObjectNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private ItemInventario criarArma(Long id, String nome, int bonusAtaque, boolean equipado) {
        ItemInventario arma = new ItemInventario();
        arma.setId(id);
        arma.setNome(nome);
        arma.setTipo(TipoItemType.ARMA);
        arma.setSlot(SlotEquipamentoType.MAO_PRINCIPAL);
        arma.setBonusAtaque(bonusAtaque);
        arma.setEquipado(equipado);
        arma.setQuantidade(1);
        arma.setPersonagem(personagem);
        return arma;
    }
}
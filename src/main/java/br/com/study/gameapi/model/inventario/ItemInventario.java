package br.com.study.gameapi.model.inventario;

import br.com.study.gameapi.model.enums.SlotEquipamentoType;
import br.com.study.gameapi.model.enums.TipoItemType;
import br.com.study.gameapi.model.personagem.Personagem;
import br.com.study.genericcrud.model.AbstractId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Item no inventário do personagem.
 * <p>
 * Cobre tanto consumíveis quanto equipamentos — o tipo determina
 * o comportamento na batalha.
 * <p>
 * Consumíveis: usam-se em batalha, passam o turno, somem após uso.
 * Equipamentos: equipam-se fora de batalha, alteram atributos do personagem.
 */
@Getter
@Setter
@Entity
@Table(name = "item_inventario")
public class ItemInventario extends AbstractId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personagem_id", nullable = false)
    private Personagem personagem;

    @NotBlank(message = "Nome do item é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "Tipo do item é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItemType tipo;

    @Min(value = 0)
    @Column(nullable = false)
    private int quantidade = 1;

    // ─── Efeitos de consumíveis ───────────────────────────

    /**
     * HP restaurado ao usar (POCAO)
     */
    @Column(nullable = false)
    private int curaHp = 0;

    /**
     * MP restaurado ao usar (ELIXIR)
     */
    @Column(nullable = false)
    private int restauraMp = 0;

    /**
     * Duração em turnos do efeito (PERGAMINHO, VENENO, PEDRA_DE_AFIAR).
     * 0 significa efeito instantâneo.
     */
    @Column(nullable = false)
    private int duracaoTurnos = 0;

    /**
     * Valor do efeito temporário.
     * PERGAMINHO: bônus de ATQ
     * VENENO: dano por turno
     * PEDRA_DE_AFIAR: bônus de ATQ por 1 batalha
     */
    @Column(nullable = false)
    private int valorEfeito = 0;

    // ─── Bônus de equipamentos ────────────────────────────

    /**
     * Slot onde o equipamento pode ser equipado (null se consumível)
     */
    @Enumerated(EnumType.STRING)
    private SlotEquipamentoType slot;

    @Column(nullable = false)
    private int bonusAtaque = 0;

    @Column(nullable = false)
    private int bonusDefesa = 0;

    @Column(nullable = false)
    private int bonusHp = 0;

    @Column(nullable = false)
    private int bonusVelocidade = 0;

    @Column(nullable = false)
    private int bonusSorte = 0;

    /**
     * Se true, este item está equipado no personagem
     */
    @Column(nullable = false)
    private boolean equipado = false;

    // ─── Utilitários ──────────────────────────────────────

    public boolean podeUsar() {
        return tipo.isConsumivel() && quantidade > 0;
    }

    public boolean podeEquipar() {
        return tipo.isEquipamento() && !equipado;
    }

    public void consumir() {
        if (tipo.isConsumivel() && quantidade > 0) {
            this.quantidade--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemInventario i)) return false;
        return id != null && id.equals(i.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
package br.com.study.gameapi.model.personagem;

import br.com.study.gameapi.model.enums.ClassePersonagemType;
import br.com.study.gameapi.model.inventario.ItemInventario;
import br.com.study.genericcrud.model.AbstractId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o herói do jogador.
 * <p>
 * Um jogador pode ter múltiplos personagens (runs diferentes).
 * O userId vem do JWT via header X-User-Id — não é uma FK para outra tabela
 * porque o usuário vive na user-api, não aqui.
 */
@Getter
@Setter
@Entity
@Table(name = "personagem")
public class Personagem extends AbstractId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Classe é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassePersonagemType classe;

    /**
     * Dono do personagem — vem do JWT, não é FK
     */
    @Column(nullable = false)
    private Long usuarioId;

    // ─── Atributos vitais ─────────────────────────────────
    @Column(nullable = false)
    private int hpMaximo;

    @Column(nullable = false)
    private int hpAtual;

    @Column(nullable = false)
    private int mpMaximo;

    @Column(nullable = false)
    private int mpAtual;

    /**
     * Action Points disponíveis por turno
     */
    @Column(nullable = false)
    private int apMaximo;

    // ─── Atributos de combate ──────────────────────────────
    @Column(nullable = false)
    private int ataque;

    @Column(nullable = false)
    private int defesa;

    @Column(nullable = false)
    private int velocidade;

    /**
     * Afeta chance de crítico e outros efeitos aleatórios
     */
    @Column(nullable = false)
    private int sorte;

    // ─── Progressão ───────────────────────────────────────
    @Column(nullable = false)
    private int nivel = 1;

    @Column(nullable = false)
    private long experiencia = 0;

    /**
     * Almas acumuladas — perdidas na morte, recuperáveis
     */
    @Column(nullable = false)
    private long almas = 0;

    // ─── Inventário ───────────────────────────────────────
    @OneToMany(mappedBy = "personagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemInventario> inventario = new ArrayList<>();

    /**
     * Limite de slots do inventário
     */
    @Column(nullable = false)
    private int limiteInventario = 20;

    // ─── Estado ───────────────────────────────────────────
    @Column(nullable = false)
    private boolean vivo = true;

    // ─── Utilitários ──────────────────────────────────────

    public boolean estaMorto() {
        return hpAtual <= 0;
    }

    public void receberDano(int dano) {
        this.hpAtual = Math.max(0, this.hpAtual - dano);
        if (this.hpAtual == 0) this.vivo = false;
    }

    public void curar(int quantidade) {
        this.hpAtual = Math.min(this.hpMaximo, this.hpAtual + quantidade);
    }

    public void restaurarMp(int quantidade) {
        this.mpAtual = Math.min(this.mpMaximo, this.mpAtual + quantidade);
    }

    public boolean inventarioCheio() {
        int slotsUsados = inventario.stream()
                .mapToInt(ItemInventario::getQuantidade)
                .sum();
        return slotsUsados >= limiteInventario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Personagem p)) return false;
        return id != null && id.equals(p.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
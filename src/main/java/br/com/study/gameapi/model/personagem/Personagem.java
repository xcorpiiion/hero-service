package br.com.study.gameapi.model.personagem;

import br.com.study.gameapi.model.enums.ClassePersonagemType;
import br.com.study.gameapi.model.inventario.ItemInventario;
import br.com.study.gameapi.model.memoria.Memoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o agente do jogador dentro do Simulacro do Vazio.
 * <p>
 * Mecânicas especiais:
 * - bitsConsciencia: recurso principal, substitui almas
 * - memorias: queimadas em críticos, narradas pelo System Architect
 * - hollow: true = personagem virou NPC inimigo, run encerrada
 */
@Getter
@Setter
@Entity
@Table(name = "personagem")
public class Personagem {

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

    @Column(nullable = false)
    private Long usuarioId;

    // ─── Vitais ───────────────────────────────────────────
    @Column(nullable = false)
    private int hpMaximo;
    @Column(nullable = false)
    private int hpAtual;
    @Column(nullable = false)
    private int mpMaximo;
    @Column(nullable = false)
    private int mpAtual;
    @Column(nullable = false)
    private int apMaximo;

    // ─── Combate ──────────────────────────────────────────
    @Column(nullable = false)
    private int ataque;
    @Column(nullable = false)
    private int defesa;
    @Column(nullable = false)
    private int velocidade;
    @Column(nullable = false)
    private int sorte;

    // ─── Progressão ───────────────────────────────────────
    @Column(nullable = false)
    private int nivel = 1;
    @Column(nullable = false)
    private long experiencia = 0;

    /**
     * Bits de Consciência — substitui almas no Simulacro do Vazio.
     * Perdidos na morte, recuperáveis via SoulDrop no servidor.
     */
    @Column(nullable = false)
    private long bitsConsciencia = 0;

    /**
     * Hollow Digital — personagem morreu sem Bits de Consciência.
     * Mente virou código NPC. Não pode iniciar novas sessões.
     */
    @Column(nullable = false)
    private boolean hollow = false;

    /**
     * Memórias — queimadas ao executar críticos.
     * Sem memórias disponíveis = críticos impossíveis.
     */
    @OneToMany(mappedBy = "personagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Memoria> memorias = new ArrayList<>();

    @OneToMany(mappedBy = "personagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemInventario> inventario = new ArrayList<>();

    @Column(nullable = false)
    private int limiteInventario = 20;

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

    public List<Memoria> memoriasDisponiveis() {
        return memorias.stream().filter(m -> !m.isQueimada()).toList();
    }

    public boolean podeExecutarCritico() {
        return !memoriasDisponiveis().isEmpty();
    }

    /**
     * Queima a primeira memória disponível e a retorna pro Gemini narrar
     */
    public Memoria queimarMemoria() {
        return memoriasDisponiveis().stream()
                .findFirst()
                .map(m -> {
                    m.setQueimada(true);
                    return m;
                })
                .orElseThrow(() -> new IllegalStateException("Sem memórias disponíveis"));
    }

    /**
     * Morte sem Bits — queima tudo e vira Hollow
     */
    public void tornarHollow() {
        this.hollow = true;
        this.vivo = false;
        memorias.forEach(m -> m.setQueimada(true));
    }

    public boolean inventarioCheio() {
        return inventario.stream().mapToInt(ItemInventario::getQuantidade).sum() >= limiteInventario;
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
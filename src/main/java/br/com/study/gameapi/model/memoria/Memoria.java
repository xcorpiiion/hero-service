package br.com.study.gameapi.model.memoria;

import br.com.study.gameapi.model.enums.TipoMemoriaType;
import br.com.study.gameapi.model.personagem.Personagem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Uma memória do personagem — recurso consumível para críticos.
 * <p>
 * Quando o personagem executa um crítico na combat-api,
 * uma memória é marcada como queimada (queimada = true).
 * O Gemini recebe o tipo e narra narrativamente o que foi perdido.
 * <p>
 * Memórias queimadas não voltam. Sem memórias disponíveis,
 * críticos são impossíveis.
 * <p>
 * Ao morrer sem Bits de Consciência, o personagem vira Hollow Digital —
 * todas as memórias restantes são queimadas automaticamente.
 */
@Getter
@Setter
@Entity
@Table(name = "memoria")
public class Memoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personagem_id", nullable = false)
    private Personagem personagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMemoriaType tipo;

    /**
     * Descrição específica da memória — definida pelo jogador ao criar
     * ou pelo Gemini quando o tipo é ALEATORIO.
     * Ex: "O cheiro de pão da padaria da esquina"
     */
    @Column(columnDefinition = "TEXT")
    private String descricao;

    /**
     * Se true, esta memória foi queimada e não pode ser usada novamente
     */
    @Column(nullable = false)
    private boolean queimada = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Memoria m)) return false;
        return id != null && id.equals(m.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
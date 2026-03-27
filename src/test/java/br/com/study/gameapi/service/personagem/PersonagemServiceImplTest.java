package br.com.study.gameapi.service.personagem;

import br.com.study.gameapi.model.dto.PersonagemRequest;
import br.com.study.gameapi.model.dto.PersonagemResponse;
import br.com.study.gameapi.model.enums.ClassePersonagemType;
import br.com.study.gameapi.model.personagem.Personagem;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonagemServiceImpl")
class PersonagemServiceImplTest {

    @Mock
    PersonagemRepository repositorio;
    @InjectMocks PersonagemServiceImpl service;

    private static final Long USUARIO_ID = 1L;

    private Personagem personagemBase;

    @BeforeEach
    void setUp() {
        personagemBase = new Personagem();
        personagemBase.setId(1L);
        personagemBase.setNome("Solaire");
        personagemBase.setClasse(ClassePersonagemType.GUERREIRO);
        personagemBase.setUsuarioId(USUARIO_ID);
        personagemBase.setHpMaximo(150);
        personagemBase.setHpAtual(150);
        personagemBase.setMpMaximo(20);
        personagemBase.setMpAtual(20);
        personagemBase.setApMaximo(4);
        personagemBase.setAtaque(18);
        personagemBase.setDefesa(12);
        personagemBase.setVelocidade(8);
        personagemBase.setSorte(5);
        personagemBase.setVivo(true);
    }

    // ─────────────────────────────────────────────
    // criar
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("cria personagem com atributos base da classe")
        void classeGuerreiro_aplicaAtributosBase() {
            var request = new PersonagemRequest.CriarPersonagem("Solaire", ClassePersonagemType.GUERREIRO);
            when(repositorio.existsByNomeAndUsuarioId("Solaire", USUARIO_ID)).thenReturn(false);
            when(repositorio.save(any())).thenReturn(personagemBase);

            PersonagemResponse.Detalhes response = service.criar(request, USUARIO_ID);

            assertThat(response.hpMaximo()).isEqualTo(ClassePersonagemType.GUERREIRO.getHpBase());
            assertThat(response.ataque()).isEqualTo(ClassePersonagemType.GUERREIRO.getAtqBase());
            assertThat(response.defesa()).isEqualTo(ClassePersonagemType.GUERREIRO.getDefBase());
            assertThat(response.velocidade()).isEqualTo(ClassePersonagemType.GUERREIRO.getVelocidadeBase());
            assertThat(response.sorte()).isEqualTo(ClassePersonagemType.GUERREIRO.getSorteBase());
        }

        @Test
        @DisplayName("lança DataIntegrityException quando nome já existe para o usuário")
        void nomeDuplicado_lancaExcecao() {
            var request = new PersonagemRequest.CriarPersonagem("Solaire", ClassePersonagemType.GUERREIRO);
            when(repositorio.existsByNomeAndUsuarioId("Solaire", USUARIO_ID)).thenReturn(true);

            assertThatThrownBy(() -> service.criar(request, USUARIO_ID))
                    .isInstanceOf(DataIntegrityException.class)
                    .hasMessageContaining("Solaire");

            verify(repositorio, never()).save(any());
        }

        @Test
        @DisplayName("personagem começa no nível 1 com 0 almas e 0 experiência")
        void novoPersonagem_estadoInicial() {
            var request = new PersonagemRequest.CriarPersonagem("Artorias", ClassePersonagemType.ASSASSINO);

            Personagem assassino = new Personagem();
            assassino.setId(2L);
            assassino.setNome("Artorias");
            assassino.setClasse(ClassePersonagemType.ASSASSINO);
            assassino.setUsuarioId(USUARIO_ID);
            assassino.setHpMaximo(ClassePersonagemType.ASSASSINO.getHpBase());
            assassino.setHpAtual(ClassePersonagemType.ASSASSINO.getHpBase());
            assassino.setMpMaximo(ClassePersonagemType.ASSASSINO.getMpBase());
            assassino.setMpAtual(ClassePersonagemType.ASSASSINO.getMpBase());
            assassino.setApMaximo(ClassePersonagemType.ASSASSINO.getApBase());
            assassino.setAtaque(ClassePersonagemType.ASSASSINO.getAtqBase());
            assassino.setDefesa(ClassePersonagemType.ASSASSINO.getDefBase());
            assassino.setVelocidade(ClassePersonagemType.ASSASSINO.getVelocidadeBase());
            assassino.setSorte(ClassePersonagemType.ASSASSINO.getSorteBase());
            assassino.setVivo(true);

            when(repositorio.existsByNomeAndUsuarioId("Artorias", USUARIO_ID)).thenReturn(false);
            when(repositorio.save(any())).thenReturn(assassino);

            PersonagemResponse.Detalhes response = service.criar(request, USUARIO_ID);

            assertThat(response.nivel()).isEqualTo(1);
            assertThat(response.almas()).isZero();
            assertThat(response.experiencia()).isZero();
            assertThat(response.vivo()).isTrue();
        }

        @Test
        @DisplayName("personagem começa com HP atual igual ao HP máximo")
        void novoPersonagem_hpAtualIgualHpMaximo() {
            var request = new PersonagemRequest.CriarPersonagem("Solaire", ClassePersonagemType.MAGO);
            when(repositorio.existsByNomeAndUsuarioId(any(), any())).thenReturn(false);
            when(repositorio.save(any())).thenAnswer(inv -> {
                Personagem p = inv.getArgument(0);
                p.setId(3L);
                return p;
            });

            PersonagemResponse.Detalhes response = service.criar(request, USUARIO_ID);

            assertThat(response.hpAtual()).isEqualTo(response.hpMaximo());
            assertThat(response.mpAtual()).isEqualTo(response.mpMaximo());
        }
    }

    // ─────────────────────────────────────────────
    // listarPorUsuario
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("listarPorUsuario")
    class ListarPorUsuario {

        @Test
        @DisplayName("retorna lista de resumos do usuário")
        void usuarioComPersonagens_retornaLista() {
            when(repositorio.findByUsuarioId(USUARIO_ID)).thenReturn(List.of(personagemBase));

            List<PersonagemResponse.Resumo> resultado = service.listarPorUsuario(USUARIO_ID);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.getFirst().nome()).isEqualTo("Solaire");
            assertThat(resultado.getFirst().classe()).isEqualTo(ClassePersonagemType.GUERREIRO);
        }

        @Test
        @DisplayName("retorna lista vazia quando usuário não tem personagens")
        void usuarioSemPersonagens_retornaListaVazia() {
            when(repositorio.findByUsuarioId(USUARIO_ID)).thenReturn(List.of());

            List<PersonagemResponse.Resumo> resultado = service.listarPorUsuario(USUARIO_ID);

            assertThat(resultado).isEmpty();
        }
    }

    // ─────────────────────────────────────────────
    // buscarPorId
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("retorna detalhes quando personagem existe e pertence ao usuário")
        void personagemExistente_retornaDetalhes() {
            when(repositorio.findByIdAndUsuarioId(1L, USUARIO_ID))
                    .thenReturn(Optional.of(personagemBase));

            PersonagemResponse.Detalhes resultado = service.buscarPorId(1L, USUARIO_ID);

            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.nome()).isEqualTo("Solaire");
        }

        @Test
        @DisplayName("lança ObjectNotFoundException quando personagem não existe")
        void personagemInexistente_lancaExcecao() {
            when(repositorio.findByIdAndUsuarioId(99L, USUARIO_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(99L, USUARIO_ID))
                    .isInstanceOf(ObjectNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lança ObjectNotFoundException quando personagem pertence a outro usuário")
        void personagemDeOutroUsuario_lancaExcecao() {
            when(repositorio.findByIdAndUsuarioId(1L, 999L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(1L, 999L))
                    .isInstanceOf(ObjectNotFoundException.class);
        }
    }
}
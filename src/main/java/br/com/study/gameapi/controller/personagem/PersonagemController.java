package br.com.study.gameapi.controller.personagem;

import br.com.study.gameapi.model.dto.PersonagemRequest;
import br.com.study.gameapi.model.dto.PersonagemResponse;
import br.com.study.gameapi.service.personagem.PersonagemService;
import br.com.study.genericauthorization.annotation.CurrentUser;
import br.com.study.genericauthorization.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/personagens")
@Tag(name = "Personagens", description = "Criação e gestão de heróis")
public class PersonagemController {

    private final PersonagemService service;

    @Operation(summary = "Cria um novo herói")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonagemResponse.Detalhes> criar(
            @Valid @RequestBody PersonagemRequest.CriarPersonagem request,
            @CurrentUser UserPrincipal usuario
    ) {
        return ResponseEntity.status(CREATED)
                .body(service.criar(request, usuario.getId()));
    }

    @Operation(summary = "Lista todos os heróis do jogador")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonagemResponse.Resumo>> listar(
            @CurrentUser UserPrincipal usuario
    ) {
        return ResponseEntity.ok(service.listarPorUsuario(usuario.getId()));
    }

    @Operation(summary = "Busca um herói pelo ID")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonagemResponse.Detalhes> buscarPorId(
            @PathVariable Long id,
            @CurrentUser UserPrincipal usuario
    ) {
        return ResponseEntity.ok(service.buscarPorId(id, usuario.getId()));
    }
}
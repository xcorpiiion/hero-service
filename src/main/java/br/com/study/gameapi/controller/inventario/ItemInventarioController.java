package br.com.study.gameapi.controller.inventario;

import br.com.study.gameapi.model.dto.ItemInventarioRequest;
import br.com.study.gameapi.model.dto.ItemInventarioResponse;
import br.com.study.gameapi.service.inventario.ItemInventarioService;
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
@RequestMapping("/personagens/{personagemId}/inventario")
@Tag(name = "Inventário", description = "Gestão de itens e equipamentos")
public class ItemInventarioController {

    private final ItemInventarioService service;

    @Operation(summary = "Adiciona um item ao inventário")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemInventarioResponse.Detalhes> adicionar(
            @PathVariable Long personagemId,
            @Valid @RequestBody ItemInventarioRequest.AdicionarItem request,
            @CurrentUser UserPrincipal usuario
    ) {
        return ResponseEntity.status(CREATED)
                .body(service.adicionarItem(request, usuario.getId()));
    }

    @Operation(summary = "Lista todos os itens do inventário")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemInventarioResponse.Resumo>> listar(
            @PathVariable Long personagemId,
            @CurrentUser UserPrincipal usuario
    ) {
        return ResponseEntity.ok(service.listarInventario(personagemId, usuario.getId()));
    }

    @Operation(summary = "Busca um item pelo ID")
    @GetMapping(value = "/{itemId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemInventarioResponse.Detalhes> buscar(
            @PathVariable Long personagemId,
            @PathVariable Long itemId,
            @CurrentUser UserPrincipal usuario
    ) {
        return ResponseEntity.ok(service.buscarItem(itemId, personagemId, usuario.getId()));
    }

    @Operation(summary = "Equipa um item no personagem")
    @PatchMapping(value = "/{itemId}/equipar", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemInventarioResponse.Detalhes> equipar(
            @PathVariable Long personagemId,
            @PathVariable Long itemId,
            @CurrentUser UserPrincipal usuario
    ) {
        var request = new ItemInventarioRequest.EquiparItem(personagemId, itemId);
        return ResponseEntity.ok(service.equiparItem(request, usuario.getId()));
    }

    @Operation(summary = "Desequipa um item do personagem")
    @PatchMapping(value = "/{itemId}/desequipar", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemInventarioResponse.Detalhes> desequipar(
            @PathVariable Long personagemId,
            @PathVariable Long itemId,
            @CurrentUser UserPrincipal usuario
    ) {
        var request = new ItemInventarioRequest.EquiparItem(personagemId, itemId);
        return ResponseEntity.ok(service.desequiparItem(request, usuario.getId()));
    }

    @Operation(summary = "Remove um item do inventário")
    @DeleteMapping(value = "/{itemId}")
    public ResponseEntity<Void> remover(
            @PathVariable Long personagemId,
            @PathVariable Long itemId,
            @CurrentUser UserPrincipal usuario
    ) {
        service.removerItem(itemId, personagemId, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
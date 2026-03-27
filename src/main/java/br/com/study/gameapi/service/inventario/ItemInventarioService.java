package br.com.study.gameapi.service.inventario;


import br.com.study.gameapi.model.dto.ItemInventarioRequest;
import br.com.study.gameapi.model.dto.ItemInventarioResponse;

import java.util.List;

public interface ItemInventarioService {

    ItemInventarioResponse.Detalhes adicionarItem(ItemInventarioRequest.AdicionarItem request, Long usuarioId);

    List<ItemInventarioResponse.Resumo> listarInventario(Long personagemId, Long usuarioId);

    ItemInventarioResponse.Detalhes buscarItem(Long itemId, Long personagemId, Long usuarioId);

    ItemInventarioResponse.Detalhes equiparItem(ItemInventarioRequest.EquiparItem request, Long usuarioId);

    ItemInventarioResponse.Detalhes desequiparItem(ItemInventarioRequest.EquiparItem request, Long usuarioId);

    void removerItem(Long itemId, Long personagemId, Long usuarioId);
}
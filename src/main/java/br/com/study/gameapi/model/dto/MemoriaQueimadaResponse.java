package br.com.study.gameapi.model.dto;


import br.com.study.gameapi.model.enums.TipoMemoriaType;

public record MemoriaQueimadaResponse(
        Long memoriaId,
        TipoMemoriaType tipo,
        String descricao
) {}
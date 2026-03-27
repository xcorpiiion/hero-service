package br.com.study.gameapi.model.dto;
 
public record SoulDropResponse(
        Long soulDropId,
        String localizacao,
        long bitsConscienciaPerdidos
) {}
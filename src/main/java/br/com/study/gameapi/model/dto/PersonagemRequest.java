package br.com.study.gameapi.model.dto;

import br.com.study.gameapi.model.enums.ClassePersonagemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PersonagemRequest {

    /**
     * Criação de um novo personagem.
     */
    public record CriarPersonagem(

            @NotBlank(message = "Nome é obrigatório")
            @Size(min = 2, max = 30, message = "Nome deve ter entre 2 e 30 caracteres")
            String nome,

            @NotNull(message = "Classe é obrigatória")
            ClassePersonagemType classe
    ) {}
}
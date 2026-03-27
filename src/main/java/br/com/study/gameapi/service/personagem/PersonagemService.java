package br.com.study.gameapi.service.personagem;


import br.com.study.gameapi.model.dto.PersonagemRequest;
import br.com.study.gameapi.model.dto.PersonagemResponse;

import java.util.List;

public interface PersonagemService {

    PersonagemResponse.Detalhes criar(PersonagemRequest.CriarPersonagem request, Long usuarioId);

    List<PersonagemResponse.Resumo> listarPorUsuario(Long usuarioId);

    PersonagemResponse.Detalhes buscarPorId(Long id, Long usuarioId);
}
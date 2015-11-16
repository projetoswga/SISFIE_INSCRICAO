package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.EsferaCurso;

public interface EsferaCursoService {

	List<EsferaCurso> listarEsferaCursos(Integer idCursoSelecionado) throws Exception;
}

package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.HomologacaoCurso;

public interface HomologacaoCursoService {

	List<HomologacaoCurso> listarHomologacaoCursos(Curso curso);

}

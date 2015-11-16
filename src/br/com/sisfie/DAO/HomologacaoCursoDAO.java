package br.com.sisfie.DAO;

import java.util.List;

import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.HomologacaoCurso;

public interface HomologacaoCursoDAO {

	List<HomologacaoCurso> listarHomologacaoCursos(Curso curso);

}

package br.com.sisfie.DAO;

import java.util.List;

import br.com.sisfie.entidade.Turma;


public interface TurmaDAO {

	List<Turma> recuperarTurmasDaGradeOficina(Integer idOficina, Integer idHorario, Integer idProfessor);

}

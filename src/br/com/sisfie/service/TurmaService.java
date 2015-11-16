package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.Turma;


public interface TurmaService {

	List<Turma> recuperarTurmasDaGradeOficina(Integer idOficina, Integer idHorario, Integer idProfessor);

}

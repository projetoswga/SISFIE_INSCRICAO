package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.GradeOficina;
import br.com.sisfie.entidade.InscricaoGrade;
import br.com.sisfie.entidade.Oficina;

public interface OficinaService {

	List<Oficina> recuperarOficinasDisponiveis(Integer idCurso);

	List<Oficina> recuperarOficina(Integer idCurso, Integer idPacote);

	List<Oficina> listarOficinas(Integer idInscricaoCurso);

	List<Integer> recuperarIdsOficinasDisponiveis(Integer idCursoSelecionado);

	List<GradeOficina> recuperarGradeOficinaPorHorario(List<Integer> listaOficinasDisponiveis, List<Integer> idsOficinasSelecionadas,
			Integer idHorario);

	List<GradeOficina> recuperarGradeOficinasVinculadas(Integer idHorario, Integer idOficina, Integer idTurma);

	Long countInscritosPorGradeOficina(Integer idCursoSelecionado, Integer idGradeOficina, List<Integer> idsParceiros);

	List<InscricaoGrade> recuperarInscricaoGrade(Integer idInscricao);

	Long countParceirosInscritos(Integer idCursoSelecionado, Integer idGradeOficina, List<Integer> idsParceiros);

}

package br.com.sisfie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.OficinaDAO;
import br.com.sisfie.entidade.GradeOficina;
import br.com.sisfie.entidade.InscricaoGrade;
import br.com.sisfie.entidade.Oficina;
import br.com.sisfie.service.OficinaService;

@Service(value = "oficinaService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class OficinaServiceImpl implements OficinaService {

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@Autowired(required = true)
	@Qualifier(value = "oficinaDAO")
	protected OficinaDAO oficinaDAO;

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	public OficinaDAO getOficinaDAO() {
		return oficinaDAO;
	}

	public void setOficinaDAO(OficinaDAO oficinaDAO) {
		this.oficinaDAO = oficinaDAO;
	}

	@Override
	public List<Oficina> recuperarOficinasDisponiveis(Integer idCurso) {
		return oficinaDAO.recuperarOficinasDisponiveis(idCurso);
	}

	@Override
	public List<Oficina> recuperarOficina(Integer idCurso, Integer idPacote) {
		return oficinaDAO.recuperarOficina(idCurso, idPacote);
	}

	@Override
	public List<Oficina> listarOficinas(Integer idInscricaoCurso) {
		return oficinaDAO.listarOficinas(idInscricaoCurso);
	}

	@Override
	public List<Integer> recuperarIdsOficinasDisponiveis(Integer idCursoSelecionado) {
		return oficinaDAO.recuperarIdsOficinasDisponiveis(idCursoSelecionado);
	}

	@Override
	public List<GradeOficina> recuperarGradeOficinaPorHorario(List<Integer> listaOficinasDisponiveis, List<Integer> idsOficinasSelecionadas, Integer idHorario) {
		return oficinaDAO.recuperarGradeOficinaPorHorario(listaOficinasDisponiveis, idsOficinasSelecionadas, idHorario);
	}

	@Override
	public List<GradeOficina> recuperarGradeOficinasVinculadas(Integer idHorario, Integer idOficina, Integer idTurma) {
		return oficinaDAO.recuperarGradeOficinasVinculadas(idHorario, idOficina, idTurma);
	}

	@Override
	public Long countInscritosPorGradeOficina(Integer idCursoSelecionado, Integer idGradeOficina, List<Integer> idsParceiros) {
		return oficinaDAO.countInscritosPorGradeOficina(idCursoSelecionado, idGradeOficina, idsParceiros);
	}

	@Override
	public List<InscricaoGrade> recuperarInscricaoGrade(Integer idInscricao) {
		return oficinaDAO.recuperarInscricaoGrade(idInscricao);
	}

	@Override
	public Long countParceirosInscritos(Integer idCursoSelecionado, Integer idGradeOficina, List<Integer> idsParceiros) {
		return oficinaDAO.countParceirosInscritos(idCursoSelecionado, idGradeOficina, idsParceiros);
	}
}

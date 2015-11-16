package br.com.sisfie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.TurmaDAO;
import br.com.sisfie.entidade.Turma;
import br.com.sisfie.service.TurmaService;

@Service(value = "turmaService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class TurmaServiceImpl implements TurmaService {
	
	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;
	
	@Autowired(required = true)
	@Qualifier(value = "turmaDAO")
	protected TurmaDAO turmaDAO;

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	public TurmaDAO getTurmaDAO() {
		return turmaDAO;
	}

	public void setTurmaDAO(TurmaDAO turmaDAO) {
		this.turmaDAO = turmaDAO;
	}

	@Override
	public List<Turma> recuperarTurmasDaGradeOficina(Integer idOficina, Integer idHorario, Integer idProfessor) {
		return turmaDAO.recuperarTurmasDaGradeOficina(idOficina, idHorario, idProfessor);
	}
}

package br.com.sisfie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.HomologacaoCursoDAO;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.HomologacaoCurso;
import br.com.sisfie.service.HomologacaoCursoService;

@Service(value = "homologacaoCursoService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class HomologacaoCursoImpl implements HomologacaoCursoService {
	
	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@Autowired(required = true)
	@Qualifier(value = "homologacaoCursoDAO")
	protected HomologacaoCursoDAO homologacaoCursoDAO;
	
	@Override
	public List<HomologacaoCurso> listarHomologacaoCursos(Curso curso) {
		return homologacaoCursoDAO.listarHomologacaoCursos(curso);
	}

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	public HomologacaoCursoDAO getHomologacaoCursoDAO() {
		return homologacaoCursoDAO;
	}

	public void setHomologacaoCursoDAO(HomologacaoCursoDAO homologacaoCursoDAO) {
		this.homologacaoCursoDAO = homologacaoCursoDAO;
	}
}

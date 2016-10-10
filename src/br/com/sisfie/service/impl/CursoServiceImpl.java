package br.com.sisfie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.CursoDAO;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.EmailCursoPrivado;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.InscricaoCursoCertificado;
import br.com.sisfie.entidade.StatusCurso;
import br.com.sisfie.entidade.StatusInscricao;
import br.com.sisfie.service.CursoService;

@Service(value = "cursoService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class CursoServiceImpl implements CursoService {
	
	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;
	
	@Autowired(required = true)
	@Qualifier(value = "cursoDAO")
	protected CursoDAO cursoDAO;

	@Override
	public List<Curso> listarCursosDisponiveis() throws Exception {
		return cursoDAO.listarCursoDisponiveis();
	}
	
	@Override
	public StatusInscricao ultimoStatusInscricao(InscricaoCurso inscricaoCurso) {
		return cursoDAO.ultimoStatusInscricao(inscricaoCurso);
	}
	
	@Override
	public StatusCurso ultimoStatusCurso(Curso curso) throws Exception {
		return cursoDAO.ultimoStatusCurso(curso);
	}
	
	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}


	public CursoDAO getCursoDAO() {
		return cursoDAO;
	}

	public void setCursoDAO(CursoDAO cursoDAO) {
		this.cursoDAO = cursoDAO;
	}

	@Override
	public List<Curso> carregarOutrosCursos(Curso curso) throws Exception {
		return cursoDAO.carregarOutrosCursos(curso);
	}

	@Override
	public Integer countInscritosCancelados(Integer idCurso) {
		return cursoDAO.countInscritosCancelados(idCurso);
	}

	@Override
	public List<EmailCursoPrivado> listarCursosDisponiveisParceirosOuInstrutores(String emailParceiro) throws Exception {
		return cursoDAO.listarCursosDisponiveisParceirosOuInstrutores(emailParceiro);
	}

	@Override
	public InscricaoCursoCertificado carregaInscricaoCursoCertPorIdInscricao(
			Integer idInscricaoCurso) throws Exception {
		
		return cursoDAO.carregaInscricaoCursoCertPorIdInscricao(idInscricaoCurso);
	}

	

	@Override
	public Curso carregaCursoById(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return (Curso) dao.get(Curso.class, id);
	}

	
}
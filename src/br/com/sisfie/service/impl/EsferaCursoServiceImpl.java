package br.com.sisfie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.EsferaCurso;
import br.com.sisfie.entidade.EsferaGoverno;
import br.com.sisfie.service.EsferaCursoService;

@Service(value = "esferaCursoService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class EsferaCursoServiceImpl implements EsferaCursoService {
	
	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<EsferaCurso> listarEsferaCursos(Integer idCursoSelecionado) throws Exception {
		EsferaCurso esferaCurso = new EsferaCurso();
		esferaCurso.setCurso(new Curso(idCursoSelecionado));
		EsferaGoverno esferaGoverno = new EsferaGoverno();
		esferaGoverno.setFlgAtivo(Boolean.TRUE);
		esferaCurso.setEsferaGoverno(esferaGoverno);
		return dao.listBy(esferaCurso);
	}
	
	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

}

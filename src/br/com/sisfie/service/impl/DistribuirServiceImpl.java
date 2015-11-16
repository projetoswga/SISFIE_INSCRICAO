package br.com.sisfie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.DistribuirDAO;
import br.com.sisfie.entidade.DistribuicaoSofGrade;
import br.com.sisfie.service.DistribuirService;

@Service(value = "distribuirService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class DistribuirServiceImpl implements DistribuirService {

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@Autowired(required = true)
	@Qualifier(value = "distribuirDAO")
	protected DistribuirDAO distribuirDAO;

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	public DistribuirDAO getDistribuirDAO() {
		return distribuirDAO;
	}

	public void setDistribuirDAO(DistribuirDAO distribuirDAO) {
		this.distribuirDAO = distribuirDAO;
	}

	@Override
	public List<DistribuicaoSofGrade> listarDistribuicao(List<Integer> idsSelecaoOficinas) {
		return distribuirDAO.listarDistribuicao(idsSelecaoOficinas);
	}

}

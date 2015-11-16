package br.com.sisfie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.SelecaoPacoteDAO;
import br.com.sisfie.entidade.SelecaoOficina;
import br.com.sisfie.entidade.SelecaoPacote;
import br.com.sisfie.service.SelecaoPacoteService;

@Service(value = "selecaoPacoteService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class SelecaoPacoteServiceImpl implements SelecaoPacoteService {

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@Autowired(required = true)
	@Qualifier(value = "selecaoPacoteDAO")
	protected SelecaoPacoteDAO selecaoPacoteDAO;

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	public SelecaoPacoteDAO getSelecaoPacoteDAO() {
		return selecaoPacoteDAO;
	}

	public void setSelecaoPacoteDAO(SelecaoPacoteDAO selecaoPacoteDAO) {
		this.selecaoPacoteDAO = selecaoPacoteDAO;
	}

	@Override
	public List<SelecaoPacote> listarSelecaoPacote(Integer idInscricaoCurso) {
		return selecaoPacoteDAO.listarSelecaoPacote(idInscricaoCurso);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void excluirSelecaoPacote(Integer idSelecaoPacote) throws Exception {
		dao.remove(SelecaoPacote.class, idSelecaoPacote);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void excluirSelecaoPacotes(List<SelecaoPacote> listaSelecaoPacote) throws Exception {
		for (SelecaoPacote selecaoPacote : listaSelecaoPacote) {
			selecaoPacote = (SelecaoPacote) dao.get(SelecaoPacote.class, selecaoPacote.getId());
			for (SelecaoOficina selecaoOficina : selecaoPacote.getInscricaoCurso().getSelecaoOficinas()) {
				dao.remove(SelecaoOficina.class, selecaoOficina.getId());
			}
			excluirSelecaoPacote(selecaoPacote.getId());
		}
	}
}

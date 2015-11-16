package br.com.sisfie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.SelecaoOficinaDAO;
import br.com.sisfie.entidade.Oficina;
import br.com.sisfie.entidade.SelecaoOficina;
import br.com.sisfie.service.SelecaoOficinaService;

@Service(value = "selecaoOficinaService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class SelecaoOficinaServiceImpl implements SelecaoOficinaService {

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@Autowired(required = true)
	@Qualifier(value = "selecaoOficinaDAO")
	protected SelecaoOficinaDAO selecaoOficinaDAO;

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	public SelecaoOficinaDAO getSelecaoOficinaDAO() {
		return selecaoOficinaDAO;
	}

	public void setSelecaoOficinaDAO(SelecaoOficinaDAO selecaoOficinaDAO) {
		this.selecaoOficinaDAO = selecaoOficinaDAO;
	}

	@Override
	public SelecaoOficina recuperarSelecaoOficina(Integer idInscricaoCurso, Integer idOficina) {
		return selecaoOficinaDAO.recuperarSelecaoOficina(idInscricaoCurso, idOficina);
	}

	@Override
	public List<SelecaoOficina> listarSelecaoOficina(Integer idInscricaoCurso) {
		return selecaoOficinaDAO.listarSelecaoOficina(idInscricaoCurso);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void salvarSelecaoOficina(Oficina oficina) throws Exception {
		dao.save(oficina);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void excluirSelecaoOficinas(List<SelecaoOficina> listaSelecaoOficinas) throws Exception {
		if (listaSelecaoOficinas != null && !listaSelecaoOficinas.isEmpty()) {
			for (SelecaoOficina selecaoOficina : listaSelecaoOficinas) {
				dao.remove(SelecaoOficina.class, selecaoOficina.getId());
			}
		}
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void salvarSelecaoOficina(List<SelecaoOficina> selecaoOficinas, List<SelecaoOficina> listaRemocao) throws Exception {
		if (selecaoOficinas != null && !selecaoOficinas.isEmpty()) {
			for (SelecaoOficina selecaoOficina : selecaoOficinas) {
				dao.save(selecaoOficina);
			}
		}

		if (listaRemocao != null && !listaRemocao.isEmpty()) {
			for (SelecaoOficina selecaoOficina : listaRemocao) {
				dao.remove(SelecaoOficina.class, selecaoOficina.getId());
			}
		}
	}
}

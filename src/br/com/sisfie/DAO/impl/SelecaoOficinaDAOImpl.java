package br.com.sisfie.DAO.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.SelecaoOficinaDAO;
import br.com.sisfie.entidade.SelecaoOficina;

@Repository(value = "selecaoOficinaDAO")
public class SelecaoOficinaDAOImpl extends HibernateDaoSupport implements SelecaoOficinaDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@Override
	public SelecaoOficina recuperarSelecaoOficina(Integer idInscricaoCurso, Integer idOficina) {
		Criteria criteria = getSession().createCriteria(SelecaoOficina.class);
		criteria.add(Restrictions.eq("inscricaoCurso.id", idInscricaoCurso));
		criteria.add(Restrictions.eq("oficina.id", idOficina));
		return (SelecaoOficina) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SelecaoOficina> listarSelecaoOficina(Integer idInscricaoCurso) {
		Criteria criteria = getSession().createCriteria(SelecaoOficina.class);
		criteria.add(Restrictions.eq("inscricaoCurso.id", idInscricaoCurso));
		return criteria.list();
	}
}

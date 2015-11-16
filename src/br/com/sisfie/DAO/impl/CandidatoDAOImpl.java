package br.com.sisfie.DAO.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.CandidatoDAO;
import br.com.sisfie.entidade.Candidato;

@Repository(value = "candidatoDAO")
public class CandidatoDAOImpl extends HibernateDaoSupport implements CandidatoDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@Override
	public Candidato buscarCandidato(String login) throws Exception {
		String sql = "from Candidato c where c.emailInstitucional like'" + login + "'";
		return (Candidato) dao.getCurrentSession().createQuery(sql).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Integer recuperarParceiro(String email) {
		Criteria criteria = getSession().createCriteria(Candidato.class);
		criteria.add(Restrictions.ilike("emailInstitucional", email));
		criteria.setProjection(Projections.distinct(Projections.property("id")));
		List<Integer> ids = criteria.list();
		if (ids != null && !ids.isEmpty()){
			return ids.get(0);
		}
		return null;
	}

}

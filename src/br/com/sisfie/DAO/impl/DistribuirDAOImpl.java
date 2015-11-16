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
import br.com.sisfie.DAO.DistribuirDAO;
import br.com.sisfie.entidade.DistribuicaoSofGrade;

@Repository(value = "distribuirDAO")
public class DistribuirDAOImpl extends HibernateDaoSupport implements DistribuirDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<DistribuicaoSofGrade> listarDistribuicao(List<Integer> idsSelecaoOficinas) {
		Criteria criteria = getSession().createCriteria(DistribuicaoSofGrade.class);
		criteria.createAlias("selecaoOficina", "so");
		criteria.add(Restrictions.in("so.id", idsSelecaoOficinas));
		return criteria.list();
	}
}

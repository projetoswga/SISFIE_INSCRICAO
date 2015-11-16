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
import br.com.sisfie.DAO.HomologacaoCursoDAO;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.HomologacaoCurso;

@Repository(value = "homologacaoCursoDAO")
public class HomologacaoCursoDAOImpl extends HibernateDaoSupport implements HomologacaoCursoDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<HomologacaoCurso> listarHomologacaoCursos(Curso curso) {
		Criteria criteria = getSession().createCriteria(HomologacaoCurso.class);
		criteria.add(Restrictions.eq("curso.id", curso.getId()));
		return criteria.list();
	}
}

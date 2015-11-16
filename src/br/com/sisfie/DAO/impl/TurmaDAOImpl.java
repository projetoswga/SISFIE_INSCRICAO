package br.com.sisfie.DAO.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.TurmaDAO;
import br.com.sisfie.entidade.GradeOficina;
import br.com.sisfie.entidade.Turma;

@Repository(value = "turmaDAO")
public class TurmaDAOImpl extends HibernateDaoSupport implements TurmaDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Turma> recuperarTurmasDaGradeOficina(Integer idOficina, Integer idHorario, Integer idProfessor) {
		Criteria criteria = getSession().createCriteria(GradeOficina.class);
		criteria.add(Restrictions.eq("oficina.id", idOficina));
		criteria.add(Restrictions.eq("horario.id", idHorario));
		criteria.add(Restrictions.eq("professorEvento.id", idProfessor));
		List<GradeOficina> listaGradeOficinas = criteria.list();
		if (listaGradeOficinas != null && !listaGradeOficinas.isEmpty()){
			List<Turma> turmas = new ArrayList<Turma>();
			for (GradeOficina gradeOficina : listaGradeOficinas) {
				turmas.add(gradeOficina.getTurma());
			}
			return turmas;
		}
		return null;
	}
}

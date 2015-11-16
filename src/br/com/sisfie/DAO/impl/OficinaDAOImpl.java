package br.com.sisfie.DAO.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.OficinaDAO;
import br.com.sisfie.entidade.GradeOficina;
import br.com.sisfie.entidade.InscricaoGrade;
import br.com.sisfie.entidade.Oficina;
import br.com.sisfie.entidade.PacoteOficina;
import br.com.sisfie.entidade.SelecaoOficina;
import br.com.sisfie.entidade.Status;

@Repository(value = "oficinaDAO")
public class OficinaDAOImpl extends HibernateDaoSupport implements OficinaDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Oficina> recuperarOficinasDisponiveis(Integer idCurso) {
		Criteria criteria = getSession().createCriteria(Oficina.class);
		if (idCurso != null && !idCurso.equals(0)) {
			criteria.createAlias("curso", "curso");
			criteria.add(Restrictions.eq("curso.id", idCurso));
			criteria.addOrder(Order.asc("nomOficina"));
		}

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Oficina> recuperarOficina(Integer idCurso, Integer idPacote) {
		Criteria criteria = getSession().createCriteria(Oficina.class);

		DetachedCriteria subCriteria = DetachedCriteria.forClass(PacoteOficina.class);
		subCriteria.createAlias("pacote", "p");
		subCriteria.add(Property.forName("p.id").eq(idPacote));
		subCriteria.createAlias("p.curso", "c");
		subCriteria.add(Property.forName("c.id").eq(idCurso));
		subCriteria.setProjection(Projections.distinct(Projections.property("oficina.id")));

		criteria.add(Property.forName("id").in(subCriteria));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Oficina> listarOficinas(Integer idInscricaoCurso) {
		Criteria criteria = getSession().createCriteria(Oficina.class);
		DetachedCriteria subCriteria = DetachedCriteria.forClass(SelecaoOficina.class);
		subCriteria.add(Property.forName("inscricaoCurso.id").eq(idInscricaoCurso));
		subCriteria.setProjection(Projections.distinct(Projections.property("oficina.id")));

		criteria.add(Property.forName("id").in(subCriteria));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> recuperarIdsOficinasDisponiveis(Integer idCursoSelecionado) {
		Criteria criteria = getSession().createCriteria(Oficina.class);
		if (idCursoSelecionado != null && !idCursoSelecionado.equals(0)) {
			criteria.createAlias("curso", "curso");
			criteria.add(Restrictions.eq("curso.id", idCursoSelecionado));
			criteria.setProjection(Projections.distinct(Projections.property("id")));
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GradeOficina> recuperarGradeOficinaPorHorario(List<Integer> listaOficinasDisponiveis, List<Integer> idsOficinasSelecionadas, Integer idHorario) {
		Criteria criteria = getSession().createCriteria(GradeOficina.class);
		if (idsOficinasSelecionadas != null && !idsOficinasSelecionadas.isEmpty()){
			criteria.add(Restrictions.not(Restrictions.in("oficina.id", idsOficinasSelecionadas)));
		}
		criteria.add(Restrictions.in("oficina.id", listaOficinasDisponiveis));
		criteria.add(Restrictions.eq("horario.id", idHorario));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GradeOficina> recuperarGradeOficinasVinculadas(Integer idHorario, Integer idOficina, Integer idTurma) {
		Criteria criteria = getSession().createCriteria(GradeOficina.class);
		criteria.add(Restrictions.not(Restrictions.eq("horario.id", idHorario)));
		criteria.add(Restrictions.eq("oficina.id", idOficina));
		criteria.add(Restrictions.eq("turma.id", idTurma));
		return criteria.list();
	}

	@Override
	public Long countInscritosPorGradeOficina(Integer idCursoSelecionado, Integer idGradeOficina, List<Integer> idsParceiros) {
		
		List<Integer> idsStatus = new ArrayList<Integer>();
		idsStatus.add(Status.CANCELADO);
		idsStatus.add(Status.AGUARDANDO_VAGA_PRIORIDADE);
		idsStatus.add(Status.AGUARDANDO_VAGA);
		
		Criteria criteria = getSession().createCriteria(InscricaoGrade.class);
		criteria.createAlias("inscricaoCurso", "i");
		criteria.createAlias("i.curso", "c");
		criteria.add(Restrictions.eq("c.id", idCursoSelecionado));
		criteria.createAlias("i.ultimoStatus", "us");
		criteria.createAlias("us.status", "s");
		criteria.add(Restrictions.not(Restrictions.in("s.id", idsStatus)));
		criteria.add(Restrictions.eq("gradeOficina.id", idGradeOficina));
		if (idsParceiros != null && !idsParceiros.isEmpty()){
			criteria.createAlias("i.candidato", "cand");
			criteria.add(Restrictions.not(Restrictions.in("cand.id", idsParceiros)));
		}
		criteria.setProjection(Projections.rowCount());
		Long result = (Long) criteria.list().get(0);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InscricaoGrade> recuperarInscricaoGrade(Integer idInscricao) {
		Criteria criteria = getSession().createCriteria(InscricaoGrade.class);
		criteria.add(Restrictions.eq("inscricaoCurso.id", idInscricao));
		return criteria.list();
	}

	@Override
	public Long countParceirosInscritos(Integer idCursoSelecionado, Integer idGradeOficina, List<Integer> idsParceiros) {
		
		List<Integer> idsStatus = new ArrayList<Integer>();
		idsStatus.add(Status.CANCELADO);
		idsStatus.add(Status.AGUARDANDO_VAGA_PRIORIDADE);
		idsStatus.add(Status.AGUARDANDO_VAGA);
		
		Criteria criteria = getSession().createCriteria(InscricaoGrade.class);
		criteria.createAlias("inscricaoCurso", "ic");
		criteria.createAlias("ic.curso", "c");
		criteria.add(Restrictions.eq("c.id", idCursoSelecionado));
		criteria.createAlias("ic.ultimoStatus", "us");
		criteria.createAlias("us.status", "s");
		criteria.add(Restrictions.not(Restrictions.in("s.id", idsStatus)));
		criteria.createAlias("gradeOficina", "go");
		criteria.add(Restrictions.eq("go.id", idGradeOficina));
		if (idsParceiros != null && !idsParceiros.isEmpty()){
			criteria.createAlias("ic.candidato", "cand");
			criteria.add(Restrictions.in("cand.id", idsParceiros));
		}
		criteria.setProjection(Projections.rowCount());
		Long result = (Long) criteria.list().get(0);
		return result;
	}
}

package br.com.sisfie.DAO.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.arquitetura.util.DateUtil;
import br.com.sisfie.DAO.CursoDAO;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.EmailCursoPrivado;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.Status;
import br.com.sisfie.entidade.StatusCurso;
import br.com.sisfie.entidade.StatusInscricao;

@Repository(value = "cursoDAO")
public class CursoDAOImpl extends HibernateDaoSupport implements CursoDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	/**
	 * Pesquisa os cursos onde a data atual esteja entre dtInicio e dtFim.<br/>
	 * Se for for dtFim determinada pelo gestor pesquisa onde data de hoje seja maior ou igual a dtInicio e o STATUS NÂO esteja como
	 * Finalizado inscrição ou curso.
	 * 
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Curso> listarCursoDisponiveis() throws Exception {

		List<Curso> cursos = new ArrayList<Curso>();

		Criteria c1 = getSession().createCriteria(Curso.class);

		// Onde estiver entre as datas
		c1.add(Restrictions.and(Restrictions.le("dtInicioInscricao", DateUtil.getDateSemHora(new Date())),
				Restrictions.ge("dtTerminoInscricao", DateUtil.getDateSemHora(new Date()))));
		// Onde não for por data determinada pelo gestor
		c1.add(Restrictions.eq("flgDataAtivo", true));
		// onde o curso não tiver finalizado
		c1.add(Restrictions.or(Restrictions.isNull("flgFinalizado"), Restrictions.eq("flgFinalizado", false)));
		List<Curso> cursosPorData = c1.list();

		Criteria c2 = getSession().createCriteria(Curso.class);
		// Onde a data de hoje maior ou igual a dtInicio
		c2.add(Restrictions.le("dtInicioInscricao", DateUtil.getDateSemHora(new Date())));
		// No caso da data de termino da inscrição for determinada pelo gestor
		c2.add(Restrictions.gt("dtRealizacaoInicio", DateUtil.getDateSemHora(new Date())));
		// Onde for por data determinada pelo gestor
		c2.add(Restrictions.eq("flgDataAtivo", false));
		// onde o curso não tiver finalizado
		c2.add(Restrictions.or(Restrictions.isNull("flgFinalizado"), Restrictions.eq("flgFinalizado", false)));
		List<Curso> cursosAux = c2.list();
		List<Curso> cursosPorDetGestor = new ArrayList<Curso>();

		// Verifica se o ultimo status não é inscrições finalizadas
		if (cursosAux != null && !cursosAux.isEmpty()) {
			for (Curso curso : cursosAux) {
				Criteria cStatus = getSession().createCriteria(StatusCurso.class);
				cStatus.createAlias("curso", "c");
				cStatus.add(Restrictions.eq("c.id", curso.getId()));
				cStatus.setProjection(Projections.max("id"));
				Integer idStatus = (Integer) cStatus.uniqueResult();
				if (idStatus != null) {
					StatusCurso statusCurso = (StatusCurso) getSession().get(StatusCurso.class, idStatus);
					if (!statusCurso.getStatus().getId().equals(Status.FINALIZADA_INSCRICAO)
							&& !statusCurso.getStatus().getId().equals(Status.FINALIZADO_CURSO)) {
						cursosPorDetGestor.add(curso);
					}
				}

			}

		}

		// Soma a duas listas e retonar.
		if (cursosPorData != null && !cursosPorData.isEmpty()) {
			cursos.addAll(cursosPorData);
		}
		if (cursosPorDetGestor != null && !cursosPorDetGestor.isEmpty()) {
			cursos.addAll(cursosPorDetGestor);
		}

		return cursos;
	}

	@Override
	public StatusInscricao ultimoStatusInscricao(InscricaoCurso inscricaoCurso) {
		// Pega ultimo Status
		Criteria cStatus = getSession().createCriteria(StatusInscricao.class);

		cStatus.createAlias("inscricaoCurso", "i");
		cStatus.add(Restrictions.eq("i.id", inscricaoCurso.getId()));
		cStatus.setProjection(Projections.max("id"));

		Integer id = (Integer) cStatus.uniqueResult();

		if (id != null) {
			return (StatusInscricao) getSession().get(StatusInscricao.class, id);
		} else {
			return null;
		}
	}

	@Override
	public StatusCurso ultimoStatusCurso(Curso curso) {
		// Pega ultimo Status
		Criteria cStatus = getSession().createCriteria(StatusCurso.class);

		cStatus.createAlias("curso", "c");
		cStatus.add(Restrictions.eq("c.id", curso.getId()));
		cStatus.setProjection(Projections.max("id"));

		Integer id = (Integer) cStatus.uniqueResult();

		if (id != null) {
			return (StatusCurso) getSession().get(StatusCurso.class, id);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Curso> carregarOutrosCursos(Curso curso) throws Exception {
		// Pega ultimo Status
		Criteria c = getSession().createCriteria(Curso.class);

		c.add(Restrictions.ilike("codigo", curso.getCodigo(), MatchMode.EXACT));
		c.add(Restrictions.not(Restrictions.eq("id", curso.getId())));

		List<Curso> listaCursos = c.list();
		for (Curso curso2 : listaCursos) {
			Hibernate.initialize(curso2.getLocalizacao());
		}

		return listaCursos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer countInscritosCancelados(Integer idCurso) {
		Criteria c = getSession().createCriteria(InscricaoCurso.class);
		c.add(Restrictions.eq("flgInstrutor", false));
		c.createAlias("curso", "c");
		c.add(Restrictions.eq("c.id", idCurso));
		c.createAlias("statusInscricoes", "st");
		c.createAlias("st.status", "status");
		c.add(Restrictions.eq("status.id", Status.CANCELADO));
		c.setProjection(Projections.distinct(Projections.property("id")));
		List<Integer> ids = c.list();

		if (ids != null && !ids.isEmpty()) {
			Criteria c2 = getSession().createCriteria(InscricaoCurso.class);
			c2.add(Restrictions.in("id", ids));
			List<InscricaoCurso> lista = c2.list();
			Set<Integer> qtd = new HashSet<Integer>();

			if (lista != null && !lista.isEmpty()) {
				for (InscricaoCurso obj : lista) {
					obj.setStatusUltimo(ultimoStatusInscricao(obj).getStatus());
					if (obj.getStatusUltimo().getId().equals(Status.CANCELADO)) {
						qtd.add(obj.getId());
					}
				}
				return qtd.size();
			}
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EmailCursoPrivado> listarCursosDisponiveisParceiros(String emailParceiro) throws Exception {
		Criteria criteria = getSession().createCriteria(EmailCursoPrivado.class);
		criteria.createAlias("curso", "c");
		criteria.add(Restrictions.lt("c.dtTerminoInscricao", DateUtil.getDateSemHora(new Date())));
		criteria.add(Restrictions.gt("c.dtRealizacaoInicio", DateUtil.getDateSemHora(new Date())));
		criteria.add(Restrictions.or(Restrictions.isNull("c.flgFinalizado"), Restrictions.eq("c.flgFinalizado", false)));
		criteria.add(Restrictions.ilike("email", emailParceiro));
		return criteria.list();
	}
}

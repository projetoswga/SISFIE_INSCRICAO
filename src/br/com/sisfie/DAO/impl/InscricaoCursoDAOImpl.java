package br.com.sisfie.DAO.impl;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.InscricaoCursoDAO;
import br.com.sisfie.entidade.Atuacao;
import br.com.sisfie.entidade.Cargo;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.Status;
import br.com.sisfie.entidade.StatusInscricao;

@Repository(value = "inscricaoCursoDAO")
public class InscricaoCursoDAOImpl extends HibernateDaoSupport implements InscricaoCursoDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Atuacao> listarAreaAtuacoes(List<Integer> idsAtuacoes) {
		Criteria criteria = getSession().createCriteria(Atuacao.class);
		criteria.add(Restrictions.eq("flgAtivo", true));
		if (idsAtuacoes != null && !idsAtuacoes.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in("id", idsAtuacoes)));
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cargo> listarCargos(List<Integer> idsCargos) {
		Criteria criteria = getSession().createCriteria(Cargo.class);
		criteria.add(Restrictions.eq("flgAtivo", true));
		if (idsCargos != null && !idsCargos.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in("id", idsCargos)));
		}
		return criteria.list();
	}

	@Override
	public Integer retornarQuantidadeInscritos(Integer idCurso) {
		
		StringBuffer idsStatus = new StringBuffer();
		idsStatus.append(Status.CANCELADO);
		idsStatus.append(",");
		idsStatus.append(Status.AGUARDANDO_VAGA);
		idsStatus.append(",");
		idsStatus.append(Status.AGUARDANDO_VAGA_PRIORIDADE);
		
		StringBuffer query = new StringBuffer();
		query.append(" select count(ic.id_inscricao_curso) from inscricao_curso ic ");
		query.append(" inner join status_inscricao si on si.id_inscricao_curso = ic.id_inscricao_curso ");
		query.append(" inner join curso c on ic.id_curso = c.id_curso ");
		query.append(" inner join status_d s on si.id_status = s.id_status ");
		query.append(" where c.id_curso = " + idCurso);
		query.append(" and s.id_status not in( " + idsStatus.toString() + " ) ");
		query.append(" and si.id_status_inscricao in (select max(sti.id_status_inscricao) from status_inscricao sti where sti.id_inscricao_curso = ic.id_inscricao_curso) ");
		
		return ((BigInteger) getSession().createSQLQuery(query.toString()).uniqueResult()).intValue();
		
//		List<Integer> idsStatus = new ArrayList<Integer>();
//		idsStatus.add(Status.CANCELADO);
//		idsStatus.add(Status.AGUARDANDO_VAGA_PRIORIDADE);
//		idsStatus.add(Status.AGUARDANDO_VAGA);
//
//		Criteria c = getSession().createCriteria(InscricaoCurso.class);
//		c.createAlias("curso", "c");
//		c.add(Restrictions.eq("c.id", idCurso));
//		c.createAlias("statusInscricoes", "st");
//		c.createAlias("st.status", "status");
//		c.add(Restrictions.not(Restrictions.in("status.id", idsStatus)));
//		c.setProjection(Projections.distinct(Projections.property("id")));
//		List<Integer> ids = c.list();
//
//		if (ids != null && !ids.isEmpty()) {
//			Criteria c2 = getSession().createCriteria(InscricaoCurso.class);
//			c2.add(Restrictions.in("id", ids));
//			List<InscricaoCurso> lista = c2.list();
//			Set<Integer> qtd = new HashSet<Integer>();
//
//			if (lista != null && !lista.isEmpty()) {
//				for (InscricaoCurso obj : lista) {
//					obj.setStatusUltimo(ultimoStatusInscricao(obj).getStatus());
//					if (!obj.getStatusUltimo().getId().equals(Status.CANCELADO)
//							&& !obj.getStatusUltimo().getId().equals(Status.AGUARDANDO_VAGA)
//							&& !obj.getStatusUltimo().getId().equals(Status.AGUARDANDO_VAGA_PRIORIDADE)) {
//						qtd.add(obj.getId());
//					}
//				}
//				return qtd.size();
//			}
//		}
//
//		return 0;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InscricaoCurso> paginateInscricoes(int first, int pageSize, InscricaoCurso model) {
		Criteria c = retornarCriteria(model);
		if (first != 0)
			c.setFirstResult(first);
		if (pageSize != 0)
			c.setMaxResults(pageSize);
		return c.list();
	}

	private Criteria retornarCriteria(InscricaoCurso model) {
		if (model == null) {
			return null;
		}
		Criteria criteria = getSession().createCriteria(InscricaoCurso.class);
		criteria.add(Restrictions.eq("candidato.id", model.getCandidato().getId()));
		return criteria;
	}

	@Override
	public Long countInscricoes(InscricaoCurso model) {
		Criteria c = retornarCriteria(model);
		c.setProjection(Projections.rowCount());
		Long result = (Long) c.list().get(0);
		return result;
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
			StatusInscricao statusInscricao = (StatusInscricao) getSession().get(StatusInscricao.class, id);
			Hibernate.initialize(statusInscricao.getStatus());
			Hibernate.initialize(statusInscricao.getInscricaoCurso());
			return statusInscricao;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InscricaoComprovante> carregarComprovantes(InscricaoCurso inscricaoCursoSelecionado) throws Exception {
		Criteria c = getSession().createCriteria(InscricaoComprovante.class);

		c.createAlias("inscricaoCurso", "c");
		c.add(Restrictions.eq("c.id", inscricaoCursoSelecionado.getId()));

		c.addOrder(Order.desc("dtCadastro"));

		return c.list();
	}

	@Override
	public InscricaoCurso recuperarInscricaoPeloHash(String hashCandidato) throws Exception {

		// Sql com hash para descobrir a inscricao do curso.
		StringBuilder sql = new StringBuilder();
		sql.append("select i.id_inscricao_curso from sisfie.candidato as c, sisfie.inscricao_curso i where ");
		sql.append(" md5(c.id_candidato || '_' || i.id_inscricao_curso) = '" + hashCandidato + "'");

		Integer idInscricao = (Integer) getSession().createSQLQuery(sql.toString()).uniqueResult();
		if (idInscricao != null) {
			InscricaoCurso inscricaoCurso = (InscricaoCurso) dao.get(InscricaoCurso.class, idInscricao);
			// verifica se a inscricao ta com o ultimo status pre inscrito ou aguardando vaga caso candidato não seja da mesma região do
			// curso.
			// Senão achar retorna null.
			StatusInscricao ultimoStatus = ultimoStatusInscricao(inscricaoCurso);
			if (ultimoStatus.getStatus().getId().equals(Status.PRE_INSCRITO)
					|| ultimoStatus.getStatus().getId().equals(Status.AGUARDANDO_VAGA)) {
				return inscricaoCurso;
			} else {
				return null;
			}

		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InscricaoDocumento> carregarDocumentacao(InscricaoCurso inscricaoCursoSelecionado) {
		Criteria criteria = getSession().createCriteria(InscricaoDocumento.class);
		criteria.add(Restrictions.eq("inscricaoCurso.id", inscricaoCursoSelecionado.getId()));
		return criteria.list();
	}

	@Override
	public int executarScript(String script) throws Exception {
		if (script.toLowerCase().trim().startsWith("select")){
			return getSession().createSQLQuery(script).list().size();
		}
		return getSession().createSQLQuery(script).executeUpdate();
	}
}

package br.com.sisfie.DAO.impl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import br.com.sisfie.DAO.ImagemDAO;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.ModeloDocumento;

@Repository(value = "ImagemDAO")
public class ImagemDAOImpl extends HibernateDaoSupport implements ImagemDAO {

	@Autowired(required = true)
	public void setFactory(SessionFactory factory) {
		super.setSessionFactory(factory);
	}

	@Override
	public InscricaoComprovante carregarImagemId(Integer id) throws Exception {
		Criteria c = getSession().createCriteria(InscricaoComprovante.class);
		c.add(Restrictions.eq("id", id));
		return (InscricaoComprovante) c.uniqueResult();
	}

	@Override
	public InscricaoDocumento carregarDocumentoId(Integer idDocumento) {
		Criteria c = getSession().createCriteria(InscricaoDocumento.class);
		c.add(Restrictions.eq("id", idDocumento));
		return (InscricaoDocumento) c.uniqueResult();
	}

	@Override
	public Curso carregarCursoId(Integer idCurso) {
		Criteria c = getSession().createCriteria(Curso.class);
		c.add(Restrictions.eq("id", idCurso));
		return (Curso) c.uniqueResult();
	}

	@Override
	public ModeloDocumento carregarCertificadoId(Integer idCertificado) {
		Criteria c = getSession().createCriteria(ModeloDocumento.class);
		c.add(Restrictions.eq("id", idCertificado));
		return (ModeloDocumento) c.uniqueResult();
	}
}

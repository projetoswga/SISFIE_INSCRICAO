package br.com.sisfie.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.sisfie.DAO.CandidatoDAO;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.service.LoginService;

@Service(value = "loginService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class LoginServiceImpl implements LoginService {

	@Autowired(required = true)
	@Qualifier(value = "mailSender")
	private JavaMailSender mailSender;

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;
	
	@Autowired(required = true)
	@Qualifier(value = "candidatoDAO")
	protected CandidatoDAO candidatoDAO;

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void esqueciSenha(Candidato user, SimpleMailMessage message) throws Exception {
		dao.save(user);
		mailSender.send(message);

	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	@Override
	public Candidato buscarCandidato(String login) throws Exception {
		return candidatoDAO.buscarCandidato(login);
	}
}

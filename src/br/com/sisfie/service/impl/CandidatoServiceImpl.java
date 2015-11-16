package br.com.sisfie.service.impl;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.arquitetura.util.ThreadEnviarEmail;
import br.com.sisfie.DAO.CandidatoDAO;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.entidade.Municipio;
import br.com.sisfie.entidade.Uf;
import br.com.sisfie.service.CandidatoService;
import br.com.sisfie.util.Constantes;

@Service(value = "candidatoService")
@Transactional(readOnly = true, rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
public class CandidatoServiceImpl implements CandidatoService {

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;
	
	@Autowired(required = true)
	@Qualifier(value = "candidatoDAO")
	protected CandidatoDAO candidatoDAO;

	@Autowired(required = true)
	@Qualifier(value = "mailSender")
	private JavaMailSender simpleMailSender;

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void save(Candidato candidato, String senhaGerada) throws Exception {
		dao.save(candidato);
		
		Municipio municipioEndereco = (Municipio) dao.get(Municipio.class, candidato.getMunicipioEndereco().getId());
		Uf ufEndereco = (Uf) dao.get(Uf.class, candidato.getMunicipioEndereco().getUf().getId());
		Municipio municipioOrgao = (Municipio) dao.get(Municipio.class, candidato.getMunicipioOrgao().getId());
		Uf ufOrgao = (Uf) dao.get(Uf.class, candidato.getMunicipioOrgao().getUf().getId());
		
		String documento = "";
		if (candidato.getNacionalidadeBrasil()){
			documento = "CPF: "+ camposEmail(candidato.getCpf())+"<br/>";
		}else {
			documento = "Documento Estrangeiro: "+ camposEmail(candidato.getDocEstrangeiro())+"<br/>";
		}

		// prepara conteudo do email
		StringBuilder textoEmail = new StringBuilder("Cadastro realizado com sucesso!");
		textoEmail.append("<br/>");
		textoEmail.append(" <table border='0' cellspacing='0' cellpadding='0' width='750'>" +
			"<tr>" +
				"<td>"+
					"<table border='0' cellspacing='0' cellpadding='0' align='left' width='85%'>"+
						"<tr>"+
							"<td width='100%'>" +
								"<p>Prezado(a) "+camposEmail(candidato.getNome())+ ", O seu cadastro foi realizado com sucesso. A sua senha de acesso &Eacute; <b>" +
										senhaGerada +"</b>. Seguem os seus dados cadastrais para confer&ecirc;ncia. Caso deseje alterar a sua senha ou dados cadastrais, " +
										"por favor clique em ALTERAR SENHA ou ALTERAR DADOS PESSOAIS no SISFIE - Sistema de Ficha Eletr&ocirc;nica da ESAF. " +
										
								"<p><b>DADOS PESSOAIS </b></p> " +
								"Nome Completo: "+camposEmail(candidato.getNome())+"<br/>"+
								"Login: "+ camposEmail(candidato.getEmailInstitucional())+ "<br/>"+
								"Matr&iacute;cula: "+ camposEmail(candidato.getMatricula())+"<br/>"+
								"Nacionalidade: "+ camposEmail(candidato.getCpf() != null && !candidato.getCpf().equals("")?"Brasileira":"Estrangeira")+"<br/>"+
								documento+
								"E-mail Pessoal: " + camposEmail(candidato.getEmailPessoal())+"<br/>"+
								"Logradouro: "+ camposEmail(candidato.getLogradouro())+"<br/>"+
								"Complemento:  "+ camposEmail(candidato.getComplemento())+"<br/>"+
								"UF: "+ camposEmail(ufEndereco.getSigla())+"<br/>"+
								"Munic&iacute;pio: "+ camposEmail(municipioEndereco.getDescricao())+"<br/>"+
								"CEP: "+ camposEmail(candidato.getCep())+
								
								"<p><b>DADOS INSTITUCIONAIS </b><p/>"+ 
								"&Oacute;rg&atilde;o: "+camposEmail(candidato.getOrgao().getNomeSiglaFormat())+"<br/>"+
								"UF: "+ camposEmail(ufOrgao.getSigla())+"<br/>"+
								"Munic&iacute;pio(&Oacute;rg&atilde;o): " + camposEmail(municipioOrgao.getDescricao())+"<br/>"+
								"E-mail Institucional: "+ camposEmail(candidato.getEmailInstitucional())+"<br/>"+
								"Matr&iacute;cula do Chefe: "+ camposEmail(candidato.getMatriculaChefe())+
								
								"<p><b>TELEFONES</b><p/>"+ 
								"Telefone trabalho: "+ camposEmail(candidato.getTelComercial())+"<br/>"+
								"Telefone Celular: "+  camposEmail(candidato.getTelCelular())+"<br/>"+
								"Telefone Resid&ecirc;ncia: "+ camposEmail(candidato.getTelResidencial())+
								
								"<p><b>CONTATO EM CASO DE EMERG&Ecirc;NCIA</b><p/>"+ 
								"Nome "+ candidato.getNomeEmergencia() +"<br/>"+
								"Telefone "+ candidato.getTelEmergencia() +
								
								"<p><b>Portador de Necessidades Especiais (PNE) - </b>"+camposEmail(candidato.getNecessidadeEspecialFormat())+"<p/>"+
							"</td>"+
						"</tr>"+
					"</table>"+
				"</td>"+
			"</tr>"+
		"</table> ");
		textoEmail.append("<br/>");
		textoEmail.append("<b>Login: </b>" + candidato.getEmailInstitucional());
		textoEmail.append("<br/>");
		textoEmail.append("<b>Senha: </b>" + senhaGerada);
		textoEmail.append("<br/>");
		textoEmail.append("<br/>");
		textoEmail.append("Mensagem Autom&aacute;tica - Não responder");
		textoEmail.append("<br/>");
		textoEmail.append("<br/>");

		String assunto = "Sistema SISFIE - SENHA DE ACESSO E CONFIRMAÇÃO DOS DADOS";

		// envia email
		Thread thread = new ThreadEnviarEmail(simpleMailSender, assunto, textoEmail.toString(), candidato.getEmailInstitucional(),
				Constantes.EMAIL_SISTEMA, true);
		thread.start();
	}

	private String camposEmail(String campo) throws UnsupportedEncodingException {
		if(campo==null){
			return "Sem informa&ccedil;&atilde;o";
		}
		return campo;
	}

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	public CandidatoDAO getCandidatoDAO() {
		return candidatoDAO;
	}

	public void setCandidatoDAO(CandidatoDAO candidatoDAO) {
		this.candidatoDAO = candidatoDAO;
	}
	
	@Override
	public Integer recuperarParceiro(String email) {
		return candidatoDAO.recuperarParceiro(email);
	}
}
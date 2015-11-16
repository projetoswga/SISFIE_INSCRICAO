package br.com.sisfie.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.hibernate.exception.DataException;

import br.com.arquitetura.util.DateUtil;
import br.com.arquitetura.util.FacesMessagesUtil;
import br.com.sisfie.service.InscricaoCursoService;

/**
 * Classe responsável por executar scritps. Fez-se necessário a criação dessa classe por não termos acesso a base de dados em produção no
 * SERPRO
 * 
 * @author Wesley Marra
 * 
 */

@ManagedBean(name = "executorScriptBean")
@ViewScoped
public class ExecutorScriptBean implements Serializable {

	@ManagedProperty(value = "#{inscricaoCursoService}")
	protected InscricaoCursoService inscricaoCursoService;

	@ManagedProperty(value = "#{login}")
	private LoginBean loginBean;

	private static final long serialVersionUID = -4077140607150738390L;
	private static final String SENHA_AUTORIZACAO = "sisfie123";
	private static final List<String> LISTA_USUARIOS_AUTORIZADOS = Collections.unmodifiableList(new ArrayList<String>() {
		private static final long serialVersionUID = -3017623137065511134L;
		{
			add("gustavoarr@gmail.com");
			add("andreazojs@gmail.com");
		}
	});

	private String senha;
	private String script;
	private String resultado;
	private boolean mostrarScript;

	public void mostrarCampoScript() {
		mostrarScript = false;
		if (senha == null || senha.equals("")) {
			FacesMessagesUtil.addErrorMessage("Erro", "É necessário informar uma senha!");
			return;
		}
		if (!LISTA_USUARIOS_AUTORIZADOS.contains(loginBean.getModel().getEmailInstitucional())) {
			FacesMessagesUtil.addErrorMessage("Erro", "Usuário não autorizado!");
			return;
		}
		if (!senha.equals(SENHA_AUTORIZACAO)) {
			FacesMessagesUtil.addErrorMessage("Erro", "Senha Inválida!");
			return;
		} else {
			mostrarScript = true;
		}
	}

	public void executar() {
		String dataHora = DateUtil.getDataHora(new Date(), "HH:ss:mm");
		try {
			resultado = null;
			if (script == null || script.equals("")) {
				FacesMessagesUtil.addErrorMessage("Erro", "É necessário informar um Script!");
				return;
			} else {
				Integer retorno = inscricaoCursoService.executarScript(script);
				if (script.toLowerCase().trim().startsWith("select")) {
					resultado = dataHora + " INFO: " + " Operação realizada com sucesso! \n  Retornou: " + retorno + " resultado(s).";
				} else {
					resultado = dataHora + " INFO: " + " Operação realizada com sucesso! \n  " + retorno + " registro(s) afetado(s).";
				}
			}
		} catch (DataException e) {
			StringBuilder erro = new StringBuilder(e.getMessage());
			erro.append(" \n  ");
			erro.append(e.getCause());
			resultado = dataHora + " " + erro.toString();
		} catch (Exception e) {
			resultado = dataHora + " " + e.getCause().getMessage();
		}
	}

	public InscricaoCursoService getInscricaoCursoService() {
		return inscricaoCursoService;
	}

	public void setInscricaoCursoService(InscricaoCursoService inscricaoCursoService) {
		this.inscricaoCursoService = inscricaoCursoService;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public boolean isMostrarScript() {
		return mostrarScript;
	}

	public void setMostrarScript(boolean mostrarScript) {
		this.mostrarScript = mostrarScript;
	}
}

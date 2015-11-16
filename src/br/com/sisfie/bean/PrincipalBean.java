package br.com.sisfie.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.com.arquitetura.bean.BaseBean;
import br.com.arquitetura.excecao.ExcecaoUtil;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.util.Constantes;

@ManagedBean(name = "principalBean")
@ViewScoped
public class PrincipalBean extends BaseBean<Candidato> {

	private static final long serialVersionUID = -7355462417028175561L;

	public String loginTimeOut() {
		try {
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.invalidate();
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("mostrarMsgSession", true);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/" + Constantes.CONTEXT_PATH + "/login.jsf");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}

		return "/pages/login.jsf?faces-redirect=true";

	}

	public void principalTemplate() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/" + Constantes.CONTEXT_PATH + "/pages/inscricaoCurso.jsf");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}

	}

	@Override
	public Candidato createModel() {
		return new Candidato();
	}

	@Override
	public String getQualifiedName() {
		return null;
	}

	@Override
	public boolean isFeminino() {
		return false;
	}
}

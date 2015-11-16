package br.com.sisfie.bean;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import br.com.arquitetura.bean.BaseBean;
import br.com.arquitetura.excecao.ExcecaoUtil;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.Status;
import br.com.sisfie.entidade.StatusInscricao;
import br.com.sisfie.service.InscricaoCursoService;

@ManagedBean(name = "confirmarInscricaoBean")
@ViewScoped
public class ConfirmarInscricaoBean extends BaseBean<InscricaoCurso> {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{inscricaoCursoService}")
	protected InscricaoCursoService inscricaoCursoService;

	private boolean mostrarErro;

	@PostConstruct
	public void carregarTela() {
		mostrarErro = false;
		try {
			String hashCandidato = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hash");
			InscricaoCurso inscricaoCurso = inscricaoCursoService.recuperarInscricaoPeloHash(hashCandidato);
			if (inscricaoCurso != null) {
				setModel(inscricaoCurso);
			} else {
				mostrarErro = true;
				return;
			}
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	public void confirmar() {
		try {

			StatusInscricao statusInscricao = new StatusInscricao(getModel(), getModel().getCurso().getUsuario(), new Status(
					Status.CONFIRMADO_PELO_CHEFE), new Date());

			universalManager.save(statusInscricao);
			getModel().setUltimoStatus(new StatusInscricao(statusInscricao.getId()));
			universalManager.save(getModel());

			FacesContext.getCurrentInstance().getExternalContext().redirect("informacao.jsf");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}

	}

	public void naoConfirmar() {
		try {
			StatusInscricao statusInscricao = new StatusInscricao(getModel(), getModel().getCurso().getUsuario(), new Status(
					Status.CANCELADO), new Date());

			universalManager.save(statusInscricao);
			getModel().setUltimoStatus(new StatusInscricao(statusInscricao.getId()));
			universalManager.save(getModel());

			FacesContext.getCurrentInstance().getExternalContext().redirect("informacao.jsf");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	@Override
	public InscricaoCurso createModel() {
		return new InscricaoCurso();
	}

	@Override
	public String getQualifiedName() {
		return "Confirmação";
	}

	@Override
	public boolean isFeminino() {
		return false;
	}

	public InscricaoCursoService getInscricaoCursoService() {
		return inscricaoCursoService;
	}

	public void setInscricaoCursoService(InscricaoCursoService inscricaoCursoService) {
		this.inscricaoCursoService = inscricaoCursoService;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isMostrarErro() {
		return mostrarErro;
	}

	public void setMostrarErro(boolean mostrarErro) {
		this.mostrarErro = mostrarErro;
	}

}

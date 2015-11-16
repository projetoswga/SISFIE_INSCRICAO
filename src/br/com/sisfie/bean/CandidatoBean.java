package br.com.sisfie.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;

import br.com.arquitetura.bean.BaseBean;
import br.com.arquitetura.excecao.ExcecaoUtil;
import br.com.arquitetura.util.FacesMessagesUtil;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.entidade.Municipio;
import br.com.sisfie.entidade.Orgao;
import br.com.sisfie.entidade.Uf;
import br.com.sisfie.service.CandidatoService;
import br.com.sisfie.service.OrgaoService;
import br.com.sisfie.util.Constantes;
import br.com.sisfie.util.CpfUtil;
import br.com.sisfie.util.CriptoUtil;
import br.com.sisfie.util.PasswordUtil;
import br.com.sisfie.util.ValidacaoUtil;

@ManagedBean(name = "candidatoBean")
@ViewScoped
public class CandidatoBean extends BaseBean<Candidato> {

	@ManagedProperty(value = "#{candidatoService}")
	protected CandidatoService candidatoService;

	@ManagedProperty(value = "#{OrgaoService}")
	protected OrgaoService orgaoService;

	private static final long serialVersionUID = -5411687049339168963L;
	private Orgao orgaoSelecionado;
	private List<Uf> UfOrgaos;
	private Integer idUfEnderecoSelecionado;
	private List<Uf> UfEnderecos;
	private Integer idUfOrgaoSelecionado;
	private List<Municipio> municipioOrgaos;
	private Integer idMunicipioSelecionado;
	private List<Municipio> municipioEnderecos;
	private Integer idMunicipioEnderecoSelecionado;

	private Date dataHj = new Date();
	private String confirmacaoEmail;

	/* Captcha */
	private Integer num1 = 0;
	private Integer num2 = 0;
	private Integer num3 = 0;
	private String resposta;
	private String pergunta;

	public CandidatoBean() {
		UfEnderecos = new ArrayList<Uf>();
		UfOrgaos = new ArrayList<Uf>();
		municipioEnderecos = new ArrayList<Municipio>();
		municipioOrgaos = new ArrayList<Municipio>();
		if (getModel().getId() == null){
			getModel().setNacionalidadeBrasil(true);
		}
		regenarCaptcha();
	}

	@SuppressWarnings("unchecked")
	public String validarCaptcha() {
		try {
			boolean captchaValido = false;
			if (getSessionMap().get("captchaValido") != null) {
				captchaValido = (Boolean) getSessionMap().get("captchaValido");
			}
			if (captchaValido) {
				getSessionMap().put("captchaValido", captchaValido);
				return redirect("primeiroAcesso.jsf");
			}
			
			Integer soma = num1 + num2 + num3;
			if(resposta == null || resposta.isEmpty()){
				FacesMessagesUtil.addErrorMessage(" ", " Resposta da equação está errada.");
				FacesContext.getCurrentInstance().getExternalContext().redirect("captcha.jsf");
				return "";
			}
			Integer respostaInt = new Integer(ValidacaoUtil.somenteNumero(resposta));
			if (soma != respostaInt.intValue()) {
				regenarCaptcha();
				FacesMessagesUtil.addErrorMessage(" ", " Resposta da equação está errada.");
				return "";
			} else {
				getSessionMap().put("captchaValido", true);
				return redirect("primeiroAcesso.jsf");
			}

		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
		return SUCCESS;
	}

	private void regenarCaptcha() {

		num1 = (int) (Math.random() * 10);
		num2 = (int) (Math.random() * 10);
		num3 = (int) (Math.random() * 10);

		while (num1 == num2 && num1 == num3 && num2 == num3) {
			num1 = (int) (Math.random() * 10);
			num2 = (int) (Math.random() * 10);
			num3 = (int) (Math.random() * 10);
		}

		pergunta = num1 + "+" + num2 + "+" + num3 + "=";
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void carregarTela() {
		try {

			if (UfOrgaos.isEmpty()) {
				UfOrgaos = universalManager.getAll(Uf.class);
				Collections.sort(UfOrgaos, new Comparator<Uf>() {
					@Override
					public int compare(Uf o1, Uf o2) {
						return o1.getSigla().trim().compareTo(o2.getSigla());
					}
				});
			}

			if (UfEnderecos.isEmpty()) {
				UfEnderecos = universalManager.getAll(Uf.class);
				Collections.sort(UfEnderecos, new Comparator<Uf>() {
					@Override
					public int compare(Uf o1, Uf o2) {
						return o1.getSigla().trim().compareTo(o2.getSigla());
					}
				});
			}
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	public void changeUF(AjaxBehaviorEvent evt) {
		carregarMunicipios(idUfOrgaoSelecionado, Boolean.FALSE);
	}

	public void changeUFEndereco(AjaxBehaviorEvent evt) {
		carregarMunicipios(idUfEnderecoSelecionado, Boolean.TRUE);
	}

	@SuppressWarnings("unchecked")
	private void carregarMunicipios(Integer idUf, Boolean isEndereco) {
		Municipio municipio = new Municipio();
		municipio.setUf(new Uf(idUf));
		try {
			if (isEndereco) {
				municipioEnderecos = universalManager.listBy(municipio);
			} else {
				municipioOrgaos = universalManager.listBy(municipio);
			}
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}

	}

	public void limparCampo() {
		if (getModel().getNacionalidadeBrasil()) {
			getModel().setDocEstrangeiro(null);
		} else {
			getModel().setCpf(null);
		}
	}

	@Override
	public String save() {
		try {
			// validar se existe (@ ou -) no nome completo
			if (getModel().getNome() != null && !getModel().getNome().isEmpty()) {
				for (int i = 0; i < getModel().getNome().length(); i++) {
					Character letra = getModel().getNome().charAt(i);
					switch (letra.charValue()) {
					case '@':
					case '-':
						FacesMessagesUtil.addErrorMessage("Caracteres inválidos!", "Não é permitido '@' nem '-' no mome completo.");
						return "";
					default:
						break;
					}
				}
			}
			if (orgaoSelecionado == null || orgaoSelecionado.getId() == null) {
				FacesMessagesUtil.addErrorMessage("Orgão ", Constantes.CAMPO_OBRIGATORIO);
				return ERROR;
			}
			// Validar email institucuional unique
			Candidato candidato = new Candidato();
			candidato.setEmailInstitucional(getModel().getEmailInstitucional().trim());
			List<Candidato> lista = universalManager.listBy(candidato, false);
			if (lista != null && !lista.isEmpty() && !lista.get(0).getId().equals(getModel().getId())) {
				FacesMessagesUtil.addErrorMessage("Email Institucional ", " Já cadastrado");
				return ERROR;
			}
			if (!getModel().getEmailInstitucional().trim().equals(confirmacaoEmail)) {
				FacesMessagesUtil.addErrorMessage("Confirmação do Email Institucional ", "Inválida");
				return ERROR;
			}

			// valida se tem cpf ou doc estrangeiro
			if (getModel().getNacionalidadeBrasil()) {
				if (getModel().getCpf() == null || getModel().getCpf().isEmpty()) {
					FacesMessagesUtil.addErrorMessage("CPF ", Constantes.CAMPO_OBRIGATORIO);
					return ERROR;
				}
				String cpf = getModel().getCpf().trim().replaceAll("[()-.]", "");
				// Validar CPF
				if (!CpfUtil.isValidCPF(cpf)) {
					FacesMessagesUtil.addErrorMessage("CPF ", "Inválido");
					return ERROR;
				}

				// Validar unique do CPF
				Candidato candidatoPesq = new Candidato();
				candidatoPesq.setCpf(cpf);
				List<Candidato> listaCandidato = universalManager.listBy(candidatoPesq, false);
				if (listaCandidato != null && !listaCandidato.isEmpty() && !listaCandidato.get(0).getId().equals(getModel().getId())) {
					FacesMessagesUtil.addErrorMessage("CPF ", " Já cadastrado");
					return ERROR;
				}

			} else {
				if (getModel().getDocEstrangeiro() == null || getModel().getDocEstrangeiro().isEmpty()) {
					FacesMessagesUtil.addErrorMessage("Documento Estrangeiro ", Constantes.CAMPO_OBRIGATORIO);
					return ERROR;
				}
			}

			// retira mascara
			if (getModel().getCep() != null && !getModel().getCep().trim().equals("")) {
				getModel().setCep(getModel().getCep().trim().replaceAll("[()-.]", ""));
			}
			// retira mascara
			if (getModel().getTelCelular() != null && !getModel().getTelCelular().trim().equals("")) {
				getModel().setTelCelular(getModel().getTelCelular().trim().replaceAll("[()-]", ""));
			}
			// retira mascara
			if (getModel().getTelComercial() != null && !getModel().getTelComercial().trim().equals("")) {
				getModel().setTelComercial(getModel().getTelComercial().trim().replaceAll("[()-]", ""));
			}
			// retira mascara
			if (getModel().getTelResidencial() != null && !getModel().getTelResidencial().trim().equals("")) {
				getModel().setTelResidencial(getModel().getTelResidencial().trim().replaceAll("[()-]", ""));
			}
			// retira mascara
			if (getModel().getCpf() != null && !getModel().getCpf().trim().equals("")) {
				getModel().setCpf(getModel().getCpf().trim().replaceAll("[()-.]", ""));
			}

			if (orgaoSelecionado != null && orgaoSelecionado.getId() == null) {
				getModel().setOrgao(null);
			} else {
				getModel().setOrgao(orgaoSelecionado);
			}

			getModel().setEmailInstitucional(getModel().getEmailInstitucional().trim());

			String senhaGerada = PasswordUtil.gerarPassword(6);
			getModel().setFlgPrimeiroAcesso(true);
			getModel().setSenha(CriptoUtil.getCriptografia(senhaGerada));
			getModel().setMunicipioEndereco(new Municipio(idMunicipioEnderecoSelecionado));
			getModel().getMunicipioEndereco().setUf(new Uf(idUfEnderecoSelecionado));
			getModel().setMunicipioOrgao(new Municipio(idMunicipioSelecionado));
			getModel().getMunicipioOrgao().setUf(new Uf(idUfOrgaoSelecionado));
			getModel().setFlgAtivo(Boolean.TRUE);
			candidatoService.save(getModel(), senhaGerada);
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("popAviso.show();");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
			return ERROR;
		}
		return SUCCESS;
	}

	public void limpar() {
		setModel(createModel());
		idMunicipioEnderecoSelecionado = null;
		idMunicipioSelecionado = null;
		orgaoSelecionado = null;
		idUfEnderecoSelecionado = null;
		idUfOrgaoSelecionado = null;
	}

	public List<Orgao> completeOrgao(String query) {
		List<Orgao> sugestoes = new ArrayList<Orgao>();
		try {
			sugestoes = orgaoService.pesquisarOrgao(query);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}

		return sugestoes;

	}

	public void habilitarCamposNecessidade() {

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Uf> getUfOrgaos() {
		return UfOrgaos;
	}

	public void setUfOrgaos(List<Uf> ufOrgaos) {
		UfOrgaos = ufOrgaos;
	}

	public Integer getIdUfEnderecoSelecionado() {
		return idUfEnderecoSelecionado;
	}

	public void setIdUfEnderecoSelecionado(Integer idUfEnderecoSelecionado) {
		this.idUfEnderecoSelecionado = idUfEnderecoSelecionado;
	}

	public List<Uf> getUfEnderecos() {
		return UfEnderecos;
	}

	public void setUfEnderecos(List<Uf> ufEnderecos) {
		UfEnderecos = ufEnderecos;
	}

	public Integer getIdUfOrgaoSelecionado() {
		return idUfOrgaoSelecionado;
	}

	public void setIdUfOrgaoSelecionado(Integer idUfOrgaoSelecionado) {
		this.idUfOrgaoSelecionado = idUfOrgaoSelecionado;
	}

	public List<Municipio> getMunicipioOrgaos() {
		return municipioOrgaos;
	}

	public void setMunicipioOrgaos(List<Municipio> municipioOrgaos) {
		this.municipioOrgaos = municipioOrgaos;
	}

	public Integer getIdMunicipioSelecionado() {
		return idMunicipioSelecionado;
	}

	public void setIdMunicipioSelecionado(Integer idMunicipioSelecionado) {
		this.idMunicipioSelecionado = idMunicipioSelecionado;
	}

	public List<Municipio> getMunicipioEnderecos() {
		return municipioEnderecos;
	}

	public void setMunicipioEnderecos(List<Municipio> municipioEnderecos) {
		this.municipioEnderecos = municipioEnderecos;
	}

	public Integer getIdMunicipioEnderecoSelecionado() {
		return idMunicipioEnderecoSelecionado;
	}

	public void setIdMunicipioEnderecoSelecionado(Integer idMunicipioEnderecoSelecionado) {
		this.idMunicipioEnderecoSelecionado = idMunicipioEnderecoSelecionado;
	}

	public CandidatoService getCandidatoService() {
		return candidatoService;
	}

	public void setCandidatoService(CandidatoService candidatoService) {
		this.candidatoService = candidatoService;
	}

	public Date getDataHj() {
		return dataHj;
	}

	public void setDataHj(Date dataHj) {
		this.dataHj = dataHj;
	}

	public String getConfirmacaoEmail() {
		return confirmacaoEmail;
	}

	public void setConfirmacaoEmail(String confirmacaoEmail) {
		this.confirmacaoEmail = confirmacaoEmail;
	}

	public Orgao getOrgaoSelecionado() {
		return orgaoSelecionado;
	}

	public void setOrgaoSelecionado(Orgao orgaoSelecionado) {
		this.orgaoSelecionado = orgaoSelecionado;
	}

	public OrgaoService getOrgaoService() {
		return orgaoService;
	}

	public void setOrgaoService(OrgaoService orgaoService) {
		this.orgaoService = orgaoService;
	}

	public Integer getNum1() {
		return num1;
	}

	public void setNum1(Integer num1) {
		this.num1 = num1;
	}

	public Integer getNum2() {
		return num2;
	}

	public void setNum2(Integer num2) {
		this.num2 = num2;
	}

	public Integer getNum3() {
		return num3;
	}

	public void setNum3(Integer num3) {
		this.num3 = num3;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

}

package br.com.sisfie.entidade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.envers.Audited;

import br.com.arquitetura.entidade.Entidade;
import br.com.arquitetura.util.DateUtil;

@Entity
@Table(name = "curso", catalog = "SISFIE")
@Audited
public class Curso extends Entidade<Integer> {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_curso", unique = true, nullable = false)
	@SequenceGenerator(name = "Curso", allocationSize = 1, sequenceName = "curso_id_curso_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Curso")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao", nullable = false)
	private Localizacao localizacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_ORGAO_SOLICITANTE")
	private Orgao orgao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_area")
	private Area area;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USUARIO_GESTOR", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_opcao_lista_cand", nullable = false)
	private OpcaoListaCandidato opcaoListaCandidato;

	@Column(name = "titulo", nullable = false)
	private String titulo;

	@Column(name = "cod_curso", nullable = false)
	private String codigo;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_realizacao_inicio", nullable = false, length = 13)
	private Date dtRealizacaoInicio;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_realizacao_fim", nullable = false, length = 13)
	private Date dtRealizacaoFim;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_inicio_inscricao", nullable = false, length = 13)
	private Date dtInicioInscricao;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_termino_inscricao", length = 13)
	private Date dtTerminoInscricao;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_limite_alteracao_oficina", length = 13)
	private Date dtLimiteAlteracaoOficina;

	@Column(name = "carga_horaria")
	private Integer cargaHoraria;

	@Column(name = "flg_finalizado")
	private Boolean flgFinalizado;

	@Column(name = "num_vagas")
	private Integer vagas;

	@Column(name = "flg_regiao_ativo", nullable = false)
	private Boolean flgRegiaoAtivo;

	@Column(name = "flg_data_ativo")
	private Boolean flgDataAtivo;

	@Column(name = "flg_possui_oficina")
	private Boolean flgPossuiOficina;

	@Column(name = "flg_curso_publico")
	private Boolean publico;

	@Column(name = "flg_exige_documentacao")
	private Boolean flgExigeDocumentacao;
	
	@Column(name = "nom_arquivo_frequencia")
	private String nomeArquivoFrequencia;
	
	@Column(name = "url_arquivo_frequencia")
	private String urlArquivoFrequencia;
	
	@Min(0)
	@Max(100)
	@Column(name = "porcentagem")
	private Integer porcentagem;
	
	@Min(0)
	@Max(100)
	@Column(name = "num_percentual_vagas_parceiro")
	private Integer numPercentualVagasParceiro;

	// Não usar cascade
	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<HomologacaoCurso> homologacaoCursos = new HashSet<HomologacaoCurso>(0);

	// Não usar cascade
	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<EsferaCurso> esferaCurso = new HashSet<EsferaCurso>(0);

	// Não usar cascade
	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<CandidatoPreenchimento> candidatoPreenchimentos = new HashSet<CandidatoPreenchimento>(0);

	// Não usar cascade
	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<UfCurso> ufCursos = new HashSet<UfCurso>(0);

	// Não usar cascade
	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<MunicipioCurso> municipioCursos = new HashSet<MunicipioCurso>(0);

	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<StatusCurso> statusCursos = new HashSet<StatusCurso>(0);

	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<InscricaoCurso> inscricoes = new HashSet<InscricaoCurso>(0);

	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<Turma> turmas = new HashSet<Turma>(0);

	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<AreaConhecimentoCurso> areaConhecimentoCursos = new HashSet<AreaConhecimentoCurso>(0);
	
	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<OrgaoCurso> orgaoCursos = new HashSet<OrgaoCurso>(0);

	@OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
	private Set<EmailCursoPrivado> emailsCursoPrivado = new HashSet<EmailCursoPrivado>(0);

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turno")
	private Turno turno;

	@Transient
	private List<UfCurso> exclusaoUfCursos = new ArrayList<UfCurso>();

	@Transient
	private List<MunicipioCurso> exclusaoMunicipioCursos = new ArrayList<MunicipioCurso>();

	@Transient
	private String dataRealizacaoInicio;

	@Transient
	private String dataRealizacaoFim;

	@Transient
	private String cursoData;
	
	@Transient
	private boolean isInstrutor;

	public Curso() {
	}

	public Curso(Integer id) {
		this.id = id;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public OpcaoListaCandidato getOpcaoListaCandidato() {
		return opcaoListaCandidato;
	}

	public void setOpcaoListaCandidato(OpcaoListaCandidato opcaoListaCandidato) {
		this.opcaoListaCandidato = opcaoListaCandidato;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Date getDtRealizacaoInicio() {
		return dtRealizacaoInicio;
	}

	public void setDtRealizacaoInicio(Date dtRealizacaoInicio) {
		this.dtRealizacaoInicio = dtRealizacaoInicio;
	}

	public Date getDtRealizacaoFim() {
		return dtRealizacaoFim;
	}

	public void setDtRealizacaoFim(Date dtRealizacaoFim) {
		this.dtRealizacaoFim = dtRealizacaoFim;
	}

	public Date getDtInicioInscricao() {
		return dtInicioInscricao;
	}

	public void setDtInicioInscricao(Date dtInicioInscricao) {
		this.dtInicioInscricao = dtInicioInscricao;
	}

	public Date getDtTerminoInscricao() {
		return dtTerminoInscricao;
	}

	public void setDtTerminoInscricao(Date dtTerminoInscricao) {
		this.dtTerminoInscricao = dtTerminoInscricao;
	}

	public Boolean getFlgFinalizado() {
		return flgFinalizado;
	}

	public void setFlgFinalizado(Boolean flgFinalizado) {
		this.flgFinalizado = flgFinalizado;
	}

	public Integer getVagas() {
		return vagas;
	}

	public void setVagas(Integer vagas) {
		this.vagas = vagas;
	}

	public Boolean getFlgRegiaoAtivo() {
		return flgRegiaoAtivo;
	}

	public void setFlgRegiaoAtivo(Boolean flgRegiaoAtivo) {
		this.flgRegiaoAtivo = flgRegiaoAtivo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getFlgDataAtivo() {
		return flgDataAtivo;
	}

	public void setFlgDataAtivo(Boolean flgDataAtivo) {
		this.flgDataAtivo = flgDataAtivo;
	}

	public Set<HomologacaoCurso> getHomologacaoCursos() {
		return homologacaoCursos;
	}

	public void setHomologacaoCursos(Set<HomologacaoCurso> homologacaoCursos) {
		this.homologacaoCursos = homologacaoCursos;
	}

	public Set<EsferaCurso> getEsferaCurso() {
		return esferaCurso;
	}

	public void setEsferaCurso(Set<EsferaCurso> esferaCurso) {
		this.esferaCurso = esferaCurso;
	}

	public Set<CandidatoPreenchimento> getCandidatoPreenchimentos() {
		return candidatoPreenchimentos;
	}

	public void setCandidatoPreenchimentos(Set<CandidatoPreenchimento> candidatoPreenchimentos) {
		this.candidatoPreenchimentos = candidatoPreenchimentos;
	}

	public Set<UfCurso> getUfCursos() {
		return ufCursos;
	}

	public void setUfCursos(Set<UfCurso> ufCursos) {
		this.ufCursos = ufCursos;
	}

	public Set<MunicipioCurso> getMunicipioCursos() {
		return municipioCursos;
	}

	public void setMunicipioCursos(Set<MunicipioCurso> municipioCursos) {
		this.municipioCursos = municipioCursos;
	}

	public Set<StatusCurso> getStatusCursos() {
		return statusCursos;
	}

	public void setStatusCursos(Set<StatusCurso> statusCursos) {
		this.statusCursos = statusCursos;
	}

	public List<UfCurso> getExclusaoUfCursos() {
		return exclusaoUfCursos;
	}

	public void setExclusaoUfCursos(List<UfCurso> exclusaoUfCursos) {
		this.exclusaoUfCursos = exclusaoUfCursos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<MunicipioCurso> getExclusaoMunicipioCursos() {
		return exclusaoMunicipioCursos;
	}

	public void setExclusaoMunicipioCursos(List<MunicipioCurso> exclusaoMunicipioCursos) {
		this.exclusaoMunicipioCursos = exclusaoMunicipioCursos;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Set<InscricaoCurso> getInscricoes() {
		return inscricoes;
	}

	public void setInscricoes(Set<InscricaoCurso> inscricoes) {
		this.inscricoes = inscricoes;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public Set<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Set<Turma> turmas) {
		this.turmas = turmas;
	}

	public Boolean getFlgPossuiOficina() {
		return flgPossuiOficina;
	}

	public void setFlgPossuiOficina(Boolean flgPossuiOficina) {
		this.flgPossuiOficina = flgPossuiOficina;
	}

	public Turno getTurno() {
		return turno;
	}

	public void setTurno(Turno turno) {
		this.turno = turno;
	}

	public Set<AreaConhecimentoCurso> getAreaConhecimentoCursos() {
		return areaConhecimentoCursos;
	}

	public void setAreaConhecimentoCursos(Set<AreaConhecimentoCurso> areaConhecimentoCursos) {
		this.areaConhecimentoCursos = areaConhecimentoCursos;
	}

	public String getDataRealizacaoInicio() {
		if (dtRealizacaoInicio != null) {
			return DateUtil.getDataHora(dtRealizacaoInicio, "dd/MM/yyyy");
		}
		return dataRealizacaoInicio;
	}

	public void setDataRealizacaoInicio(String dataRealizacaoInicio) {
		this.dataRealizacaoInicio = dataRealizacaoInicio;
	}

	public String getDataRealizacaoFim() {
		if (dtRealizacaoFim != null) {
			return DateUtil.getDataHora(dtRealizacaoFim, "dd/MM/yyyy");
		}
		return dataRealizacaoFim;
	}

	public void setDataRealizacaoFim(String dataRealizacaoFim) {
		this.dataRealizacaoFim = dataRealizacaoFim;
	}

	public String getCursoData() {
		if (dtRealizacaoInicio != null && dtRealizacaoFim != null && titulo != null && !titulo.isEmpty()) {
			String periodo = DateUtil.getDataHora(dtRealizacaoInicio, "dd/MM/yyyy") + " à "
					+ DateUtil.getDataHora(dtRealizacaoFim, "dd/MM/yyyy");
			cursoData = titulo + " - " + periodo;
		}
		return cursoData;
	}

	public void setCursoData(String cursoData) {
		this.cursoData = cursoData;
	}

	public Boolean getPublico() {
		if (publico == null || publico) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getPrivado() {
		if (publico != null && !publico) {
			return true;
		} else {
			return false;
		}
	}

	public void setPublico(Boolean publico) {
		this.publico = publico;
	}

	public Set<EmailCursoPrivado> getEmailsCursoPrivado() {
		return emailsCursoPrivado;
	}

	public void setEmailsCursoPrivado(Set<EmailCursoPrivado> emailsCursoPrivado) {
		this.emailsCursoPrivado = emailsCursoPrivado;
	}

	public Date getDtLimiteAlteracaoOficina() {
		return dtLimiteAlteracaoOficina;
	}

	public void setDtLimiteAlteracaoOficina(Date dtLimiteAlteracaoOficina) {
		this.dtLimiteAlteracaoOficina = dtLimiteAlteracaoOficina;
	}

	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public Boolean getFlgExigeDocumentacao() {
		return flgExigeDocumentacao;
	}

	public void setFlgExigeDocumentacao(Boolean flgExigeDocumentacao) {
		this.flgExigeDocumentacao = flgExigeDocumentacao;
	}

	public Integer getNumPercentualVagasParceiro() {
		return numPercentualVagasParceiro;
	}

	public void setNumPercentualVagasParceiro(Integer numPercentualVagasParceiro) {
		this.numPercentualVagasParceiro = numPercentualVagasParceiro;
	}

	public Integer getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(Integer porcentagem) {
		this.porcentagem = porcentagem;
	}

	public Set<OrgaoCurso> getOrgaoCursos() {
		return orgaoCursos;
	}

	public void setOrgaoCursos(Set<OrgaoCurso> orgaoCursos) {
		this.orgaoCursos = orgaoCursos;
	}

	public String getNomeArquivoFrequencia() {
		return nomeArquivoFrequencia;
	}

	public void setNomeArquivoFrequencia(String nomeArquivoFrequencia) {
		this.nomeArquivoFrequencia = nomeArquivoFrequencia;
	}

	public String getUrlArquivoFrequencia() {
		return urlArquivoFrequencia;
	}

	public void setUrlArquivoFrequencia(String urlArquivoFrequencia) {
		this.urlArquivoFrequencia = urlArquivoFrequencia;
	}

	public boolean isInstrutor() {
		return isInstrutor;
	}

	public void setInstrutor(boolean isInstrutor) {
		this.isInstrutor = isInstrutor;
	}
}

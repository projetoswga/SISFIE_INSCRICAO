package br.com.sisfie.entidade;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import br.com.arquitetura.entidade.Entidade;

@Entity
@Table(name = "inscricao_curso", catalog = "SISFIE")
@Audited
public class InscricaoCurso extends Entidade<Integer> {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_inscricao_curso", unique = true, nullable = false)
	@SequenceGenerator(name = "InscricaoCurso", sequenceName = "inscricao_curso_id_inscricao_curso_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InscricaoCurso")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_candidato", nullable = false)
	private Candidato candidato;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", nullable = false)
	private Curso curso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_situacao", nullable = false)
	private Situacao situacao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma", nullable = false)
	private Turma turma;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_cadastro", nullable = false, length = 13)
	private Date dtCadastro;

	@Column(name = "num_inscricao")
	private String numeroInscricao;

	@Column(name = "ano_homologacao")
	private Integer anoHomologacao;
	
	@Column(name = "flg_parceiro")
	private Boolean flgParceiro;
	
	@Column(name = "flg_instrutor")
	private Boolean flgInstrutor;
	
	@Column(name = "tp_status")
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "id_ultimo_status_inscricao", nullable = true)
	private StatusInscricao ultimoStatus;

	@OneToMany(mappedBy = "inscricaoCurso", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<StatusInscricao> statusInscricoes = new HashSet<StatusInscricao>(0);

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "inscricao_esfera", joinColumns = { @JoinColumn(name = "id_inscricao_curso") }, inverseJoinColumns = { @JoinColumn(name = "id_esfera_gov") })
	private Set<EsferaGoverno> inscricaoEsferas = new HashSet<EsferaGoverno>(0);
	
	@OneToMany(mappedBy = "inscricaoCurso", fetch = FetchType.LAZY)
	private Set<InscricaoInfoComplementar> inscricaoInfoComplemento = new HashSet<InscricaoInfoComplementar>(0);
	
	@OneToMany(mappedBy = "inscricaoCurso", fetch = FetchType.LAZY)
	private Set<SelecaoPacote> selecaoPacotes = new HashSet<SelecaoPacote>(0);
	
	@OneToMany(mappedBy = "inscricaoCurso", fetch = FetchType.LAZY)
	private Set<SelecaoOficina> selecaoOficinas = new HashSet<SelecaoOficina>(0);

	@Transient
	private String nomeImagem;

	@Transient
	private Status statusUltimo;
	
	@Transient
	private String tipoInscricao;

	public InscricaoCurso() {
	}

	public InscricaoCurso(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Candidato getCandidato() {
		return candidato;
	}

	public void setCandidato(Candidato candidato) {
		this.candidato = candidato;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Situacao getSituacao() {
		return situacao;
	}

	public void setSituacao(Situacao situacao) {
		this.situacao = situacao;
	}

	public Date getDtCadastro() {
		return dtCadastro;
	}

	public void setDtCadastro(Date dtCadastro) {
		this.dtCadastro = dtCadastro;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getNomeImagem() {
		return nomeImagem;
	}

	public void setNomeImagem(String nomeImagem) {
		this.nomeImagem = nomeImagem;
	}

	public Set<EsferaGoverno> getInscricaoEsferas() {
		return inscricaoEsferas;
	}

	public void setInscricaoEsferas(Set<EsferaGoverno> inscricaoEsferas) {
		this.inscricaoEsferas = inscricaoEsferas;
	}

	public Set<StatusInscricao> getStatusInscricoes() {
		return statusInscricoes;
	}

	public void setStatusInscricoes(Set<StatusInscricao> statusInscricoes) {
		this.statusInscricoes = statusInscricoes;
	}

	public Status getStatusUltimo() {
		return statusUltimo;
	}

	public void setStatusUltimo(Status statusUltimo) {
		this.statusUltimo = statusUltimo;
	}



	public String getNumeroInscricao() {
		return numeroInscricao;
	}

	public void setNumeroInscricao(String numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	public Integer getAnoHomologacao() {
		return anoHomologacao;
	}

	public void setAnoHomologacao(Integer anoHomologacao) {
		this.anoHomologacao = anoHomologacao;
	}

	public Set<InscricaoInfoComplementar> getInscricaoInfoComplemento() {
		return inscricaoInfoComplemento;
	}

	public void setInscricaoInfoComplemento(Set<InscricaoInfoComplementar> inscricaoInfoComplemento) {
		this.inscricaoInfoComplemento = inscricaoInfoComplemento;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Set<SelecaoPacote> getSelecaoPacotes() {
		return selecaoPacotes;
	}

	public void setSelecaoPacotes(Set<SelecaoPacote> selecaoPacotes) {
		this.selecaoPacotes = selecaoPacotes;
	}

	public Set<SelecaoOficina> getSelecaoOficinas() {
		return selecaoOficinas;
	}

	public void setSelecaoOficinas(Set<SelecaoOficina> selecaoOficinas) {
		this.selecaoOficinas = selecaoOficinas;
	}

	public StatusInscricao getUltimoStatus() {
		return ultimoStatus;
	}

	public void setUltimoStatus(StatusInscricao ultimoStatus) {
		this.ultimoStatus = ultimoStatus;
	}

	public Boolean getFlgParceiro() {
		return flgParceiro;
	}

	public void setFlgParceiro(Boolean flgParceiro) {
		this.flgParceiro = flgParceiro;
	}

	public Boolean getFlgInstrutor() {
		return flgInstrutor;
	}

	public void setFlgInstrutor(Boolean flgInstrutor) {
		this.flgInstrutor = flgInstrutor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTipoInscricao() {
		return tipoInscricao;
	}

	public void setTipoInscricao(String tipoInscricao) {
		this.tipoInscricao = tipoInscricao;
	}
}

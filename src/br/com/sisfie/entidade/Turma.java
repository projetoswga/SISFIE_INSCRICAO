package br.com.sisfie.entidade;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import br.com.arquitetura.entidade.Entidade;

@Entity
@Table(name = "TURMA", catalog = "SISFIE")
@Audited
public class Turma extends Entidade<Integer> {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id_turma", unique = true, nullable = false)
	@SequenceGenerator(name = "turma_seq", sequenceName = "turma_id_turma_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "turma_seq")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_curso")
	private Curso curso;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	@Column(name = "flg_ativo")
	private Boolean flgAtivo;

	// bi-directional many-to-one association to InscricaoCurso
	@OneToMany(mappedBy = "turma")
	private Set<InscricaoCurso> inscricaoCursos = new HashSet<InscricaoCurso>(0);

	// bi-directional many-to-one association to GradeOficina
	@OneToMany(mappedBy = "turma")
	private Set<GradeOficina> gradeOficinas;

	// bi-directional many-to-one association to GradePacote
	@OneToMany(mappedBy = "turma")
	private Set<GradePacote> gradePacotes;

	@SuppressWarnings("unused")
	@Transient
	private String ativoFormat;

	public Turma() {
	}

	public Turma(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getFlgAtivo() {
		return flgAtivo;
	}

	public void setFlgAtivo(Boolean flgAtivo) {
		this.flgAtivo = flgAtivo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAtivoFormat() {
		if (flgAtivo != null && flgAtivo) {
			return "Sim";
		} else {
			return "Não";
		}
	}

	public void setAtivoFormat(String ativoFormat) {
		this.ativoFormat = ativoFormat;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Set<InscricaoCurso> getInscricaoCursos() {
		return inscricaoCursos;
	}

	public void setInscricaoCursos(Set<InscricaoCurso> inscricaoCursos) {
		this.inscricaoCursos = inscricaoCursos;
	}

	public Set<GradeOficina> getGradeOficinas() {
		return gradeOficinas;
	}

	public void setGradeOficinas(Set<GradeOficina> gradeOficinas) {
		this.gradeOficinas = gradeOficinas;
	}

	public Set<GradePacote> getGradePacotes() {
		return gradePacotes;
	}

	public void setGradePacotes(Set<GradePacote> gradePacotes) {
		this.gradePacotes = gradePacotes;
	}

}

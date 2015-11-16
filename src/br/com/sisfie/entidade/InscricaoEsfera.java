package br.com.sisfie.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.com.arquitetura.entidade.Entidade;

@Entity
@Table(name = "inscricao_esfera", catalog = "SISFIE")
@Audited
public class InscricaoEsfera extends Entidade<Integer> {

	private static final long serialVersionUID = 6084660740804506496L;

	@Id
	@Column(name = "id_inscricao_esfera", unique = true, nullable = false)
	@SequenceGenerator(name = "inscricao_esfera_seq", sequenceName = "inscricao_esfera_id_inscricao_esfera_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inscricao_esfera_seq")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_inscricao_curso", nullable = false)
	private InscricaoCurso inscricaoCurso;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_esfera_gov", nullable = false)
	private EsferaGoverno esferaGoverno;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public InscricaoCurso getInscricaoCurso() {
		return inscricaoCurso;
	}

	public void setInscricaoCurso(InscricaoCurso inscricaoCurso) {
		this.inscricaoCurso = inscricaoCurso;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public EsferaGoverno getEsferaGoverno() {
		return esferaGoverno;
	}

	public void setEsferaGoverno(EsferaGoverno esferaGoverno) {
		this.esferaGoverno = esferaGoverno;
	}
}

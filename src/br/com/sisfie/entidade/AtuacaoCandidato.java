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
@Table(name = "atuacao_candidato", catalog = "SISFIE")
@Audited
public class AtuacaoCandidato extends Entidade<Integer> {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id_atuacao_candidato", unique = true, nullable = false)
	@SequenceGenerator(name = "atuacao_cand_seq", sequenceName = "atuacao_candidato_id_atuacao_candidato_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atuacao_cand_seq")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_candidato", nullable = false)
	private Candidato candidato;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_atuacao", nullable = false)
	private Atuacao atuacao;

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

	public Atuacao getAtuacao() {
		return atuacao;
	}

	public void setAtuacao(Atuacao atuacao) {
		this.atuacao = atuacao;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atuacao == null) ? 0 : atuacao.hashCode());
		result = prime * result + ((candidato == null) ? 0 : candidato.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AtuacaoCandidato other = (AtuacaoCandidato) obj;
		if (atuacao == null) {
			if (other.atuacao != null)
				return false;
		} else if (!atuacao.equals(other.atuacao))
			return false;
		if (candidato == null) {
			if (other.candidato != null)
				return false;
		} else if (!candidato.equals(other.candidato))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

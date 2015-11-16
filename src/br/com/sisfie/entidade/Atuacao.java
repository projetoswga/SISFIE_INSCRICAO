package br.com.sisfie.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import br.com.arquitetura.entidade.Entidade;

@Entity
@Table(name = "atuacao", catalog = "SISFIE")
@Audited
public class Atuacao extends Entidade<Integer> {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id_atuacao", unique = true, nullable = false)
	@SequenceGenerator(name = "atuacao_seq", sequenceName = "atuacao_id_atuacao_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atuacao_seq")
	private Integer id;

	@Column(name = "dsc_atuacao", nullable = false)
	private String descricao;

	@Column(name = "flg_ativo")
	private Boolean flgAtivo;

	@Transient
	private String ativoFormat;

	public Atuacao() {
	}

	public Atuacao(Integer id) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ativoFormat == null) ? 0 : ativoFormat.hashCode());
		result = prime * result + ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((flgAtivo == null) ? 0 : flgAtivo.hashCode());
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
		Atuacao other = (Atuacao) obj;
		if (ativoFormat == null) {
			if (other.ativoFormat != null)
				return false;
		} else if (!ativoFormat.equals(other.ativoFormat))
			return false;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (flgAtivo == null) {
			if (other.flgAtivo != null)
				return false;
		} else if (!flgAtivo.equals(other.flgAtivo))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

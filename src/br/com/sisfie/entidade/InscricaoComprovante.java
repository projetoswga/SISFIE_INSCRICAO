package br.com.sisfie.entidade;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.arquitetura.entidade.Entidade;
import br.com.arquitetura.excecao.ExcecaoUtil;

@Entity
@Table(name = "inscricao_comprovante", catalog = "SISFIE")
@Audited
public class InscricaoComprovante extends Entidade<Integer> {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_inscricao_comprovante", unique = true, nullable = false)
	@SequenceGenerator(name = "InscricaoComprovante", sequenceName = "inscricao_comprovante_id_inscricao_comprovante_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InscricaoComprovante")
	private Integer id;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_cadastro")
	private Date dtCadastro;

	@Column(name = "nom_arquivo")
	private String nomArquivo;

	@Column(name = "nom_tipo")
	private String nomTipo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_inscricao_curso")
	private InscricaoCurso inscricaoCurso;

	@Column(name = "URL_COMPROVANTE")
	private String urlImagem;

	@Transient
	private byte[] imgComprovante;

	@Transient
	private StreamedContent content;

	public InscricaoComprovante() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDtCadastro() {
		return this.dtCadastro;
	}

	public void setDtCadastro(Date dtCadastro) {
		this.dtCadastro = dtCadastro;
	}

	public String getNomArquivo() {
		return this.nomArquivo;
	}

	public void setNomArquivo(String nomArquivo) {
		this.nomArquivo = nomArquivo;
	}

	public String getNomTipo() {
		return this.nomTipo;
	}

	public void setNomTipo(String nomTipo) {
		this.nomTipo = nomTipo;
	}

	public InscricaoCurso getInscricaoCurso() {
		return this.inscricaoCurso;
	}

	public void setInscricaoCurso(InscricaoCurso inscricaoCurso) {
		this.inscricaoCurso = inscricaoCurso;
	}

	public void setContent(StreamedContent content) {
		this.content = content;
	}

	public StreamedContent getContent() throws SQLException {
		try {
			
			if(imgComprovante != null){
				InputStream is = new ByteArrayInputStream(imgComprovante);
				content = new DefaultStreamedContent(is, this.getNomTipo(), this.getNomArquivo());
			}else if (urlImagem != null) {
				File file = new File(urlImagem);
				InputStream is = new FileInputStream(file);
				content = new DefaultStreamedContent(is, this.getNomTipo(), this.getNomArquivo());
			}
			
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
		return content;
	}

	public byte[] getImgComprovante() {
		return imgComprovante;
	}

	public void setImgComprovante(byte[] imgComprovante) {
		this.imgComprovante = imgComprovante;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

}
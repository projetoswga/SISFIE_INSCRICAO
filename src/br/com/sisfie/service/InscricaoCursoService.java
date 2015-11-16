package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.Atuacao;
import br.com.sisfie.entidade.AtuacaoCandidato;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.entidade.CandidatoCargo;
import br.com.sisfie.entidade.CandidatoComplemento;
import br.com.sisfie.entidade.Cargo;
import br.com.sisfie.entidade.GradeOficina;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.InscricaoInfoComplementar;
import br.com.sisfie.entidade.SelecaoPacote;
import br.com.sisfie.entidade.StatusCurso;
import br.com.sisfie.entidade.StatusInscricao;

public interface InscricaoCursoService {

	public List<InscricaoCurso> listarInscricoes(InscricaoCurso inscricaoCurso) throws Exception;

	public List<Atuacao> listarAreaAtuacoes(List<Integer> idsAtuacoes) throws Exception;

	public List<Cargo> listarCargos(List<Integer> idsCargos) throws Exception;

	public void anexarComprovante(InscricaoCurso inscricaoCurso) throws Exception;

	public Integer retornarQuantidadeInscritos(Integer idCursoSelecionado);

	public void salvarCandidato(Candidato candidato, List<AtuacaoCandidato> atuacaoCandidatos, List<CandidatoCargo> candidatoCargos,
			List<CandidatoCargo> candidatoCargosExclusao, List<AtuacaoCandidato> atuacaoCandidatosExclusao) throws Exception;

	public List<InscricaoCurso> paginateInscricoes(int first, int pageSize, InscricaoCurso model) throws Exception;

	public Long countInscricoes(InscricaoCurso model) throws Exception;

	public void cancelarInscricao(InscricaoCurso inscricaoCurso) throws Exception;

	public void realizarInscricaoCurso(InscricaoCurso model, Candidato candidato, CandidatoComplemento candidatoComplemento,
			StatusCurso statusCurso, List<InscricaoDocumento> documentos) throws Exception;

	StatusInscricao ultimoStatusInscricao(InscricaoCurso inscricaoCurso);

	List<InscricaoComprovante> carregarComprovantes(InscricaoCurso inscricaoCursoSelecionado) throws Exception;

	InscricaoCurso recuperarInscricaoPeloHash(String hashCandidato) throws Exception;

	void confirmarPresenca(InscricaoCurso inscricaoCursoSelecionado) throws Exception;

	void aguardandoComprovante(List<InscricaoComprovante> comprovantes, List<InscricaoDocumento> documentos,
			StatusInscricao statusInscricao, InscricaoInfoComplementar inscricaoInfoComplementar) throws Exception;

	public void realizarInscricaoCurso(InscricaoCurso model, Candidato candidato, CandidatoComplemento candidatoComplemento,
			StatusCurso statusCurso, List<GradeOficina> listaGradeOficinas, List<InscricaoDocumento> documentos) throws Exception;

	public void realizarInscricaoCurso(InscricaoCurso model, Candidato candidato, CandidatoComplemento candidatoComplemento,
			StatusCurso statusCurso, SelecaoPacote selecaoPacote, List<InscricaoDocumento> documentos) throws Exception;

	void salvarSelecaoPacote(SelecaoPacote selecaoPacote) throws Exception;

	public List<InscricaoDocumento> carregarDocumentacao(InscricaoCurso inscricaoCursoSelecionado);

	int executarScript(String script) throws Exception;

}
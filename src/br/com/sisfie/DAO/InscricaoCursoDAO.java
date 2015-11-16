package br.com.sisfie.DAO;

import java.util.List;

import br.com.sisfie.entidade.Atuacao;
import br.com.sisfie.entidade.Cargo;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.StatusInscricao;

public interface InscricaoCursoDAO {

	List<Atuacao> listarAreaAtuacoes(List<Integer> idsAtuacoes);

	List<Cargo> listarCargos(List<Integer> idsCargos);

	Integer retornarQuantidadeInscritos(Integer idCursoSelecionado);

	List<InscricaoCurso> paginateInscricoes(int first, int pageSize, InscricaoCurso model);

	Long countInscricoes(InscricaoCurso model);

	StatusInscricao ultimoStatusInscricao(InscricaoCurso inscricaoCurso);

	List<InscricaoComprovante> carregarComprovantes(InscricaoCurso inscricaoCursoSelecionado) throws Exception;

	InscricaoCurso recuperarInscricaoPeloHash(String hashCandidato) throws Exception;

	List<InscricaoDocumento> carregarDocumentacao(InscricaoCurso inscricaoCursoSelecionado);

	int executarScript(String script) throws Exception;

}

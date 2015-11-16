package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.SelecaoPacote;

public interface SelecaoPacoteService {

	List<SelecaoPacote> listarSelecaoPacote(Integer idInscricaoCurso);

	void excluirSelecaoPacote(Integer idSelecaoPacote) throws Exception;

	void excluirSelecaoPacotes(List<SelecaoPacote> listaSelecaoPacote) throws Exception;

}

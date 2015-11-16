package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.Oficina;
import br.com.sisfie.entidade.SelecaoOficina;

public interface SelecaoOficinaService {

	SelecaoOficina recuperarSelecaoOficina(Integer idInscricaoCurso, Integer idOficina);

	List<SelecaoOficina> listarSelecaoOficina(Integer idInscricaoCurso);

	void salvarSelecaoOficina(Oficina oficina) throws Exception;

	void excluirSelecaoOficinas(List<SelecaoOficina> listaSelecaoOficinas) throws Exception;

	void salvarSelecaoOficina(List<SelecaoOficina> selecaoOficinas, List<SelecaoOficina> listaRemocao) throws Exception;

}

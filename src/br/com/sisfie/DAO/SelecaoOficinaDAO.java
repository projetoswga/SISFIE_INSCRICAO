package br.com.sisfie.DAO;

import java.util.List;

import br.com.sisfie.entidade.SelecaoOficina;

public interface SelecaoOficinaDAO {

	SelecaoOficina recuperarSelecaoOficina(Integer idInscricaoCurso, Integer idOficina);

	List<SelecaoOficina> listarSelecaoOficina(Integer idInscricaoCurso);

}

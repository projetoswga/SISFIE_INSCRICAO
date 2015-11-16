package br.com.sisfie.DAO;

import java.util.List;

import br.com.sisfie.entidade.Pacote;
import br.com.sisfie.entidade.PacoteOficina;

public interface PacoteDAO {

	PacoteOficina recuperarPacoteOficina(Integer idOficina, Integer idCurso);

	List<PacoteOficina> recuperarPacoteOficina(Integer idCurso);

	List<PacoteOficina> recuperarPacoteOficina(Pacote model);

	List<PacoteOficina> recuperarPacoteOficinaPorPacote(Integer idPacote);

	List<Pacote> recuperarPacote(Integer idCurso);

}

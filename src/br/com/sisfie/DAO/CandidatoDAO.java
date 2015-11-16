package br.com.sisfie.DAO;

import br.com.sisfie.entidade.Candidato;

public interface CandidatoDAO {

	Candidato buscarCandidato(String login) throws Exception;

	Integer recuperarParceiro(String email);

}

package br.com.sisfie.service;

import br.com.sisfie.entidade.Candidato;

public interface CandidatoService {

	void save(Candidato candidato, String senhaGerada) throws Exception;

	Integer recuperarParceiro(String email);

	Candidato get(Integer id) throws Exception;

}

package br.com.sisfie.service;

import org.springframework.mail.SimpleMailMessage;

import br.com.sisfie.entidade.Candidato;

public interface LoginService {

	public void esqueciSenha(Candidato user, SimpleMailMessage message) throws Exception;

	public Candidato buscarCandidato(String login) throws Exception;
}

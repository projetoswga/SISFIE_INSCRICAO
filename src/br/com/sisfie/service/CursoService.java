package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.EmailCursoPrivado;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.InscricaoCursoCertificado;
import br.com.sisfie.entidade.ModeloDocumento;
import br.com.sisfie.entidade.StatusCurso;
import br.com.sisfie.entidade.StatusInscricao;

public interface CursoService {

	List<Curso> listarCursosDisponiveis() throws Exception;
	
	StatusInscricao ultimoStatusInscricao(InscricaoCurso inscricaoCurso) throws Exception;
	
	StatusCurso ultimoStatusCurso(Curso curso) throws Exception;

	List<Curso> carregarOutrosCursos(Curso curso) throws Exception;

	Integer countInscritosCancelados(Integer idCurso);

	List<EmailCursoPrivado> listarCursosDisponiveisParceirosOuInstrutores(String emailParceiro) throws Exception;
	
	InscricaoCursoCertificado carregaInscricaoCursoCertPorIdInscricao(Integer idInscricaoCurso)throws Exception ;



	Curso carregaCursoById(Integer id) throws Exception;

	ModeloDocumento carregaModeloCurso(Integer id) throws Exception;
}

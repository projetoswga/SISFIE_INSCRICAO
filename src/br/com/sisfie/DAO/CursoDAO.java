package br.com.sisfie.DAO;

import java.util.List;

import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.EmailCursoPrivado;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.InscricaoCursoCertificado;
import br.com.sisfie.entidade.StatusCurso;
import br.com.sisfie.entidade.StatusInscricao;

public interface CursoDAO {

	List<Curso> listarCursoDisponiveis() throws Exception;

	StatusInscricao ultimoStatusInscricao(InscricaoCurso inscricaoCurso);

	StatusCurso ultimoStatusCurso(Curso curso);

	List<Curso> carregarOutrosCursos(Curso curso) throws Exception;

	Integer countInscritosCancelados(Integer idCurso);

	List<EmailCursoPrivado> listarCursosDisponiveisParceirosOuInstrutores(String emailParceiro) throws Exception;

	InscricaoCursoCertificado carregaInscricaoCursoCertPorIdInscricao(Integer idInscricaoCurso) throws Exception;

}

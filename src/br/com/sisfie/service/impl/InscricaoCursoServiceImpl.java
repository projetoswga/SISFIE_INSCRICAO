package br.com.sisfie.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.arquitetura.DAO.UniversalDAO;
import br.com.arquitetura.util.ThreadEnviarEmail;
import br.com.sisfie.DAO.InscricaoCursoDAO;
import br.com.sisfie.DAO.PacoteDAO;
import br.com.sisfie.entidade.Atuacao;
import br.com.sisfie.entidade.AtuacaoCandidato;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.entidade.CandidatoCargo;
import br.com.sisfie.entidade.CandidatoComplemento;
import br.com.sisfie.entidade.Cargo;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.GradeOficina;
import br.com.sisfie.entidade.Homologacao;
import br.com.sisfie.entidade.HomologacaoCurso;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.InscricaoGrade;
import br.com.sisfie.entidade.InscricaoInfoComplementar;
import br.com.sisfie.entidade.PacoteOficina;
import br.com.sisfie.entidade.SelecaoOficina;
import br.com.sisfie.entidade.SelecaoPacote;
import br.com.sisfie.entidade.Status;
import br.com.sisfie.entidade.StatusCurso;
import br.com.sisfie.entidade.StatusInscricao;
import br.com.sisfie.entidade.Usuario;
import br.com.sisfie.service.InscricaoCursoService;
import br.com.sisfie.util.Constantes;
import br.com.sisfie.util.CriptoUtil;

@Service(value = "inscricaoCursoService")
@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class InscricaoCursoServiceImpl implements InscricaoCursoService {

	@Autowired(required = true)
	@Qualifier(value = "universalDAO")
	protected UniversalDAO dao;

	@Autowired(required = true)
	@Qualifier(value = "inscricaoCursoDAO")
	protected InscricaoCursoDAO inscricaoCursoDAO;

	@Autowired(required = true)
	@Qualifier(value = "pacoteDAO")
	protected PacoteDAO pacoteDAO;

	@Autowired(required = true)
	@Qualifier(value = "mailSender")
	private JavaMailSender simpleMailSender;

	public UniversalDAO getDao() {
		return dao;
	}

	public void setDao(UniversalDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InscricaoCurso> listarInscricoes(InscricaoCurso inscricaoCurso) throws Exception {
		return dao.listBy(inscricaoCurso);
	}

	@Override
	public List<Atuacao> listarAreaAtuacoes(List<Integer> idsAtuacoes) {
		return inscricaoCursoDAO.listarAreaAtuacoes(idsAtuacoes);
	}

	@Override
	public List<Cargo> listarCargos(List<Integer> idsCargos) throws Exception {
		return inscricaoCursoDAO.listarCargos(idsCargos);
	}

	@Override
	public void anexarComprovante(InscricaoCurso inscricaoCurso) throws Exception {
		dao.save(inscricaoCurso);
	}

	@Override
	public Integer retornarQuantidadeInscritos(Integer idCursoSelecionado) {
		return inscricaoCursoDAO.retornarQuantidadeInscritos(idCursoSelecionado);
	}

	@Override
	public void salvarCandidato(Candidato candidato, List<AtuacaoCandidato> atuacaoCandidatos, List<CandidatoCargo> candidatoCargos,
			List<CandidatoCargo> candidatoCargosExclusao, List<AtuacaoCandidato> atuacaoCandidatosExclusao) throws Exception {
		dao.save(candidato);
		for (AtuacaoCandidato atuacaoCandidato : atuacaoCandidatosExclusao) {
			if (atuacaoCandidato.getId() != null) {
				dao.remove(AtuacaoCandidato.class, atuacaoCandidato.getId());
			}
		}
		for (CandidatoCargo candidatoCargo : candidatoCargosExclusao) {
			if (candidatoCargo.getId() != null) {
				dao.remove(CandidatoCargo.class, candidatoCargo.getId());
			}
		}
		for (AtuacaoCandidato atuacaoCandidato : atuacaoCandidatos) {
			dao.save(atuacaoCandidato);
		}
		for (CandidatoCargo candidatoCargo : candidatoCargos) {
			dao.save(candidatoCargo);
		}
	}

	@Override
	public List<InscricaoCurso> paginateInscricoes(int first, int pageSize, InscricaoCurso model) throws Exception {
		return inscricaoCursoDAO.paginateInscricoes(first, pageSize, model);
	}

	@Override
	public Long countInscricoes(InscricaoCurso model) throws Exception {
		return inscricaoCursoDAO.countInscricoes(model);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void cancelarInscricao(InscricaoCurso inscricaoCurso) throws Exception {

		StatusInscricao statusInscricao = new StatusInscricao(inscricaoCurso, new Usuario(Constantes.USUARIO_SISFIE_ID), new Status(
				Status.CANCELADO), new Date());

		dao.save(statusInscricao);

		// Atualizando o ultimo status da inscrição no objeto inscricaoCurso
		inscricaoCurso.setStatusUltimo(statusInscricao.getStatus());

		inscricaoCurso.setUltimoStatus(new StatusInscricao(statusInscricao.getId()));
		dao.merge(inscricaoCurso);

	}

	@Override
	public void realizarInscricaoCurso(InscricaoCurso model, Candidato candidato, CandidatoComplemento candidatoComplemento,
			StatusCurso statusCurso, List<InscricaoDocumento> documentos) throws Exception {
		// salva Inscricao Curso
		dao.save(model);
		
		// salva o ultimo status na inscrição do candidato. Neste momento só exite uma.
		List<StatusInscricao> listaStatusInscricao = new ArrayList<StatusInscricao>();
		listaStatusInscricao.addAll(model.getStatusInscricoes());
		model.setUltimoStatus(listaStatusInscricao.get(0));
		dao.merge(model);

		// se email chefe for obrigatorio salva candidato.
		if (candidato != null) {
			dao.save(candidato);
		}
		// Se tiver candidato complemento salva
		if (candidatoComplemento != null) {
			dao.save(candidatoComplemento);
		}
		// Altera status curso Caso seja o primeira a se inscrever.
		if (statusCurso != null) {
			dao.save(statusCurso);
		}

		// Salvando documentos para cursos que exigem documentação
		if (documentos != null && !documentos.isEmpty()) {
			for (InscricaoDocumento inscricaoDocumento : documentos) {
				inscricaoDocumento.setInscricaoCurso(model);
				dao.save(inscricaoDocumento);
			}
		}

		// gera o numero da inscricao
		SimpleDateFormat dfAno = new SimpleDateFormat("yyyy");
		String ano = dfAno.format(new Date());
		ano = ano.substring(2, 4);
		SimpleDateFormat dfMes = new SimpleDateFormat("MM");
		String mes = dfMes.format(new Date());

		Curso curso = (Curso) dao.get(Curso.class, model.getCurso().getId());
		String inscricao = curso.getCodigo().substring(6, 11);
		inscricao = ano + mes + inscricao + model.getId();

		model.setNumeroInscricao(inscricao);
		dao.merge(model);

		// Se o status da inscrição for pre inscricao e precisar de confirmação
		// do chefe
		InscricaoCurso inscricaoCurso = (InscricaoCurso) dao.get(InscricaoCurso.class, model.getId());
		inscricaoCurso.setCurso((Curso) dao.get(Curso.class, inscricaoCurso.getCurso().getId()));
		StatusInscricao ultimo = inscricaoCursoDAO.ultimoStatusInscricao(inscricaoCurso);

		StringBuilder textoEmail = new StringBuilder();
		String assunto = "";

		if (ultimo.getStatus().getId().equals(Status.PRE_INSCRITO)) {
			boolean homologacaoConfirmacaoChefe = false;
			for (HomologacaoCurso hm : curso.getHomologacaoCursos()) {
				Homologacao h = hm.getHomologacao();
				if (h.getId().equals(Homologacao.CONFIRMACAO_CHEFE)) {
					homologacaoConfirmacaoChefe = true;
					break;
				}
			}
			// Envia email para o chefe.
			if (homologacaoConfirmacaoChefe) {

				String hashCandidato = candidato.getId().toString() + "_" + inscricaoCurso.getId().toString();
				hashCandidato = CriptoUtil.getCriptografia(hashCandidato);
				String link = Constantes.ENDERECO_SERVIDOR_COM_PATH + "/pages/externo/confirmarInscricao.jsf?hash=" + hashCandidato;

				textoEmail.append("<p>Prezado(a),</p>");
				textoEmail.append("<p>A pr&eacute;-inscri&ccedil;&atilde;o do servidor(a) <b>" + candidato.getNome() + "</b>");
				textoEmail.append(" no evento " + curso.getTitulo());
				textoEmail.append(", no per&iacute;odo de " + curso.getDataRealizacaoInicio() + " a ");
				textoEmail.append(curso.getDataRealizacaoFim());
				textoEmail.append(", no local " + curso.getLocalizacao().getDescricao());
				textoEmail.append(" foi realizada com sucesso.</p>");
				textoEmail.append("<p>Para dar continuidade ao processo de inscri&ccedil;&atilde;o, ");
				textoEmail.append("solicitamos a gentileza de clicar no link <a href=\"" + link + "\">Autoriza&ccedil;&atilde;o</a> ");
				textoEmail.append("para autorizar a participa&ccedil;&atilde;o do candidato no evento.<br/>");
				textoEmail.append("<p>Caso n&atilde;o seja poss&iacute;vel acessar o link diretamente, ");
				textoEmail.append("por favor copie o endere&ccedil;o abaixo e cole na URL!<br/>");
				textoEmail.append(link + "</p>");
				textoEmail.append("<p>O comprovante de inscri&ccedil;&atilde;o ser&aacute; enviado para o email do candidato, ");
				textoEmail.append("assim que for homologado pelo coordenador do curso.</p>");
				textoEmail.append("<br/>");
				textoEmail.append("Mensagem Autom&aacute;tica - N&atilde;o responder");

				assunto = "Sistema SISFIE - CONFIRMAÇÃO DE PRE-INSCRIÇÃO DE CANDIDATO";

				Thread thread = new ThreadEnviarEmail(simpleMailSender, assunto, textoEmail.toString(), candidato.getEmailChefe(),
						Constantes.EMAIL_SISTEMA, true);
				thread.start();

			}
		} else if (ultimo.getStatus().getId().equals(Status.AGUARDANDO_VAGA)
				|| ultimo.getStatus().getId().equals(Status.AGUARDANDO_VAGA_PRIORIDADE)) {
			textoEmail.append("<p>Prezado(a), <b>" + model.getCandidato().getNome() + "</b></p> ");
			textoEmail.append("<p>A sua pr&eacute;-inscri&ccedil;&atilde;o foi aceita na lista de espera do evento " + curso.getTitulo()
					+ "</p>");
			textoEmail.append("<p>Aguarde o surgimento de Vaga ou abertura de nova turma.</p> ");
			textoEmail
					.append("<p>O pagamento de GRU ou emiss&atilde;o de Nota de empeho, nos eventos com taxa de inscri&ccedil;&atilde;o, n&atilde;o poder&aacute; ser realizados antes do status da inscri&ccedil;&atilde;o mudar para AGUARDANDO COMPROVANTE DE INSCRI&Ccedil;&Atilde;O na &aacute;rea do candidato na aba inscri&ccedil;&otilde;es realizadas.</p> ");
			textoEmail.append("<p>Atenciosamente.</p> ");
			textoEmail.append("<p>Coordena&ccedil;&atilde;o de Eventos<br /> ");
			textoEmail.append("Escola de Administra&ccedil;&atilde;o Fazend&aacute;ria-ESAF</p> ");
			textoEmail.append("<p>Mensagem Autom&aacute;tica - N&atilde;o responder</p>");

			assunto = "Sistema SISFIE - CONFIRMAÇÃO DE LISTA DE ESPERA DE CANDIDATO";

			Thread thread = new ThreadEnviarEmail(simpleMailSender, assunto, textoEmail.toString(), model.getCandidato()
					.getEmailInstitucional(), Constantes.EMAIL_SISTEMA, true);
			thread.start();
		}
	}

	@Override
	public StatusInscricao ultimoStatusInscricao(InscricaoCurso inscricaoCurso) {
		return inscricaoCursoDAO.ultimoStatusInscricao(inscricaoCurso);
	}

	@Override
	public List<InscricaoComprovante> carregarComprovantes(InscricaoCurso inscricaoCursoSelecionado) throws Exception {
		return inscricaoCursoDAO.carregarComprovantes(inscricaoCursoSelecionado);

	}

	@Override
	public InscricaoCurso recuperarInscricaoPeloHash(String hashCandidato) throws Exception {
		return inscricaoCursoDAO.recuperarInscricaoPeloHash(hashCandidato);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void aguardandoComprovante(List<InscricaoComprovante> comprovantes, List<InscricaoDocumento> documentos,
			StatusInscricao statusInscricao, InscricaoInfoComplementar inscricaoInfoComplementar) throws Exception {

		for (InscricaoComprovante comprovante : comprovantes) {
			if (comprovante.getId() == null) {
				// Salva arquivo em diretorio
				File file = new File(comprovante.getUrlImagem());
				OutputStream out = new FileOutputStream(file);
				byte[] contents = comprovante.getImgComprovante();
				out.write(contents);
				out.flush();
				out.close();

				// limpa o blob para não salvar em banco.
				comprovante.setImgComprovante(null);

				// salva o comprovante
				dao.save(comprovante);
			}

		}

		for (InscricaoDocumento documento : documentos) {
			if (documento.getId() == null) {
				// Salva arquivo em diretorio
				File file = new File(documento.getUrlImagem());
				OutputStream out = new FileOutputStream(file);
				byte[] contents = documento.getImgDocumento();
				out.write(contents);
				out.flush();
				out.close();

				// limpa o blob para não salvar em banco.
				documento.setImgDocumento(null);

				// salva o comprovante
				dao.save(documento);
			}

		}

		if (statusInscricao != null) {
			dao.save(statusInscricao);
			statusInscricao.getInscricaoCurso().setUltimoStatus(new StatusInscricao(statusInscricao.getId()));
			dao.merge(statusInscricao.getInscricaoCurso());
		}

		if (inscricaoInfoComplementar != null) {
			dao.merge(inscricaoInfoComplementar);
		}

		// return carregarComprovantes(new InscricaoCurso(statusInscricao.getInscricaoCurso().getId()));

	}

	@Override
	public void confirmarPresenca(InscricaoCurso inscricaoCurso) throws Exception {
		StatusInscricao statusInscricao = new StatusInscricao(inscricaoCurso, new Usuario(Constantes.USUARIO_SISFIE_ID), new Status(
				Status.PRESENCA_CONFIRMADA), new Date());
		dao.save(statusInscricao);

		// Atualizando o ultimo status da inscrição no objeto inscricaoCurso
		inscricaoCurso.setStatusUltimo(statusInscricao.getStatus());

		inscricaoCurso.setUltimoStatus(new StatusInscricao(statusInscricao.getId()));
		dao.merge(inscricaoCurso);
	}

	@Override
	public void realizarInscricaoCurso(InscricaoCurso model, Candidato candidato, CandidatoComplemento candidatoComplemento,
			StatusCurso statusCurso, List<GradeOficina> listaGradeOficinas, List<InscricaoDocumento> documentos) throws Exception {

		realizarInscricaoCurso(model, candidato, candidatoComplemento, statusCurso, documentos);

		for (GradeOficina gradeOficina : listaGradeOficinas) {
			InscricaoGrade inscricaoGrade = new InscricaoGrade();
			inscricaoGrade.setGradeOficina(new GradeOficina(gradeOficina.getId()));
			inscricaoGrade.setInscricaoCurso(new InscricaoCurso(model.getId()));
			dao.save(inscricaoGrade);
		}
	}

	@Override
	public void realizarInscricaoCurso(InscricaoCurso model, Candidato candidato, CandidatoComplemento candidatoComplemento,
			StatusCurso statusCurso, SelecaoPacote selecaoPacote, List<InscricaoDocumento> documentos) throws Exception {

		realizarInscricaoCurso(model, candidato, candidatoComplemento, statusCurso, documentos);

		// Salvando tambem na seleção oficina para posteriormente fazer a distribuição
		selecaoPacote.getPacote().setCurso(model.getCurso());
		List<PacoteOficina> listaPacoteOficinas = pacoteDAO.recuperarPacoteOficina(selecaoPacote.getPacote());
		if (listaPacoteOficinas != null && !listaPacoteOficinas.isEmpty()) {
			Integer ordem = 1;
			for (PacoteOficina pacoteOficina : listaPacoteOficinas) {
				SelecaoOficina selecaoOficina = new SelecaoOficina();
				selecaoOficina.setInscricaoCurso(model);
				selecaoOficina.setOficina(pacoteOficina.getOficina());
				selecaoOficina.setNumOrdemSelecao(ordem++);
				dao.save(selecaoOficina);
			}
		}
		dao.save(selecaoPacote);
	}

	@Override
	public void salvarSelecaoPacote(SelecaoPacote selecaoPacote) throws Exception {
		dao.save(selecaoPacote);
		List<PacoteOficina> listaPacoteOficinas = pacoteDAO.recuperarPacoteOficinaPorPacote(selecaoPacote.getPacote().getId());
		if (listaPacoteOficinas != null && !listaPacoteOficinas.isEmpty()) {
			Integer ordem = 1;
			for (PacoteOficina pacoteOficina : listaPacoteOficinas) {
				SelecaoOficina selecaoOficina = new SelecaoOficina();
				selecaoOficina.setInscricaoCurso(new InscricaoCurso(selecaoPacote.getInscricaoCurso().getId()));
				selecaoOficina.setOficina(pacoteOficina.getOficina());
				selecaoOficina.setNumOrdemSelecao(ordem++);
				dao.save(selecaoOficina);
			}
		}
	}

	@Override
	public List<InscricaoDocumento> carregarDocumentacao(InscricaoCurso inscricaoCursoSelecionado) {
		return inscricaoCursoDAO.carregarDocumentacao(inscricaoCursoSelecionado);
	}

	@Override
	public int executarScript(String script) throws Exception {
		return inscricaoCursoDAO.executarScript(script);
	}
}

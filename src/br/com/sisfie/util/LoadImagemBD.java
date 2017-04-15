package br.com.sisfie.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import br.com.arquitetura.excecao.ExcecaoUtil;
import br.com.arquitetura.util.DateUtil;
import br.com.sisfie.bean.InscricaoCursoBean;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoCursoCertificado;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.ModeloDocumento;
import br.com.sisfie.service.CandidatoService;
import br.com.sisfie.service.CursoService;
import br.com.sisfie.service.ImagemService;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

@WebServlet("/loadImagemBD")
public class LoadImagemBD extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Autowired(required = true)
	@Qualifier(value = "ImagemService")
	public ImagemService imagemService;
	
	@Autowired(required = true)
	@Qualifier(value = "cursoService")
	public CursoService cursoService;
	
	@Autowired(required = true)
	@Qualifier(value = "candidatoService")
	public CandidatoService candidatoService;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String DOCUMENTO = "documento";
	private static final String COMPROVANTE = "comprovante";
	private static final String FREQUENCIA = "frequencia";
	public static final String CERTIFICADO = "certificado";
	public static final String CERTIFICADO_TESTE = "certificado_teste";

	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			String tipo = (String) request.getParameter("tipo");
			if (tipo != null) {
				if (tipo.equals(DOCUMENTO)) {
					processRequestDocumento(request, response);
				} else if (tipo.equals(COMPROVANTE)) {
					processRequestComprovante(request, response);
				} else if (tipo.equals(FREQUENCIA)) {
					processRequestFrequencia(request, response);
				} else if (tipo.equals(CERTIFICADO)) {
					processRequestCertificado(request, response);
				}else if (tipo.equals(CERTIFICADO_TESTE)) {
					processRequestCertificadoTeste(request, response);
				}
			} else {
				throw new Exception("Parâmetro não encontrado");
			}
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}
	private void processRequestCertificadoTeste(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			
			String id = (String) request.getParameter("id");
			
			ModeloDocumento m = cursoService.carregaModeloCurso(new Integer(id));
			
			
			String templatePath  = m.getUrl();
			//TODO apagar a linha e descomentar a anterior para gerar versÃ£o ***
//			String templatePath  = "/Users/carlos/Downloads/teste.docx";
			SimpleDateFormat sdfFormatted = new SimpleDateFormat("dd/MM/YYYY");
			Map<String, Object> nonImageVariableMap = new HashMap<String, Object>();
			nonImageVariableMap.put("DATAFINAL", sdfFormatted.format(Calendar.getInstance().getTime()));
			nonImageVariableMap.put("DATAINICIAL", sdfFormatted.format(Calendar.getInstance().getTime()));
			nonImageVariableMap.put("nome", "Maria José Silva");
			nonImageVariableMap.put("NOME", "Maria José Silva");
			nonImageVariableMap.put("DIRETOR", "José Maria Silva");
			nonImageVariableMap.put("CONTEUDOPROGRAMATICOP", "Matemática;");
			nonImageVariableMap.put("CONTEUDOPROGRAMATICOD", "Matemática Financeira");
			nonImageVariableMap.put("CURSO", "SIAFI - Gerencial Teste");
			nonImageVariableMap.put("VALIDAINSCRICAO","001010101");
			nonImageVariableMap.put("DATAINICIALEXTENSO",DateUtil.formataData(new Date()));
			nonImageVariableMap.put("DATAFINALEXTENSO",DateUtil.formataData(new Date()));
			nonImageVariableMap.put("CARGA", "30H" );
			
			
			Map<String, String> imageVariablesWithPathMap = new HashMap<String, String>();
			Object pathHeaderLogo = InscricaoCursoBean.class.getResource("../../resources/design/imagem/cabecalho.jpg");
			if (null != pathHeaderLogo) {
				imageVariablesWithPathMap.put("header_image_logo", pathHeaderLogo.toString());
			}

			byte[] mergedOutput = new DocxDocumentMergerAndConverter().mergeAndGenerateOutput(
					templatePath, TemplateEngineKind.Freemarker, nonImageVariableMap, null);

			//response.getOutputStream().write(mergedOutput);

			enviarImagem(request, response, templatePath, m.getNomTipo(), mergedOutput);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}
	
	
	
	
	private void processRequestCertificado(HttpServletRequest request, HttpServletResponse response) {
		try {
			String idInscricaoCurso = (String) request.getParameter("idInscricaoCurso");
			String idCandidato = (String) request.getParameter("idCandidato");
			String idCurso = (String) request.getParameter("idCurso");
			InscricaoCursoCertificado icc = cursoService
					.carregaInscricaoCursoCertPorIdInscricao(Integer.parseInt(idInscricaoCurso));
			Candidato candidato = candidatoService.get(Integer.parseInt(idCandidato));
			Curso curso = cursoService.carregaCursoById(Integer.parseInt(idCurso));
			String templatePath  = icc.getModeloDocumento().getUrl();
			//TODO apagar a linha e descomentar a anterior para gerar versÃ£o ***
//			String templatePath  = "/Users/carlos/Downloads/teste.docx";
			SimpleDateFormat sdfFormatted = new SimpleDateFormat("dd/MM/YYYY");
			Map<String, Object> nonImageVariableMap = new HashMap<String, Object>();
			nonImageVariableMap.put("DATAFINAL", sdfFormatted.format(curso.getDtRealizacaoFim()));
			nonImageVariableMap.put("DATAINICIAL", sdfFormatted.format(curso.getDtRealizacaoInicio()));
			nonImageVariableMap.put("nome", candidato.getNome());
			nonImageVariableMap.put("NOME", candidato.getNome());
			nonImageVariableMap.put("DIRETOR", curso.getDiretor()!=null?curso.getDiretor():"S/N");
			nonImageVariableMap.put("CONTEUDOPROGRAMATICOP", curso.getConteudoProgramatico()!=null ?curso.getConteudoProgramatico():"S/N");
			nonImageVariableMap.put("CONTEUDOPROGRAMATICOD", curso.getConteudoProgramatico()!=null ?curso.getConteudoProgramatico():"S/N");
			nonImageVariableMap.put("CURSO", curso.getTitulo());
			nonImageVariableMap.put("VALIDAINSCRICAO",icc.getInscricaoCurso().getNumeroInscricao());
			nonImageVariableMap.put("DATAINICIALEXTENSO",DateUtil.formataData(curso.getDtRealizacaoInicio()));
			nonImageVariableMap.put("DATAFINALEXTENSO",DateUtil.formataData(curso.getDtRealizacaoFim()));
			nonImageVariableMap.put("CARGA", curso.getCargaHoraria() !=null ? curso.getCargaHoraria().toString() : "S/N" );
			nonImageVariableMap.put("NUMLIVRO", icc.getCodigoLivro() !=null ? curso.getCargaHoraria().toString() + "/"+icc.getAno() : "S/N" );
			
			
			Map<String, String> imageVariablesWithPathMap = new HashMap<String, String>();
			Object pathHeaderLogo = InscricaoCursoBean.class.getResource("../../resources/design/imagem/cabecalho.jpg");
			if (null != pathHeaderLogo) {
				imageVariablesWithPathMap.put("header_image_logo", pathHeaderLogo.toString());
			}

			byte[] mergedOutput = new DocxDocumentMergerAndConverter().mergeAndGenerateOutput(
					templatePath, TemplateEngineKind.Freemarker, nonImageVariableMap, null);

			//response.getOutputStream().write(mergedOutput);

			enviarImagem(request, response, icc.getModeloDocumento().getUrl(),
					icc.getModeloDocumento().getNomTipo(), mergedOutput);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}
	
	private void processRequestFrequencia(HttpServletRequest request, HttpServletResponse response) {
		try {
			Curso curso = null;
			String idDownload = (String) request.getParameter("idImagemDownload");
			if (idDownload != null && !idDownload.isEmpty()) {
				ValidacaoUtil.somenteNumero(idDownload);
				curso = imagemService.carregarCursoId(new Integer(idDownload));
			} else {
				String paramId = request.getParameter("id");
				ValidacaoUtil.somenteNumero(paramId);
				if (paramId == null || paramId.isEmpty()) {
					throw new Exception("Parâmetro não encontrado");
				}
				curso = imagemService.carregarCursoId(new Integer(paramId));
			}
			if (curso == null || curso.getUrlArquivoFrequencia() == null) {
				throw new Exception("Parâmetro não encontrado");
			}
			enviarImagem(request, response, curso.getUrlArquivoFrequencia(), curso.getNomeArquivoFrequencia(), null);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	private void processRequestDocumento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			InscricaoDocumento inscricaoDocumento = null;
			String idDownload = (String) request.getParameter("idImagemDownload");
			if (idDownload != null && !idDownload.isEmpty()) {
				ValidacaoUtil.somenteNumero(idDownload);
				inscricaoDocumento = imagemService.carregarDocumentoId(new Integer(idDownload));
			} else {
				String paramId = request.getParameter("id");
				ValidacaoUtil.somenteNumero(paramId);
				if (paramId == null || paramId.isEmpty()) {
					throw new Exception("Parâmetro não encontrado");
				}
				inscricaoDocumento = imagemService.carregarDocumentoId(new Integer(paramId));
			}
			if (inscricaoDocumento == null || inscricaoDocumento.getUrlImagem() == null) {
				throw new Exception("Parâmetro não encontrado");
			}
			enviarImagem(request, response, inscricaoDocumento.getUrlImagem(), inscricaoDocumento.getNomTipo(), null);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	protected void processRequestComprovante(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			InscricaoComprovante inscricaoComprovante = null;
			String idDownload = (String) request.getParameter("idImagemDownload");
			if (idDownload != null && !idDownload.isEmpty()) {
				ValidacaoUtil.somenteNumero(idDownload);
				inscricaoComprovante = imagemService.carregarImagemId(new Integer(idDownload));
			} else {
				String paramId = request.getParameter("id");
				ValidacaoUtil.somenteNumero(paramId);
				if (paramId == null || paramId.isEmpty()) {
					throw new Exception("Parâmetro não encontrado");
				}
				inscricaoComprovante = imagemService.carregarImagemId(new Integer(paramId));
			}
			if (inscricaoComprovante == null || inscricaoComprovante.getUrlImagem() == null) {
				throw new Exception("Parâmetro não encontrado");
			}
			enviarImagem(request, response, inscricaoComprovante.getUrlImagem(), inscricaoComprovante.getNomTipo(), null);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	private void enviarImagem(HttpServletRequest request, HttpServletResponse response, String imageFileName, String contentType, byte[] content)
			throws Exception {

		if (imageFileName != null) {
			imageFileName = imageFileName.replaceAll("\\.+(\\\\|/)", "");

			// Decode the file name and prepare file object.
			imageFileName = URLDecoder.decode(imageFileName, "UTF-8");

			// Get content type by filename.
			File file = new File(imageFileName);

			// Preparando os streams
			InputStream input = null;
			OutputStream output = null;

			try {
				// Init servlet response.
				response.reset();
				response.setBufferSize(DEFAULT_BUFFER_SIZE);
		        String mimeType = null;
		        long fileSize = 0;
				if (null == content) {
					input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
					mimeType = request.getServletContext().getMimeType(imageFileName);
					fileSize = file.length();
				} else {
					input = new ByteArrayInputStream(content);
					mimeType = URLConnection.guessContentTypeFromStream(input);
					fileSize = content.length;
				}
		        if (mimeType == null) {
		            mimeType = contentType==null?"application/octet-stream":contentType;
		        }
		        System.out.println("MIME type: " + mimeType);
		        
		        response.setContentType(mimeType); //application/force-download
				response.setContentLength((int)fileSize);
		        //response.setContentLength(-1);
				response.addHeader("Content-Transfer-Encoding", "binary");
				response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

				output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

				// Write file contents to response.
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int length;
				while ((length = input.read(buffer)) > 0) {
					output.write(buffer, 0, length);
				}

				// Finalizando
				output.flush();
				response.flushBuffer();
			} catch (IOException e) {
				throw new IOException();
			} finally {
				// Gently close streams.
				//close(output);
				close(input);
			}
		}
	}

	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

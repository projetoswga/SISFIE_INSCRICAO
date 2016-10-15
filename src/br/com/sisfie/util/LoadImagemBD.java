package br.com.sisfie.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import br.com.arquitetura.excecao.ExcecaoUtil;
import br.com.sisfie.bean.InscricaoCursoBean;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoCursoCertificado;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.service.CandidatoService;
import br.com.sisfie.service.CursoService;
import br.com.sisfie.service.ImagemService;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

@Component
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
				}
			} else {
				throw new Exception("Parâmetro não encontrado");
			}
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
			//TODO apagar a linha e descomentar a anterior para gerar versão ***
//			String templatePath  = "/Users/carlos/Downloads/teste.docx";
			SimpleDateFormat sdfFormatted = new SimpleDateFormat("dd/MM/YYYY");
			SimpleDateFormat sdfSimply = new SimpleDateFormat("ddMMYYYY");
			Map<String, Object> nonImageVariableMap = new HashMap<String, Object>();
			nonImageVariableMap.put("DATAFINAL", sdfFormatted.format(Calendar.getInstance().getTime()));
			nonImageVariableMap.put("nome", candidato.getNome());
			nonImageVariableMap.put("NOME", candidato.getNome());
			nonImageVariableMap.put("Diretor", icc.getDiretor());
			nonImageVariableMap.put("CONTEUDOPROGRAMATICOP", icc.getConteudoProgramaticoParticipante());
			nonImageVariableMap.put("CONTEUDOPROGRAMATICOD", icc.getConteudoProgramaticoDocente());
			nonImageVariableMap.put("CURSO", curso.getTitulo());
			nonImageVariableMap.put("CARGA", curso.getCargaHoraria() !=null ? curso.getCargaHoraria().toString() : "S/N" );
			
			
			Map<String, String> imageVariablesWithPathMap = new HashMap<String, String>();
			Object pathHeaderLogo = InscricaoCursoBean.class.getResource("../../resources/design/imagem/cabecalho.jpg");
			if (null != pathHeaderLogo) {
				imageVariablesWithPathMap.put("header_image_logo", pathHeaderLogo.toString());
			}

			byte[] mergedOutput = new DocxDocumentMergerAndConverter().mergeAndGeneratePDFOutput(
					templatePath, TemplateEngineKind.Freemarker, nonImageVariableMap, null);

			response.getOutputStream().write(mergedOutput);

			enviarImagem(request, response, icc.getModeloDocumento().getUrl(),
					icc.getModeloDocumento().getNomTipo());
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}
	
	
//	ModeloDocumento m = icc.getModeloDocumento();
//	if(m!=null){
//		String templatePath  = m.getUrl();
//		SimpleDateFormat sdfFormatted = new SimpleDateFormat("dd/MM/YYYY");
//		SimpleDateFormat sdfSimply = new SimpleDateFormat("ddMMYYYY");
//		Map<String, Object> nonImageVariableMap = new HashMap<String, Object>();
//		nonImageVariableMap.put("thank_you_date", sdfFormatted.format(Calendar.getInstance().getTime()));
//		nonImageVariableMap.put("name", icc.getInscricaoCurso().getCandidato().getNome());
//		nonImageVariableMap.put("website", icc.getInscricaoCurso().getCurso().getTitulo());
//		nonImageVariableMap.put("author_name", icc.getInscricaoCurso().getNumeroInscricao());
//		nonImageVariableMap.put("nanico_name", "Tenente Ribeiro");
//		nonImageVariableMap.put("processo", "2015.333.11");
//		
//		Map<String, String> imageVariablesWithPathMap = new HashMap<String, String>();
//		Object pathHeaderLogo = InscricaoCursoBean.class.getResource("../../resources/design/imagem/cabecalho.jpg");
//		if (null != pathHeaderLogo) {
//			imageVariablesWithPathMap.put("header_image_logo", pathHeaderLogo.toString());
//		}
// 
//		//BasicConfigurator.configure(new NullAppender()); //Necessário para evitar "java.util.NoSuchElementException ... at org.docx4j.utils.Log4jConfigurator.configure(Log4jConfigurator.java:42) [docx4j-2.8.1.jar:]"
//		
//		DocxDocumentMergerAndConverter docxDocumentMergerAndConverter = new DocxDocumentMergerAndConverter();
//		byte[] mergedOutput = docxDocumentMergerAndConverter.mergeAndGeneratePDFOutput(templatePath, TemplateEngineKind.Freemarker, nonImageVariableMap, imageVariablesWithPathMap);
//		
//		ServletOutputStream sos = null;
//		FacesContext context = FacesContext.getCurrentInstance();
//		try {
//			
//			HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
//            res.setContentType("application/pdf");
//            //Código abaixo gerar o relatório e disponibiliza diretamente na página 
//            res.setHeader("Content-disposition", "inline;filename=arquivo.pdf");
//            //Código abaixo gerar o relatório e disponibiliza para o cliente baixar ou salvar 
//            sos = res.getOutputStream();
//            sos.write(mergedOutput);
//            System.out.println("Cerficado gerado a partir do template: " + templatePath);
//            
//            // TODO REMOVER TRECHO ABAIXO
//            FileOutputStream fos = new FileOutputStream(new File("c:/temp/certificado123321.pdf"));
//            fos.write(mergedOutput);
//            fos.close();
//		} finally {
//			if (null != sos) {
//				sos.flush();
//				sos.close();
//			}
//            //FacesContext.getCurrentInstance().responseComplete();  //NOT WORK
//    		context.renderResponse();
//    		context.responseComplete();
//		}
//	}
//}
//} catch (Exception e) {
//ExcecaoUtil.tratarExcecao(e);
//}
	
	
	
	
	
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
			enviarImagem(request, response, curso.getUrlArquivoFrequencia(), curso.getNomeArquivoFrequencia());
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
			enviarImagem(request, response, inscricaoDocumento.getUrlImagem(), inscricaoDocumento.getNomTipo());
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
			enviarImagem(request, response, inscricaoComprovante.getUrlImagem(), inscricaoComprovante.getNomTipo());
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	private void enviarImagem(HttpServletRequest request, HttpServletResponse response, String imageFileName, String contentType)
			throws Exception {

		if (imageFileName != null) {
			imageFileName = imageFileName.replaceAll("\\.+(\\\\|/)", "");

			// Decode the file name and prepare file object.
			imageFileName = URLDecoder.decode(imageFileName, "UTF-8");

			// Get content type by filename.

			File file = new File(imageFileName);

			// Preparando os streams
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			try {
				// Abrindo a imagem
				input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

				// Init servlet response.
				response.reset();
				response.setBufferSize(DEFAULT_BUFFER_SIZE);
				response.setContentType(contentType);
				response.setHeader("Content-disposition", "inline; filename=\"" + imageFileName + "\"");
				output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

				// Write file contents to response.
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int length;
				while ((length = input.read(buffer)) > 0) {
					output.write(buffer, 0, length);
				}

				// Finalizando
				output.flush();
			} catch (IOException e) {
				throw new IOException();
			} finally {
				// Gently close streams.
				close(output);
				close(input);
			}
		}
	}

	private static void close(Closeable resource) throws IOException {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				throw new IOException();
			}
		}
	}
}

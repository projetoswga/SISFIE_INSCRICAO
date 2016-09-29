package br.com.sisfie.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

import com.lowagie.text.FontFactory;

import br.com.arquitetura.excecao.ExcecaoUtil;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoCursoCertificado;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.ModeloDocumento;
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
			InscricaoCursoCertificado inscricaoCursoCertificado = cursoService
					.carregaInscricaoCursoCertPorIdInscricao(Integer.parseInt(idInscricaoCurso));

			byte[] mergedOutput = new DocxDocumentMergerAndConverter().mergeAndGeneratePDFOutput(
					inscricaoCursoCertificado.getModeloDocumento().getUrl(), TemplateEngineKind.Freemarker, null, null);

			response.getOutputStream().write(mergedOutput);

			enviarImagem(request, response, inscricaoCursoCertificado.getModeloDocumento().getUrl(),
					inscricaoCursoCertificado.getModeloDocumento().getNomTipo());
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

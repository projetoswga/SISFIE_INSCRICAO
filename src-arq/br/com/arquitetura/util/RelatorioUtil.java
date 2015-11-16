package br.com.arquitetura.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 * @author Igor Galv�o <br/>
 *         Class Util para gerar Relatorios.<br/>
 *         Pode ser exibido no browser
 * @version 1.0
 * @date 23/09/2011
 */

public class RelatorioUtil {

	/**
	 * 
	 * @param lista
	 * @param parametros
	 * @param caminho
	 * @param nomePDF
	 * @throws JRException
	 * @throws IOException
	 * @funcionalidade: Gerar relatorio com lista e parametos
	 */
	public static void gerarRelatorio(Collection lista, Map parametros, String caminho, String nomePDF)
			throws JRException, IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		/* Cria o response */
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		/* Coloca o nome do PDF e o Tipo que o browser deve interpretar */
		response.setHeader("Content-Disposition", "attachment; filename=" + nomePDF);
		response.setContentType("application/pdf");

		/* Cria o Stream com o caminho */
		InputStream stream = context.getExternalContext().getResourceAsStream(caminho);

		/* Cria a lista do relatorio */
		JRDataSource colecao = new JRBeanCollectionDataSource(lista == null ? new ArrayList() : lista);
		JasperPrint jasperPrint = JasperFillManager.fillReport(stream, parametros, colecao);
		ServletOutputStream outputStream = response.getOutputStream();
		JRExporter jrExporter = new JRPdfExporter();
		jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		jrExporter.exportReport();
		outputStream.flush();
		outputStream.close();
		context.renderResponse();
		context.responseComplete();
	}

	/**
	 * 
	 * @param lista
	 * @param parametros
	 * @param caminho
	 * @param nomePDF
	 * @throws JRException
	 * @throws IOException
	 * @funcionalidade: Gerar relatorio com parametos
	 */
	public static void gerarRelatorio(Map parametros, String caminho, String nomePDF) throws JRException, IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		/* Cria o response */
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		/* Coloca o nome do PDF e o Tipo que o browser deve interpretar */

		response.setHeader("Content-Disposition", "attachment; filename=" + nomePDF);
		response.setContentType("application/pdf");

		/* Cria o Stream com o caminho */
		InputStream stream = context.getExternalContext().getResourceAsStream(caminho);

		/* Quando nao passa a lista da erro */
		/* Para resolver vou criar uma lista sem nada */
		Collection lista = new ArrayList();
		lista.add(new Object());
		JRDataSource colecao = new JRBeanCollectionDataSource(lista);
		JasperPrint jasperPrint = JasperFillManager.fillReport(stream, parametros, colecao);
		ServletOutputStream outputStream = response.getOutputStream();
		JRExporter jrExporter = new JRPdfExporter();
		jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		jrExporter.exportReport();
		outputStream.flush();
		outputStream.close();
		context.renderResponse();
		context.responseComplete();

	}

	/**
	 * 
	 * @param lista
	 * @param parametros
	 * @param caminho
	 * @param nomePDF
	 * @throws JRException
	 * @throws IOException
	 * @funcionalidade: Gerar relatorio com Lista
	 */
	public static void gerarRelatorio(Collection lista, String caminho, String nomePDF) throws JRException, IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		/* Cria o response */
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		/* Coloca o nome do PDF e o Tipo que o browser deve interpretar */
		response.setHeader("Content-Disposition", "attachment; filename=" + nomePDF);
		response.setContentType("application/pdf");

		/* Cria o Stream com o caminho */
		InputStream stream = context.getExternalContext().getResourceAsStream(caminho);

		/* Cria a lista do relatorio */
		JRDataSource dataSource = new JRBeanCollectionDataSource(lista == null ? new ArrayList() : lista);
		JasperPrint jasperPrint = JasperFillManager.fillReport(stream, new HashMap<String, Object>(), dataSource);
		ServletOutputStream outputStream = response.getOutputStream();
		JRExporter jrExporter = new JRPdfExporter();
		jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		jrExporter.exportReport();
		outputStream.flush();
		outputStream.close();
		context.renderResponse();
		context.responseComplete();
	}

	/**
	 * 
	 * @param lista
	 * @param parametros
	 * @param caminho
	 * @param nomePDF
	 * @throws JRException
	 * @throws IOException
	 * @funcionalidade: Gerar relatorio
	 */
	public static void gerarRelatorio(String caminho, String nomePDF) throws JRException, IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		/* Cria o response */
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		/* Coloca o nome do PDF e o Tipo que o browser deve interpretar */
		response.setHeader("Content-Disposition", "attachment; filename=" + nomePDF);
		response.setContentType("application/pdf");

		/* Cria o Stream com o caminho */
		InputStream stream = context.getExternalContext().getResourceAsStream(caminho);
		/* Quando nao passa a lista da erro */
		/* Para resolver vou criar uma lista sem nada */
		Collection lista = new ArrayList();
		lista.add(new Object());
		JRDataSource colecao = new JRBeanCollectionDataSource(lista);
		JasperPrint jasperPrint = JasperFillManager.fillReport(stream, new HashMap<String, Object>(), colecao);
		ServletOutputStream outputStream = response.getOutputStream();
		JRExporter jrExporter = new JRPdfExporter();
		jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		jrExporter.exportReport();
		outputStream.flush();
		outputStream.close();
		context.renderResponse();
		context.responseComplete();
	}

}

package br.com.sisfie.teste;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import br.com.sisfie.util.DocxDocumentMergerAndConverter;
import fr.opensagres.xdocreport.converter.ConverterRegistry;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.IConverter;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.converter.XDocConverterException;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.DocumentKind;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

public class DOCXtoPDFTest {
	//https://mvnrepository.com/artifact/fr.opensagres.xdocreport
	private static byte[] convertDocxToPDF(String fileUrl) throws IOException, XDocConverterException {
		  Options options=Options.getFrom(DocumentKind.DOCX).to(ConverterTypeTo.PDF);
		  IConverter converter=ConverterRegistry.getRegistry().getConverter(options);
		  if (null == converter) {
			  converter=ConverterRegistry.getRegistry().findConverter(options);
		  }
		  InputStream in=new FileInputStream(new File(fileUrl));
		  ByteArrayOutputStream os=new ByteArrayOutputStream();
		  converter.convert(in,os,options);
		  in.close();
		  return os.toByteArray();
	}
	
	private byte[] convertDocxToPDF2(String templatePath) throws IOException, XDocReportException, Docx4JException {
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
		nonImageVariableMap.put("DATAINICIALEXTENSO","01/04/2017");
		nonImageVariableMap.put("DATAFINALEXTENSO","10/04/2017");
		nonImageVariableMap.put("CARGA", "30H" );
		
		
		byte[] mergedOutput = new DocxDocumentMergerAndConverter().mergeAndGeneratePDFOutput(
				templatePath, TemplateEngineKind.Freemarker, nonImageVariableMap, null);
		
		return mergedOutput;
	}
	
	public static void main(String[] args) {
		try {
			FileOutputStream fos = new FileOutputStream(new File("D:/ambdes/OUTPUT_ODT.pdf"));
			fos.write(new DOCXtoPDFTest().convertDocxToPDF2("D:/ambdes/CERTIFICADOCOMUM_ODT_2.odt"));
			fos.close();
			System.out.println("------------------PDF from ODT generated!");
			
			fos = new FileOutputStream(new File("D:/ambdes/OUTPUT_DOCX.pdf"));
			fos.write(new DOCXtoPDFTest().convertDocxToPDF2("D:/ambdes/CERTIFICADOCOMUM_DOCX.docx"));
			fos.close();

			System.out.println("------------------PDF from DOCX generated!");
		} catch (XDocConverterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XDocReportException e) {
			e.printStackTrace();
		} catch (Docx4JException e) {
			e.printStackTrace();
		}
	}
}

package br.com.sisfie.service;

import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.ModeloDocumento;

public interface ImagemService {

	InscricaoComprovante carregarImagemId(Integer id) throws Exception;

	InscricaoDocumento carregarDocumentoId(Integer idDocumento);

	Curso carregarCursoId(Integer idCurso);

	ModeloDocumento carregarCertificadoId(Integer idCertificado);
	
}

package br.com.sisfie.DAO;

import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoDocumento;

public interface ImagemDAO {

	InscricaoComprovante carregarImagemId(Integer id) throws Exception;

	InscricaoDocumento carregarDocumentoId(Integer idDocumento);

}

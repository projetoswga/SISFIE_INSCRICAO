package br.com.sisfie.DAO;

import java.util.List;

import br.com.sisfie.entidade.DistribuicaoSofGrade;

public interface DistribuirDAO {

	List<DistribuicaoSofGrade> listarDistribuicao(List<Integer> idsSelecaoOficinas);

}

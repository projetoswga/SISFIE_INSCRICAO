package br.com.sisfie.service;

import java.util.List;

import br.com.sisfie.entidade.DistribuicaoSofGrade;

public interface DistribuirService {

	List<DistribuicaoSofGrade> listarDistribuicao(List<Integer> idsSelecaoOficinas);

}

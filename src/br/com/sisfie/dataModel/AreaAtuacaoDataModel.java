package br.com.sisfie.dataModel;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.com.sisfie.entidade.Atuacao;

public class AreaAtuacaoDataModel extends ListDataModel<Atuacao> implements SelectableDataModel<Atuacao> {

	public AreaAtuacaoDataModel() {
	}

	public AreaAtuacaoDataModel(List<Atuacao> data) {
		super(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Atuacao getRowData(String rowKey) {

		List<Atuacao> lista = (List<Atuacao>) getWrappedData();

		for (Atuacao obj : lista) {
			if (obj.getId().equals(Integer.valueOf(rowKey)))
				return obj;
		}

		return null;
	}

	@Override
	public Object getRowKey(Atuacao obj) {
		return obj.getId();
	}
}
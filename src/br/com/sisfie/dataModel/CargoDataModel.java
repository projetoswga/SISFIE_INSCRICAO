package br.com.sisfie.dataModel;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.com.sisfie.entidade.Cargo;

public class CargoDataModel extends ListDataModel<Cargo> implements SelectableDataModel<Cargo> {

	public CargoDataModel() {
	}

	public CargoDataModel(List<Cargo> data) {
		super(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cargo getRowData(String rowKey) {

		List<Cargo> lista = (List<Cargo>) getWrappedData();

		for (Cargo obj : lista) {
			if (obj.getId().equals(Integer.valueOf(rowKey)))
				return obj;
		}

		return null;
	}

	@Override
	public Object getRowKey(Cargo obj) {
		return obj.getId();
	}
}
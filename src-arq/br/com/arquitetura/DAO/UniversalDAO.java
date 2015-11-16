package br.com.arquitetura.DAO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import br.com.arquitetura.util.CriteriaMC;

public interface UniversalDAO extends Serializable {

	Object get(Class clazz, Serializable id);

	List getAll(Class clazz);

	Long getCount(Class clazz, Map filters) throws Exception;

	Long getCount(Object o);

	@SuppressWarnings("rawtypes")
	public List listBy(Object o, boolean like) throws Exception;

	Object save(Object o) throws Exception;

	Object merge(Object o) throws Exception;

	List listBy(Object o) throws Exception;

	Session getCurrentSession();

	CriteriaMC addOrderBy(CriteriaMC criteriaMC, String propriedade, boolean sortOrder);

	void remove(Class clazz, Serializable id) throws Exception;

	List paginate(int first, int pageSize, String sortField, boolean sortOrder, Map<String, String> filters, Class clazz) throws Exception;

	Long getCount(int first, int pageSize, String sortField, boolean sortOrder, Map<String, String> filters, Class clazz) throws Exception;
}
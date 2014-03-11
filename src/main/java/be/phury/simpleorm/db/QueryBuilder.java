package be.phury.simpleorm.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryBuilder<T> {
	private final String sqlTpl;
	private Map<String, Object> sqlParameters = new HashMap<String, Object>();
	
	public QueryBuilder(String sqlTpl) {
		this.sqlTpl = sqlTpl;
	}
	
	public QueryBuilder<T> setParameter(String name, Object value) {
		sqlParameters.put(name, value);
		return this;
	}


	public <U> List<U> getResultList() {
		String sql = sqlTpl;
		for (String name : sqlParameters.keySet()) {
		}
		return null;
	}
}
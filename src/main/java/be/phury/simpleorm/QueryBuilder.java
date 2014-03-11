package be.phury.simpleorm;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.phury.simpleorm.db.DatabaseManager;
import be.phury.simpleorm.db.EntityMapper;
import be.phury.simpleorm.db.SqlBuilder;
import be.phury.simpleorm.db.DatabaseManager.PreparedStatementTemplate;

public class QueryBuilder<T> {
	private final SqlBuilder sqlBuilder;
	private final EntityMapper entityMapper;
	private final DatabaseManager databaseManager;
	
	private final Class<T> typeOf;
	private final Map<String, Object> clauses = new HashMap<String, Object>();
	private final char queryType;
	
	protected QueryBuilder(char queryType, Class<T> typeOf, DatabaseManager databaseManager, EntityMapper entityMapper, SqlBuilder sqlBuilder) {
		this.queryType = queryType;
		this.typeOf = typeOf;
		this.databaseManager = databaseManager;
		this.entityMapper = entityMapper;
		this.sqlBuilder = sqlBuilder;
	}
	
	public QueryBuilder<T> and(String field, Object value) {
		clauses.put(field, value);
		return this;
	}
	
	public List<T> getResultList() {
		switch (queryType) {
		case 's':
			String sql = sqlBuilder.select(typeOf, new ArrayList<String>(clauses.keySet()));
			return databaseManager.executeInPrepareStatementWithResult(sql, new PreparedStatementTemplate<List<T>>() {
				@Override
				public List<T> execute(PreparedStatement stmt) throws SQLException {
					return entityMapper.toEntityList(stmt.executeQuery(), typeOf);
				}
			});
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	public T getSingleResut() {
		switch (queryType) {
		case 's':
			String sql = sqlBuilder.select(typeOf, new ArrayList<String>(clauses.keySet()));
			return databaseManager.executeInPrepareStatementWithResult(sql, new PreparedStatementTemplate<T>() {
				@Override
				public T execute(PreparedStatement stmt) throws SQLException {
					return entityMapper.toEntity(stmt.executeQuery(), typeOf);
				}
			});
		default:
			throw new UnsupportedOperationException();
		}
	}
}
package be.phury.simpleorm;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.phury.simpleorm.db.DatabaseManager;
import be.phury.simpleorm.db.EntityMapper;
import be.phury.simpleorm.db.SqlBuilder;
import be.phury.simpleorm.db.DatabaseManager.PreparedStatementTemplate;
import be.phury.simpleorm.refelect.ReflectUtils;

public abstract class EntityDao {

	private final Class<?> typeOf;
	private DatabaseManager databaseManager;
	private SqlBuilder sqlBuilder;
	private EntityMapper entityMapper;
	
	public EntityDao(Class<?> typeOf) {
		this.typeOf = typeOf;
	}
	
	public <T> T findById(final Integer id) {
		return databaseManager.executeInPrepareStatementWithResult(
				sqlBuilder.select(typeOf, ReflectUtils.getIdFieldWithError(typeOf).getName()),
				new PreparedStatementTemplate<T>() {
					@Override @SuppressWarnings("unchecked")
					public T execute(PreparedStatement stmt) throws SQLException {
						stmt.setInt(1, id);
						return (T) entityMapper.toEntity(stmt.executeQuery(), typeOf);
					}
				});
	}

	protected <T> List<T> find(Object ...clauses) {
		final List<String> fieldNames = new ArrayList<String>(clauses.length/2);
		final List<Object> fieldValues = new ArrayList<Object>(clauses.length/2);
		for (int i = 0; i < clauses.length; i+=2) {
			fieldNames.add((String)clauses[i]);
			fieldValues.add(clauses[i+1]);
		}		
		return databaseManager.executeInPrepareStatementWithResult(
				// select * from BINARY_STORE_DATA where BD_ID = ?
				sqlBuilder.select(typeOf, fieldNames),
				new PreparedStatementTemplate<List<T>>() {
					@Override @SuppressWarnings("unchecked")
					public List<T> execute(PreparedStatement stmt) throws SQLException {
						entityMapper.fromFieldsInEntity(stmt, typeOf, fieldNames, fieldValues);
						return (List<T>) entityMapper.toEntityList(stmt.executeQuery(), typeOf);
					}
				});
	}
	
	public <T> T save(final T entity) {
		final Integer result = databaseManager.executeInPrepareStatementWithResult(
				sqlBuilder.insert(typeOf),
				new PreparedStatementTemplate<Integer>() {
					@Override
					public Integer execute(PreparedStatement stmt) throws SQLException {
						ReflectUtils.setIdValueWithError(entity, nextId());
						entityMapper.fromEntity(stmt, entity);
						return stmt.executeUpdate();
					}
				});
		if (result != 1) {
			throw new RuntimeException("Unable to save entity " + entity);
		}
		return entity;
	}
	
	public <T> T update(final T toUpdate) {
		final Integer id = ReflectUtils.getIdValueWithError(toUpdate);
		final T original = findById(id);
		if (original == null) {
			throw new IllegalArgumentException(typeOf + " with id " + id + " does not exist in database");
		}
		final Integer result = databaseManager.executeInPrepareStatementWithResult(
				sqlBuilder.update(original, toUpdate),
				new PreparedStatementTemplate<Integer>() {
					@Override
					public Integer execute(PreparedStatement stmt) throws SQLException {
						entityMapper.fromEntity(stmt, original, toUpdate);
						return stmt.executeUpdate();
					}
				});
		if (result != 1) {
			throw new RuntimeException("Unable to update entity " + toUpdate);
		}
		return toUpdate;
	}
	
	public <T> T delete(final Integer id) {
		final T entity = findById(id);
		final Integer result = databaseManager.executeInPrepareStatementWithResult(
				sqlBuilder.delete(typeOf, ReflectUtils.getIdFieldWithError(typeOf).getName()),
				new PreparedStatementTemplate<Integer>() {
					@Override
					public Integer execute(PreparedStatement stmt) throws SQLException {
						stmt.setInt(1, id);
						return stmt.executeUpdate();
					}
				});
		if (result != 1) {
			throw new RuntimeException("Unable to delete entity " + entity);
		}
		return entity;
	}
	
	protected DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	protected EntityMapper getEntityMapper() {
		return entityMapper;
	}
	
	protected SqlBuilder getSqlBuilder() {
		return sqlBuilder;
	}
	
	private Integer nextId() {
		return Math.abs(new Long(System.currentTimeMillis()).intValue());
	}

}

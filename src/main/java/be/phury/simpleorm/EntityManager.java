package be.phury.simpleorm;

import be.phury.simpleorm.db.DatabaseManager;
import be.phury.simpleorm.db.EntityMapper;
import be.phury.simpleorm.db.SqlBuilder;

public class EntityManager {

//	getEntityManager()
//		.selectOn(Document.class)
//		.and("id", documentId)
//		.limit(5)
//		.getResultList();
	
	private final SqlBuilder sqlBuilder;
	private final EntityMapper entityMapper;
	private final DatabaseManager databaseManager;
	
	public EntityManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
		this.entityMapper = new EntityMapper();
		this.sqlBuilder = new SqlBuilder();
	}
	
	public <T> QueryBuilder<T> selectOn(Class<T> typeOf) {
		return new QueryBuilder<T>('s', typeOf, databaseManager, entityMapper, sqlBuilder);
	}
}

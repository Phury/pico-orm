package be.phury.simpleorm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

	private DatabaseConfiguration config; 
	
	public DatabaseManager(DatabaseConfiguration config) {
		 try {
			Class.forName(config.getDriver());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		this.config = config;
	}
	
	private DatabaseConfiguration getConfig() {
		return config;
	}
	
	private Connection getConnection() {
		try {
			return DriverManager.getConnection(
					getConfig().getUrl(), 
					getConfig().getUsername(),
					getConfig().getPassword());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T executeInPrepareStatementWithResult(final String sql, final PreparedStatementTemplate<T> tpl) {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			T result = tpl.execute(stmt);
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// ignore
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
	}

	public interface StatementTemplate {
		void execute(Statement stmt) throws SQLException;
	}
	
	public interface PreparedStatementTemplate<T> {
		T execute(PreparedStatement stmt) throws SQLException;
	}

	public <T> QueryBuilder<T> createQuery(String sql) {
		return new QueryBuilder<T>(sql);
	}
}

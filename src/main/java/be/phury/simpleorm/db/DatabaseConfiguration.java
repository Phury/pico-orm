package be.phury.simpleorm.db;

public class DatabaseConfiguration {
	private String username;
	private String password;
	private String driver;
	private String url;
	public String getUsername() {
		return username;
	}
	public DatabaseConfiguration setUsername(String username) {
		this.username = username;
		return this;
	}
	public String getPassword() {
		return password;
	}
	public DatabaseConfiguration setPassword(String password) {
		this.password = password;
		return this;
	}
	public String getDriver() {
		return driver;
	}
	public DatabaseConfiguration setDriver(String driver) {
		this.driver = driver;
		return this;
	}
	public String getUrl() {
		return url;
	}
	public DatabaseConfiguration setUrl(String url) {
		this.url = url;
		return this;
	}
}
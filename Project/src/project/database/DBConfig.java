package project.database;

/**
 * A convenience class to store information from the database configuration file.
 *
 */
public class DBConfig {

	private String username;
	private String password;
	private String db;
	private String host;
	private String port;
	
	
	/**
	 * Constructor.
	 * @param username
	 * @param password
	 * @param db
	 * @param host
	 * @param port
	 */
	public DBConfig() {
		super();
		this.username = "user20";
		this.password = "user20";
		this.db = "user20";
		this.host = "sql.cs.usfca.edu";
//		this.host = "127.0.0.1";
		this.port = "3306";
	}
	
	
	/**
	 * Constructor.
	 * @param username
	 * @param password
	 * @param db
	 * @param host
	 * @param port
	 */
	public DBConfig(String username, String password, String db, String host,
			String port) {
		super();
		this.username = username;
		this.password = password;
		this.db = db;
		this.host = host;
		this.port = port;
	}

	/**
	 * Return username
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Return password
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Return database.
	 * @return
	 */
	public String getDb() {
		return db;
	}

	/**
	 * Return host.
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Return port.
	 * @return
	 */
	public String getPort() {
		return port;
	}	
}


package project.database;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import project.web.DBSong;
import project.web.LastFMClient;

import java.sql.PreparedStatement;

/**
 * A class that provides static method to access a relational database.
 * Relies on the database drivers here: https://dev.mysql.com/downloads/connector/j/
 *
 */
public class DBHelper {
	

	/**
	 * Creates a table called userInfo in the database specified by the configuration information.
	 * The table must have three columns:
	 * name - should be a 100 character string 
	 * userName - a 100 character string that cannot be null and is the primary key
	 * password - should be a 100 character string 
	 * 
	 * @param dbconfig
	 * @throws SQLException
	 */
	public static void createUserInfoTable(DBConfig dbconfig) throws SQLException {		
		Connection dbConnection = getConnection(dbconfig);
		
		if(!tableExists(dbConnection, "userInfo")){
			String selectStmt = "CREATE TABLE userInfo " +
	                   "(name VARCHAR(100) not null, " +
	                   "userName VARCHAR(100) not null, " + 
	                   "password VARCHAR(100) not null) "; 
			
			PreparedStatement statement = dbConnection.prepareStatement(selectStmt);
			
			statement.executeUpdate(selectStmt);
		}
		

		dbConnection.close();
	}
	
	
	
	/**
	 * Creates a table called favorites in the database specified by the configuration information.
	 * The table must have two columns:
	 * name - should be a 100 character string
	 * trackId - should be a 100 character string
	 * 
	 * @param dbconfig
	 * @throws SQLException
	 */
	public static void createFavoritesTable(DBConfig dbconfig) throws SQLException {		
		Connection dbConnection = getConnection(dbconfig);
		
		if(!tableExists(dbConnection, "favorites"))
		{
			String selectStmt = "CREATE TABLE favorites " +
	                   "(userName VARCHAR(100) not null, " +
	                   "artist VARCHAR(100) not null, " +
	                   "title VARCHAR(100) not null, " +
	                   " trackId VARCHAR(100) not null) ";
			
			PreparedStatement statement = dbConnection.prepareStatement(selectStmt);
			
			statement.executeUpdate(selectStmt);
		}
		

		dbConnection.close();
	}

	
	/**
	 * A helper method that returns a database connection.
	 * A calling method is responsible for closing the connection when finished.
	 * @param dbconfig
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(DBConfig dbconfig) throws SQLException {

		String username  = dbconfig.getUsername();
		String password  = dbconfig.getPassword();
		String db  = dbconfig.getDb();

		try {
			// load driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception e) {
			System.err.println("Can't find driver");
			System.exit(1);
		}

		// format "jdbc:mysql://[hostname][:port]/[dbname]"
		//note: if connecting through an ssh tunnel make sure to use 127.0.0.1 and
		//also to that the ports are set up correctly
		String host = dbconfig.getHost();
		String port = dbconfig.getPort();
		String urlString = "jdbc:mysql://" + host + ":" + port + "/"+db +"?useUnicode=true&characterEncoding=UTF-8";
		Connection con = DriverManager.getConnection(urlString,
				username,
				password);

		return con;
	}


	/**
	 * Helper method that determines whether a table exists in the database.
	 * @param con
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	private static boolean tableExists(Connection con, String table) throws SQLException {

		DatabaseMetaData metadata = con.getMetaData();
		ResultSet resultSet;
		resultSet = metadata.getTables(null, null, table, null);

		if(resultSet.next()) {
			// Table exists
			return true;
		}		
		return false;
	}
	
	/**
	 * Adds the name, userName, and password into the userInfo table.
	 * 
	 * @param name
	 * @param userName
	 * @param password
	 * @param dbconfig
	 */
	public static void addToUserInfoTable(String name, String userName, String password, DBConfig dbConfig) {
		
		try {
			Connection connection = getConnection(dbConfig);


			if(!tableExists(connection, "userInfo")) {
				createUserInfoTable(dbConfig);
			}

			PreparedStatement addStmt = connection.prepareStatement("INSERT INTO userInfo "+
					"(name, userName, password) VALUES (?, ?, ?);");
			
			addStmt.setString(1, name);
			addStmt.setString(2, userName);
			addStmt.setString(3, password);
			
			addStmt.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			System.err.println("SQLException error in addToUserInfoTable");
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Adds the userName and trackId to the favorites table.
	 * 
	 * @param userName
	 * @param trackId
	 * @param dbconfig
	 */
	public static void addToFavoritesTable(String userName, String trackId, String artist, String title, DBConfig dbConfig) {
		
		try {
			Connection connection = getConnection(dbConfig);

			if(!tableExists(connection, "favorites")) {
				createFavoritesTable(dbConfig);
			}

			PreparedStatement addStmt = connection.prepareStatement("INSERT INTO favorites "+
					"(userName, artist, title, trackId) VALUES (?, ?, ?, ?)");
			
			addStmt.setString(1, userName);
			addStmt.setString(2, artist);
			addStmt.setString(3, title);
			addStmt.setString(4, trackId);
			
			addStmt.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			System.err.println("SQLException error in addToFavoritesTable");
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Checks the database to see if the user exists
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 * @param inputPassword
	 */
	public static boolean verifyUser(DBConfig dbconfig, String inputUserName, String inputPassword) throws SQLException {

		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			
			if(tableExists(con, "userInfo"))
			{
				Statement stmt = con.createStatement();
				String sqlStatement = "SELECT * FROM userInfo WHERE userName = '"+
						 inputUserName + "' AND password = '"+ inputPassword + "'";
				ResultSet users = stmt.executeQuery(sqlStatement);
				if(users.next()) {
					return true;
				}
			}
			
			return false;
		} finally {
			con.close();
		}		
	}
	
	
	/**
	 * Checks the database to see if the userName exists
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static boolean checkUserNameTaken(DBConfig dbconfig, String inputUserName) throws SQLException {
		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			Statement stmt = con.createStatement();
			
			if(tableExists(con, "userInfo")){
				ResultSet users = stmt.executeQuery("SELECT * FROM userInfo WHERE userName = '"+inputUserName+"'");
				if(users.next()) {
					return true;
				}

			}

			return false;
		} finally {
			con.close();
		} 

	}
	
	/**
	 * Checks the database to see if the password is correct
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static boolean verifyPassword(DBConfig dbconfig,String inputUserName, String inputPassword) throws SQLException {
		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			Statement stmt = con.createStatement();
			
			if(tableExists(con, "userInfo")){
				String statement = "SELECT * FROM userInfo WHERE userName = '"+inputUserName +
						"' AND password = '"+ inputPassword+"'";
				ResultSet users = stmt.executeQuery(statement);
				if(users != null)
				{
					while(users.next()) {
						String userName = users.getString("userName");
						String password = users.getString("password");
						if(password.equals(inputPassword) && userName.equals(inputUserName)){
							return true;
						}
					}
				}
			}

			return false;
		} finally {
			con.close();
		} 

	}
	
	
	/**
	 * Checks the database to see if the password is correct
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static boolean checkIfFavorited(DBConfig dbconfig,String inputUserName, String inputTrackId) throws SQLException {
		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			Statement stmt = con.createStatement();
			
			if(tableExists(con, "favorites")){
				ResultSet users = stmt.executeQuery("SELECT * FROM favorites WHERE userName = '"+inputUserName +
						"' AND trackId = '"+ inputTrackId+"'");
				while(users.next()) {
					String userName = users.getString("userName");
					String trackId = users.getString("trackId");
					if(trackId.equals(inputTrackId) && userName.equals(inputUserName)){
						return true;
					}
				}
			}

			return false;
		} finally {
			con.close();
		} 

	}
	
	
	/**
	 * Gets the favorited songs for the specified user from the database
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static ArrayList<ArrayList<String>> getFavorites(DBConfig dbconfig,String inputUserName) throws SQLException {
		ArrayList<ArrayList<String>> favorites = new ArrayList<ArrayList<String>>();
		
		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			Statement stmt = con.createStatement();
			
			if(tableExists(con, "favorites")){
				ResultSet users = stmt.executeQuery("SELECT * FROM favorites WHERE userName = '"+inputUserName +"'");

				while(users.next()) {
					String artist = users.getString("artist");
					String title = users.getString("title");
					ArrayList<String> tmp = new ArrayList<>();
					tmp.add(artist);
					tmp.add(title);
					favorites.add(tmp);
				}
			}

			return favorites;
		} finally {
			con.close();
		} 
	}
	
	/**
	 * Checks the database to see if the password is correct
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static String getName(DBConfig dbconfig,String inputUserName, String inputPassword) throws SQLException {
		String name = null;
		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			Statement stmt = con.createStatement();
			
			if(tableExists(con, "userInfo")){
				ResultSet users = stmt.executeQuery("SELECT * FROM userInfo WHERE userName = '"+inputUserName +
						"' AND password = '"+ inputPassword+"'");
				while(users.next()) {
					name = users.getString("name");
				}
			}

			return name;
		} finally {
			con.close();
		} 
	}
	
	/**
	 * If the artist table exists in the database, adds the song to the table.
	 * 
	 * @param dbconfig
	 * @param tables
	 * @throws SQLException
	 */
	public static void buildArtistInfoTable(DBConfig dbConfig, DBSong song) {
		
		try {
			Connection connection = getConnection(dbConfig);


			if(!tableExists(connection, "artistInfo")) {
				createArtistInfoTable(dbConfig);
			}

			PreparedStatement addStmt = connection.prepareStatement("INSERT INTO artistInfo "+
					"(name, listeners, playcount, bio) VALUES (?, ?, ?, ?)");
			
			addStmt.setString(1, song.getArtistName());
			addStmt.setInt(2, song.getNumberOfListeners());
			addStmt.setInt(3, song.getPlayCount());
			addStmt.setString(4, song.getBio());
			
			addStmt.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			System.err.println("SQLException error in addToArtistInfoTable");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Creates a table called artist in the database specified by the configuration information.
	 * The table must have four columns:
	 * name - should be a 100 character string that cannot be null and is the primary key
	 * listeners - an integer
	 * playcount - an integer
	 * bio - a long text string
	 * 
	 * @param dbconfig
	 * @throws SQLException
	 */
	public static void createArtistInfoTable(DBConfig dbconfig) {		

		try {
			Connection dbConnection = getConnection(dbconfig);

			String selectStmt = "CREATE TABLE artistInfo " +
					"(name VARCHAR(250) not NULL primary key, " +
					" listeners INTEGER, " + 
					" playcount INTEGER, " + 
					" bio LONGTEXT)"; 

			PreparedStatement statement;

			statement = dbConnection.prepareStatement(selectStmt);

			statement.executeUpdate(selectStmt);

			dbConnection.close();
		} catch (SQLException e) {
			System.err.println("Error in createArtistInfoTable");
			e.printStackTrace();
		}
	}
	

	/**
	 * Creates table with artist info for every artist in library
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 * @param inputPassword
	 */
	public static DBSong getArtistInfo(DBConfig dbconfig, String artist, TreeSet<String> artistsSet) {

		String name = null;
		int listeners = 0;
		int playCount = 0;
		String bio = null;
		ArrayList<String> artistList = new ArrayList<String>();
		Iterator iterator = artistsSet.iterator();
		
		while(iterator.hasNext()){
			artistList.add(iterator.next().toString());
		}
		
		Connection con = null;
		
		try {
			con = DBHelper.getConnection(dbconfig);
			
			if(!tableExists(con, "artistInfo")){
				createArtistInfoTable(dbconfig);
				LastFMClient.fetchAndStoreArtists(artistList, dbconfig);
			}
			

			Statement stmt = con.createStatement();
			String sqlStatement = "SELECT * FROM artistInfo WHERE name ='"+artist+"'";
			ResultSet artists = stmt.executeQuery(sqlStatement);
			while(artists.next()) {
				name = artists.getString("name");
				listeners = artists.getInt("listeners");
				playCount = artists.getInt("playcount");
				bio = artists.getString("bio");
			}
		} catch (SQLException e) {
			System.err.println("Error in getting info from artistInfo table");
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		DBSong artistInfo = new DBSong(name, listeners, playCount, bio);
		
		return artistInfo;
	}
	
	
	/**
	 * Gets the favorited songs for the specified user from the database
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static ArrayList<String> getSortedPlayCount(DBConfig dbconfig) {
		ArrayList<String> playCount = new ArrayList<String>();
		
		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			Statement stmt = con.createStatement();
			
			if(tableExists(con, "artistInfo")){
				ResultSet users = stmt.executeQuery("SELECT name, playcount FROM artistInfo ORDER BY playcount DESC");

				while(users.next()) {
					String artist = users.getString("name");
					playCount.add(artist);
				}
			}
			con.close();
		} catch (SQLException e) {
			System.err.println("Error in get sorted play count");
			e.printStackTrace();
		} 
		return playCount;
	}
	
	
	/**
	 * Creates a table called artist in the database specified by the configuration information.
	 * The table must have four columns:
	 * name - should be a 100 character string that cannot be null and is the primary key
	 * listeners - an integer
	 * playcount - an integer
	 * bio - a long text string
	 * 
	 * @param dbconfig
	 * @throws SQLException
	 */
	public static void createSearchHistoryTable(DBConfig dbconfig) {		

		try {
			Connection dbConnection = getConnection(dbconfig);

			String selectStmt = "CREATE TABLE searchHistory " +
					"(name VARCHAR(250) not NULL, " +
					" tag VARCHAR(100), " + 
					" query VARCHAR(250))"; 

			PreparedStatement statement;

			statement = dbConnection.prepareStatement(selectStmt);

			statement.executeUpdate(selectStmt);

			dbConnection.close();
		} catch (SQLException e) {
			System.err.println("Error in createSearchHistoryTable");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Adds the userName and trackId to the favorites table.
	 * 
	 * @param userName
	 * @param trackId
	 * @param dbconfig
	 */
	public static void addToSearchHistoryTable(String userName, String tag, String query, DBConfig dbConfig) {
		
		try {
			Connection connection = getConnection(dbConfig);

			if(!tableExists(connection, "searchHistory")) {
				createSearchHistoryTable(dbConfig);
			}

			PreparedStatement addStmt = connection.prepareStatement("INSERT INTO searchHistory "+
					"(name, tag, query) VALUES (?, ?, ?)");
			
			addStmt.setString(1, userName);
			addStmt.setString(2, tag);
			addStmt.setString(3, query);
			
			addStmt.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			System.err.println("SQLException error in addToSearchHistoryTable");
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Gets the favorited songs for the specified user from the database
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static ArrayList<ArrayList<String>> getSearchHistory(DBConfig dbconfig,String inputUserName) throws SQLException {
		ArrayList<ArrayList<String>> searchHistory = new ArrayList<ArrayList<String>>();
		
		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			Statement stmt = con.createStatement();
			
			if(tableExists(con, "searchHistory")){
				ResultSet users = stmt.executeQuery("SELECT * FROM searchHistory WHERE name = '"+inputUserName +"'");

				while(users.next()) {
					String tag = users.getString("tag");
					String query = users.getString("query");
					ArrayList<String> tmp = new ArrayList<>();
					tmp.add(tag);
					tmp.add(query);
					searchHistory.add(tmp);
				}
			}

			return searchHistory;
		} finally {
			con.close();
		} 
	}
	
	
	/**
	 * Gets the favorited songs for the specified user from the database
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static Boolean clearSearchHistory(DBConfig dbconfig,String inputUserName) {
		
		Connection con = null;
		try {
			con = DBHelper.getConnection(dbconfig);
			Statement stmt = con.createStatement();
			
			if(tableExists(con, "searchHistory")){
				boolean users = stmt.execute("DELETE FROM searchHistory WHERE name = '"+inputUserName +"'");

				return users;
			}

			con.close();
		} catch (SQLException e) {
			System.err.println("Error in clear search history");
			e.printStackTrace();
		} 
		return false;
	}
	
	
}

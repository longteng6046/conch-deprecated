package conch2.sql;

import java.sql.*;
//import com.mysql.jdbc.*;

/**
 * 
 * Provides static methods to check and return database connectivity.
 *
 */
public class MySQLUtil {
	static String jdbcDriver = "com.mysql.jdbc.Driver";
	
	/**
	 * Connect to the database server according to the given configuration
	 * information. As long as connection returned successfully, consider
	 * the database is ready.
	 * <p>
	 * 
	 * @return true/false
	 * 
	 */
	static public boolean dbServerCheck(String cfgFilePath) {
		/* check JDBC driver */
		try {
			Class.forName(jdbcDriver);
		} catch (Exception e) {
			System.err.println("Cannot find JDBC driver.");
			System.exit(-1);
		}


		Connection conn;
		/* check connection */

		conn = MySQLUtil.connect(cfgFilePath);
		if (conn != null)
			return true;

		/*try{
			conn = MySQLUtil.connect(cfgFilePath);
			if (conn != null){
				System.out.println("Database connection established.");
				Statement s = conn.createStatement();
				s.execute("SELECT * from testOnly;");
				int size = 0;
				ResultSet result = s.getResultSet();
				while (result.next())
					size ++;
				if (size != 0){
					System.out.println("Database working correctly.");
					return true;
				}
			}
			
		}
		catch(SQLException e){
			System.err.println("Error in connecting to the SQL server");
			return false;
		}*/
		return false; // logically unreachable code
	}
	


	/**
	 * Connect to the database using the configuration file, and return the connection.
	 * <p>
	 * @param cfgFilePath path to the database configuration file
	 * @return a Connection instance, or null if connection failed.
	 */
	static public Connection connect(String cfgFilePath) {
		DBConfiguration conf = new DBConfiguration(cfgFilePath);

		try{
			Class.forName(jdbcDriver);
			String url = "jdbc:mysql://" + conf.host + ":" + conf.port + "/" + conf.dbname;

			Connection conn = null;
			//System.out.println(url);
			
			conn = DriverManager.getConnection(url, conf.username, conf.password);
			//System.out.println(conn.toString());
			return conn;
		}
		catch (SQLException e) {
			System.err.println("Fail connecting to the MySQL database");
		} catch (ClassNotFoundException e) {
			System.err.println("driver not found.");
		}
		return null;
	}

	
}

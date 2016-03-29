package conch2.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class DBConfiguration {

	public String host = null;
	public String port = null;
	public String username = null;
	public String password = null;
	public String dbname = null;
	public String socket = null;
	
	/**
	 * Reads in a text file containing all data needed to connect to a MySQL
	 * database, and set the values accordingly.
	 * <p> 
	 * @param cfgFilePath the path to the configuration file
	 */
	public DBConfiguration(String cfgFilePath) {
		try {
			BufferedReader buff = new BufferedReader(new FileReader(
					cfgFilePath));
			String inLine;
			while ((inLine = buff.readLine()) != null) {
				inLine = inLine.trim();
				if (inLine.startsWith("#"))
					continue;

				String head = inLine.split("=")[0].trim();
				String tail = inLine.split("=")[1].trim();
				if (head.compareTo("host") == 0)
					host = tail;
				else if (head.compareTo("port") == 0)
					port = tail;
				else if (head.compareTo("user") == 0)
					username = tail;
				else if (head.compareTo("passwd") == 0)
					password = tail;
				else if (head.compareTo("db") == 0)
					dbname = tail;
				else if (head.compareTo("unix_socket") == 0)
					socket = tail;
				else {
					System.err
							.println("Unrecognized filed in database configuration file...");
					continue;
				}

			}
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("IO error when reading the database configuration file.");
			System.err.println(cfgFilePath);
			System.exit(-1);
		}
	}
}
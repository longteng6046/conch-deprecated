package conch2.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HelloBlob {

	public static void main(String[] args) {
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + "localhost:3306" + "/" + "tmpBlob";

			Connection conn = null;
			System.out.println(url);
			
			conn = DriverManager.getConnection(url, "ripper", "ripper");
			//System.out.println(conn.toString());
			
			FileInputStream fis = new FileInputStream("./workflow/Conch Data Reposotory Working Logic.xmind");
			
			PreparedStatement pstmt = conn.prepareStatement("insert into test(id,obj) values(1,?)");
			pstmt.setBinaryStream(1, fis);
			pstmt.execute();
		}
		catch (SQLException e) {
			System.err.println("Fail connecting to the MySQL database");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("driver not found.");
		} catch (IOException e) {
			System.err.println("IO exceptions.");
			e.printStackTrace();
		}
	}
}



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDatabase {
	
	String db = "atm";
	String url = "jdbc:mysql://localhost:3306/" + db;
	String user = "root";
	String mdp = "mysql2024pwd";
	
	Connection connexiondb() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try {
				Connection conn = DriverManager.getConnection(url, user, mdp);
				return conn;
			} catch (SQLException e) {
				return null;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}

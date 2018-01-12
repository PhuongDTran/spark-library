package app.util;

import java.sql.*;

//connect to database
public class connectDb {

	private static Connection connection=null;

	public static Connection getConnection() throws Exception
	{
		if (connection != null) return connection;

		return getConnection("library","library","S2duyphuong!");
	}

	private static Connection getConnection(String dbName, String userName, String password) throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName,userName,password);
		return connection;

	}
}

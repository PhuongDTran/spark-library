package app.user;


import java.util.*;
import java.util.stream.*;

import app.util.connectDb;

import java.sql.*;

public class UserDao{

	private Statement connect() throws Exception
	{
		Connection connection = connectDb.getConnection();
		Statement statement = connection.createStatement();
		
		return statement;

	}
	
	public User getUserByUsername(String username) throws Exception {

		Statement statement = connect();
		
		ResultSet rs = statement.executeQuery("SELECT * FROM user WHERE username=" + "'" + username + "'");
		if (rs.next())
		{
			String salt = rs.getString(2);
			String hashedPassword = rs.getString(3);
			return new User(username, salt,hashedPassword);
		}
		return null;

		//return users.stream().filter(b -> b.getUsername().equals(username)).findFirst().orElse(null);
	}

	public ArrayList<String> getAllUserNames() throws Exception {

		Statement statement = connect();

		ResultSet rs = statement.executeQuery("SELECT username FROM user");

		ArrayList<String> usernames = new ArrayList<String>();

		while (rs.next())
			usernames.add(rs.getString(1));

		rs.close();

		return usernames;

		//return users.stream().map(User::getUsername).collect(Collectors.toList());
	}


	// *****written by Phuong Tran**********


	// add new user to Users list
	public void setUser(String username, String salt, String hashedPassword)  throws Exception
	{
		Statement statement = connect();

		String query = String.format("INSERT INTO user VALUES ('%s','%s','%s')",username,salt,hashedPassword);
		statement.executeUpdate(query);

	}

}

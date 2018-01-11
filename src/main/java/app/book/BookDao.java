package app.book;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


import app.util.*;

public class BookDao {
	public List<Book> getAllBooks() throws Exception {
		Connection conn = connectDb.getConnection();
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM book");
		List<Book> bookList = new ArrayList<Book>();
		while(rs.next())
			bookList.add(  new Book(rs.getString(1),rs.getString(2),rs.getString(3)) );
		conn.close();
		return bookList;
	}

	public Book getBookByIsbn(String isbn) throws Exception {
		Connection conn = connectDb.getConnection();
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM book WHERE isbn=" + "'" +isbn+ "'");
		if(rs.next())
		{
			Book book = new Book(rs.getString(1),rs.getString(2),rs.getString(3)); 
			conn.close();
			return book;
		}
		conn.close();
		return null;
	}

	public Book getRandomBook() throws Exception {
		Connection conn = connectDb.getConnection();
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM book ORDER BY RAND() LIMIT 1");
		rs.next();
		return new Book(rs.getString(1), rs.getString(2),rs.getString(3));
	}
}

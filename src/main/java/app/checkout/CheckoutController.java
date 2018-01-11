package app.checkout;

import app.book.*;
import app.login.LoginController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;

import app.util.Path;
import app.util.ViewUtil;
import app.util.connectDb;
import spark.*;

public class CheckoutController {

	/**
	 * render checkout page
	 */
	public static Route handleCheckoutPage = (Request request,Response response)->{
		LoginController.ensureUserIsLoggedIn(request, response);
		Map<String,Object> model = new HashMap<String,Object>();
		String currentUser = request.session().attribute("currentUser");
		List<Book> books = getUserBooks(currentUser);
		model.put("isListEmpty", books.isEmpty());
		model.put("books", books);
		return ViewUtil.render(request, model, Path.Template.CHECKOUT);
	};
	
	/**
	 * handle buttons in checkout page
	 */
	public static Route handleCheckoutButtons = (Request request, Response response)->{
		if ( LoginController.ensureUserIsLoggedIn(request, response) ) {
			Map<String,Object> model = new HashMap<String,Object>();
			String currentUser = request.session().attribute("currentUser");
			String buttonName = request.queryParams().iterator().next();
			if(Objects.equal(buttonName,"checkoutbutton")){
				setIsCheckoutToOne(currentUser);
				model.put("checkoutButtonClicked", true);
			}
			else{
				delete(buttonName);
			}
			List<Book> books = getUserBooks(currentUser);
			model.put("isListEmpty", books.isEmpty());
			model.put("books", books);
			return ViewUtil.render(request, model, Path.Template.CHECKOUT);
		}
		return null; //TODO
	};
	/**
	 * delete a book from checkout page
	 */
	private static void delete(String isbn) throws Exception
	{
		Connection conn = connectDb.getConnection();
		Statement statement = conn.createStatement();
		String sql = String.format("DELETE FROM checkout WHERE isbn='%s' AND is_checkout=0", isbn);
		statement.executeUpdate(sql);
		conn.close();
	}
	/**
	 * set is_checkout field to 1 in database
	 */
	private static void setIsCheckoutToOne( String currentUser) throws Exception
	{
		Connection  conn = connectDb.getConnection();
		String sql = String.format("UPDATE checkout SET is_checkout=1 WHERE username='%s' AND is_checkout=0", currentUser);
		Statement statement = conn.createStatement();
		statement.executeUpdate(sql);
		conn.close();
	}
	private static List<Book> getUserBooks (String currentUser) throws Exception
	{	
		Connection conn = connectDb.getConnection();
		Statement statement = conn.createStatement();
		String sql = String.format("SELECT book.title,book.author,book.isbn"
				+ " FROM checkout,book"
				+" WHERE checkout.isbn=book.isbn AND username='%s' AND is_checkout=0"
				+" ORDER BY book.title",currentUser);
		ResultSet rs = statement.executeQuery(sql);
		List<Book> bookList = new ArrayList<Book>();
		while (rs.next())
		{
			bookList.add( new Book(rs.getString(1),rs.getString(2),rs.getString(3)) );
		}
		conn.close();
		return bookList;
	}

}

package app.book;

import app.login.*;
import app.util.*;
import spark.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.*;


import static app.Application.bookDao;
import static app.util.JsonUtil.*;
import static app.util.RequestUtil.*;

public class BookController {
	
	public static Route fetchAllBooks = (Request request, Response response) -> {
		LoginController.ensureUserIsLoggedIn(request, response);
		if (clientAcceptsHtml(request)) {
			HashMap<String, Object> model = new HashMap<>();
			model.put("books", bookDao.getAllBooks());
			return ViewUtil.render(request, model, Path.Template.BOOKS_ALL);
		}
		if (clientAcceptsJson(request)) {
			return dataToJson(bookDao.getAllBooks());
		}
		return ViewUtil.notAcceptable.handle(request, response);
	};

	public static Route fetchOneBook = (Request request, Response response) -> {
		//LoginController.ensureUserIsLoggedIn(request, response);//TODO
		if (clientAcceptsHtml(request)) {
			HashMap<String, Object> model = new HashMap<>();
			Book book = bookDao.getBookByIsbn(getParamIsbn(request));
			model.put("book", book);
			return ViewUtil.render(request, model, Path.Template.BOOKS_ONE);
		}
		if (clientAcceptsJson(request)) {
			return dataToJson(bookDao.getBookByIsbn(getParamIsbn(request)));
		}
		return ViewUtil.notAcceptable.handle(request, response);
	};
	/**
	 * add  books to cart
	 * redirect to Books page after added
	 */
	public static Route addBook = (Request request, Response response) ->{
		if(LoginController.ensureUserIsLoggedIn(request, response)){
			HashMap<String, Object> model = new HashMap<>();
			Connection conn = connectDb.getConnection();
			Statement statement = conn.createStatement();
			String currentUser = request.session().attribute("currentUser");
			String isbn = request.queryParams("isbn");
			String sql = String.format("INSERT INTO checkout(username,isbn,is_checkout) VALUES ('%s','%s',0)",currentUser, isbn);
			statement.executeUpdate(sql);
			conn.close();
			model.put("books", bookDao.getAllBooks());
			return ViewUtil.render(request, model, Path.Template.BOOKS_ALL);
		}
		return null;
	};
}

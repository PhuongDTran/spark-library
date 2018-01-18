package app.login;

import app.user.*;
import app.util.*;
import spark.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import static app.util.RequestUtil.*;

public class LoginController {

	public static Route serveLoginPage = (Request request, Response response) -> {
		Map<String, Object> model = new HashMap<>();
		model.put("loggedOut", removeSessionAttrLoggedOut(request));
		model.put("loginRedirect", removeSessionAttrLoginRedirect(request));
		return ViewUtil.render(request, model, Path.Template.LOGIN);
	};

	public static Route handleLoginPost = (Request request, Response response) -> {
		Map<String, Object> model = new HashMap<>();
		if (!UserController.authenticate(getQueryUsername(request), getQueryPassword(request))) {
			model.put("authenticationFailed", true);
			return ViewUtil.render(request, model, Path.Template.LOGIN);
		}
		model.put("authenticationSucceeded", true);
		request.session().attribute("currentUser", getQueryUsername(request));
		if (getQueryLoginRedirect(request) != null) {
			response.redirect(getQueryLoginRedirect(request));
		}
		else
		{
			response.redirect(Path.Web.BOOKS);
		}
		return ViewUtil.render(request, model, Path.Template.LOGIN);
	};

	public static Route handleLogoutPost = (Request request, Response response) -> {
		request.session().removeAttribute("currentUser");
		request.session().attribute("loggedOut", true);
		request.cookies().remove("currentUser");
		response.removeCookie("currentUser");
		// pass a cookie back to the client indicating logout
		//response.cookie("/", "loggedOut", "true", 3600, false);
		response.redirect(Path.Web.LOGIN);
		return null;
	};

	/**
     The origin of the request (request.pathInfo()) is saved in the session so
     the user can be redirected back after login
     @return true if logged in, false if not log in
	 * @throws Exception 
	 */
	public static boolean ensureUserIsLoggedIn(Request request, Response response) throws Exception {
		if (request.session().attribute("currentUser") == null) {
			// the current username/email may be passed to us in a cookie
			String userCookie = request.cookie("currentUser");
			if (userCookie != null && userCookie.length() > 0) {
				// save the username in a session variable with the same key name
				request.session().attribute("currentUser", userCookie);
				handleGoogleSignIn(userCookie);
				return true;
			} else {
				request.session().attribute("loginRedirect", request.pathInfo());
				response.redirect(Path.Web.LOGIN);
				return false;
			}
		}
		return true;
	};
	/**
	 * check if 'currentUser' is a gmail address.
	 * If does, add that email address to 'user' table in database if not exist in the table.
	 * @param currentUser
	 * @throws Exception 
	 */
	private static void handleGoogleSignIn(String currentUser) throws Exception
	{
		if(currentUser.contains("@gmail.com"))
		{
			Connection conn = connectDb.getConnection();
			Statement statement = conn.createStatement();
			String sql = String.format("SELECT * FROM user WHERE username='%s'", currentUser);
			ResultSet rs = statement.executeQuery(sql);
			if(!rs.next())
			{
				sql = String.format("INSERT INTO user(username) VALUES('%s')", currentUser);
				statement.executeUpdate(sql);
			}	
			conn.close();
		}
	}

}

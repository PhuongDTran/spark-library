package app.signup;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

import app.util.Path;
import app.util.ViewUtil;
import spark.*;
import app.user.*;

import static app.Application.userDao;// what is this?

public class SignupControler {

	public static Route handleSignupPage = (Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String, Object>();
		return ViewUtil.render(request, model, Path.Template.SIGNUP);
	};
	public static Route handleSignupPost = (Request request, Response response) -> {
		Map<String, Object> model = new HashMap<String,Object>();
		String username = request.queryParams("username");
		String enterPassword = request.queryParams("enterpassword");
		String reenterPassword = request.queryParams("reenterpassword");
		User user = userDao.getUserByUsername(username);
		if (user!=null)
		{
			model.put("newUserExist", true);
			return ViewUtil.render(request, model, Path.Template.SIGNUP);
		}
		else if(! Objects.equal(enterPassword, reenterPassword))
		{
			model.put("passwordNotMatch",true);
			return ViewUtil.render(request, model, Path.Template.SIGNUP);
		}
		else
		{
			UserController.addUser(username, enterPassword);
			model.put("success", true);
			response.redirect(Path.Web.LOGIN);
			return ViewUtil.render(request, model, Path.Template.SIGNUP);
		}
	};

}

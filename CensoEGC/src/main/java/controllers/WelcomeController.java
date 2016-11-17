package controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/welcome")
public class WelcomeController extends AbstractController {

	// Constructors -----------------------------------------------------------

	public WelcomeController() {
		super();
	}

	// Index ------------------------------------------------------------------

	@RequestMapping(value = "/index")
	public ModelAndView index(String token, HttpServletResponse response) {
		ModelAndView result;
		String username = "";
		String password = "";

		if (token != null && !token.equals("logout")) {

			username = token.split(":")[0];
			password = token.split(":")[1];

			response.reset();
			Cookie user = new Cookie("user", username);
			user.setMaxAge(9000000);
			user.setPath("/");
			response.addCookie(user);

			Cookie token_cookie = new Cookie("token", password);
			user.setMaxAge(9000000);
			user.setPath("/");
			response.addCookie(token_cookie);

		} else if (token != null && token.equals("logout")) {
			response.reset();
			Cookie user = new Cookie("user", "");
			user.setMaxAge(9000000);
			user.setPath("/");
			response.addCookie(user);

			Cookie token_cookie = new Cookie("token", "");
			user.setMaxAge(9000000);
			user.setPath("/");
			response.addCookie(token_cookie);
		}
		result = new ModelAndView("welcome/index");
		result.addObject("user", username);
		result.addObject("pass", password);
		result.addObject("prueba", "textoprueba");

		return result;
	}

}
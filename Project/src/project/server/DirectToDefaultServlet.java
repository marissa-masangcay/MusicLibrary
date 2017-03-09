package project.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DirectToDefaultServlet extends BaseServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//Redirects all invalid paths to login.
		response.sendRedirect(response.encodeRedirectURL("/login"));
		return;
	}

}

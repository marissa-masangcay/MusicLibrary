package project.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.database.DBConfig;
import project.database.DBHelper;

public class LoginServlet extends BaseServlet{
	
	/**
	 * GET /login returns a web page containing text boxes for the user to attempt to log in
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		
		if(session.getAttribute(USER_NAME) != null) {
			response.sendRedirect(response.encodeRedirectURL("/search"));
			return;
		}
		
		//Checks status to see if logged
		String status = getParameterValue(request, STATUS);
		
		boolean statusok = status != null && status.equals(ERROR)?false:true;
		boolean redirected = status != null && status.equals(NOT_LOGGED_IN)?true:false;	
		
		
		//craft the HTML response
		String responseHtmlHead = loginHeader();
		
		PrintWriter writer = prepareResponse(response);
		
		//if the user was redirected here as a result of an error
		if(!statusok) {
			writer.println("<h3><font color=\"red\">Invalid Request to Login</font></h3>");
		} else if(redirected) {
			writer.println("<h3><font color=\"red\">Log in first!</font></h3>");
		} 
		
		String responseHtmlAction = 
				"<form action=\"verifyUser\" method=\"post\">" + 
						" User Name: "+
						"<input type=\"text\" name=\"userName\">" +
						" Password: "+
						"<input type=\"password\" name=\"password\">" +
						"<input type=\"submit\" value=\"Submit\">" +
						"</form>" ;


		String newUserButton = newUserButton();
		
		String responseHtmlFoot = footer();

		String responseHtml = responseHtmlHead + responseHtmlAction + newUserButton + responseHtmlFoot;	
		
		writer.println(responseHtml);

	}
	
	
	/**
	 * POST /login returns a web page containing a list and a search box where a query may be entered.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//VerifyUser does not accept Post requests. Redirects to login with error status.
		response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
		return;
	}


	/*
	 * Return the html for new user button
	 */
	protected String newUserButton() {
		return "<p><center>"+
                "<form action=\"newUser\" method=\"get\">"+
				"<input type=\"submit\" name=\"newUserButton\" value=\"I'm a New User\"></input>"+
				"</p>";
	}

	protected String loginHeader() {
		return "<h2><center> Login </h2>" +
				"<p><center> Please log in to use the Search Finder.</p>";
	}


}

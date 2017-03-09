package project.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.database.DBConfig;
import project.database.DBHelper;

public class NewUserServlet extends BaseServlet{

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		String status = getParameterValue(request, STATUS);
		
		boolean statusok = status != null && status.equals(ERROR)?false:true;
		boolean redirected = status != null && status.equals(NOT_LOGGED_IN)?true:false;	
		
		PrintWriter writer = prepareResponse(response);
		
		if(!statusok) {
			writer.println("<h3><font color=\"red\">Invalid Request to Login</font></h3>");
		} else if(redirected) {
			writer.println("<h3><font color=\"red\">Log in first!</font></h3>");
		} 
		
		writer.write("<center><font size=\"6\"><b>Song Finder</b></font>");
		writer.write("<html><head><title> New User Registration </title></head><body>");
		writer.write("<br>Welcome to song finder! Please enter your information to register.<br/><hr>");
		writer.write(backToLoginButton());
		
		String responseHtmlAction = 
				"<form name=\"name\" action=\"newUser\" method=\"post\">" + 
						" Full Name: "+
						"<input type=\"text\" name=\"name\">" +
						" User Name: "+
						"<input type=\"text\" name=\"userName\">" +
						" Password: "+
						"<input type=\"password\" name=\"password\">" +
						" Verify Password: "+
						"<input type=\"password\" name=\"verifyPassword\">" +
						"<input type=\"submit\" value=\"Submit\">" +
						"</form>" ;
		
		writer.write(responseHtmlAction);
		writer.write(footer());
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String name = request.getParameter("name");
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String verifyPassword = request.getParameter("verifyPassword");
		

		if(name == null || name.trim().equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/newUser?" + STATUS + "=" + ERROR));
			return;
		}
		
		if(userName == null || userName.trim().equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/newUser?" + STATUS + "=" + ERROR));
			return;
		}
		
		if(password == null || password.trim().equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/newUser?" + STATUS + "=" + ERROR));
			return;
		}
		
		if(verifyPassword == null || verifyPassword.trim().equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/newUser?" + STATUS + "=" + ERROR));
			return;
		}
		
		if(!password.equals(verifyPassword)) {
			response.sendRedirect(response.encodeRedirectURL("/newUser?" + STATUS + "=" + ERROR));
			return;
		}
		
		
		DBConfig dbConfig = new DBConfig();
		
		try {
			//Checks to see if user already exists
			if(DBHelper.verifyUser(dbConfig, userName, password)){
				response.sendRedirect(response.encodeRedirectURL("/newUser?" + STATUS + "=" + ERROR));
				return;
			}
			//Checks to see if user name is taken
			if(DBHelper.checkUserNameTaken(dbConfig, userName)){
				response.sendRedirect(response.encodeRedirectURL("/newUser?" + STATUS + "=" + ERROR));
				return;
			}
			
			//Adds user to database
			DBHelper.addToUserInfoTable(name, userName, password, dbConfig);
			
		} catch (SQLException e) {
			System.err.println("Error in verifying user");
			e.printStackTrace();
		}	
		
		
		
		HttpSession session = request.getSession();
		session.setAttribute(USER_NAME, userName);
		session.setAttribute(NAME, name);
		
		Date lastLogin = new Date(session.getLastAccessedTime());
		session.setAttribute("lastLogin", lastLogin);
								
		//redirect to search
		response.sendRedirect(response.encodeRedirectURL("/search"));
		return;
	}
	
	protected String backToLoginButton() {
		return "<p><center>"+
                "<form action=\"login\" method=\"get\">"+
				"<input type=\"submit\" name=\"backToLoginButton\" value=\"Back To Login\"></input>"+
				"</form></p>";
	}
	
	
}

package project.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.database.DBConfig;
import project.database.DBHelper;

public class VerifyUserServlet extends BaseServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//VerifyUser does not accept GET requests. Redirects to login with error status.
		response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
		return;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		
		if(userName == null || userName.trim().equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
			return;
		}
		
		if(password == null || password.trim().equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
			return;
		}
		
		try {
			//Verifies that user exists
			if(!DBHelper.verifyUser(dbConfig, userName, password)){
				response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
				return;
			}

			//Checks the database to see if the password is correct
			if(!DBHelper.verifyPassword(dbConfig, userName, password)){
				response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
				return;
			}

		} catch (SQLException e) {
			System.err.println("Error in verifying user in VerifyUserServerA");
			e.printStackTrace();
		}
		
		DBConfig dbConfig = new DBConfig();
		HttpSession session = request.getSession();
		String name = null;
		session.setAttribute(USER_NAME, userName);
		
		try {
			name = DBHelper.getName(dbConfig, userName, password);
		} catch (SQLException e) {
			System.err.println("Error trying to get user's name");
			e.printStackTrace();
		}
		session.setAttribute(NAME, name);
		
		Date lastLogin = new Date(session.getLastAccessedTime());
		session.setAttribute("lastLogin", lastLogin);
							
		//redirect to search
		response.sendRedirect(response.encodeRedirectURL("/search"));
		return;
	}
	


}

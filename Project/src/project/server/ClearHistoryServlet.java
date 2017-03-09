package project.server;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.database.DBConfig;
import project.database.DBHelper;

public class ClearHistoryServlet extends BaseServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//clearHistory does not accept GET requests. Redirects to login with error status.
		response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
		return;
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String userName = request.getParameter("userName");
		
		DBConfig dbConfig = new DBConfig();
		
		if(userName == null || userName.trim().equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
			return;
		}
		
		DBHelper.clearSearchHistory(dbConfig, userName);
							
		//redirect to search history
		response.sendRedirect(response.encodeRedirectURL("/searchHistory"));
		return;
	}

}

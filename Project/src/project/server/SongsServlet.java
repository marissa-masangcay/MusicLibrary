package project.server;
import java.io.IOException;

import org.apache.log4j.*;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import project.database.DBHelper;

public class SongsServlet extends SongSearchServlet {
	
	
	/**
	 * GET /song returns a web page containing a list and a search box where a query may be entered.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute(USER_NAME);

		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		else {
			processSongsRequest(request, response);
		}

	}

	
	/**
	 * POST /songs returns a web page containing a list and a search box where a query may be entered.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute(USER_NAME);

		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		else {
			processSongsRequest(request, response);
		}

	}


}

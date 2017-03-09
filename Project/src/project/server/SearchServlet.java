package project.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import project.concurrent.ThreadSafeMusicLibrary;
import project.database.DBConfig;
import project.database.DBHelper;

public class SearchServlet extends SongSearchServlet{
	
	/**
	 * GET /search returns a web page containing a list and a search box where a query may be entered.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String name = (String) session.getAttribute(NAME);
		String userName = (String) session.getAttribute(USER_NAME);
		Date lastLogin = (Date) session.getAttribute("lastLogin");

		//user is not logged in, redirect to login page
		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}

		String responseHtml = "<form action=\"songs\" method=\"get\">" + 
				"Search Type: "+
				"<select name=\"queryType\">" +
				"<option value=\"Artist\">Artist</option>" +
				"<option value=\"Title\">Title</option>" +
				"<option value=\"Tag\">Tag</option>" +
				"</select>"+
				" Query: "+
				"<input type=\"text\" name=\"query\">" +
				"<input type=\"submit\" value=\"Search\">" +
				"</form>" + showAllFavoritesButton() + showSortedArtistsButton() + 
				showSortedPlayCountButton() + showSearchHistoryButton();
		
		String fullResponseHtml = header(name)+ "<br>Last login: "+lastLogin + "<br>"+ responseHtml + footer();
		
		PrintWriter writer = prepareResponse(response);
		writer.println(fullResponseHtml);
	}
	
	/**
	 * POST /search assumes a parameter of query=<query>.
	 * Returns a web page containing the songs of the input query
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		processSongsRequest(request, response);
	}

}

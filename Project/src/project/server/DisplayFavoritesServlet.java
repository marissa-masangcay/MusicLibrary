package project.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.database.DBConfig;
import project.database.DBHelper;

public class DisplayFavoritesServlet extends BaseServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//VerifyUser does not accept GET requests. Redirects to login with error status.
		response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
		return;
	}
	
	/**
	 * GET /search returns a web page containing a list and a search box where a query may be entered.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute(USER_NAME);
		String name = (String) session.getAttribute(NAME);
		String favoritesTable = null;
		ArrayList<ArrayList<String>> favoritesList = new ArrayList<ArrayList<String>>();
		
		DBConfig dbConfig = new DBConfig();
		
		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		
		String responseHtml = queryInput();
		
		try {
			favoritesList = DBHelper.getFavorites(dbConfig, userName);
			favoritesTable = favoritesToHtml(favoritesList);
		} catch (SQLException e) {
			System.err.println("Error in trying to display favorites");
			e.printStackTrace();
		}
		
		if(favoritesTable == null){
			favoritesTable = "<center><b>Sorry, but you haven't favorited anything yet!</b>";
		}
		
		String fullResponseHtml = header(name) + responseHtml + favoritesTable + footer();
		
		PrintWriter writer = prepareResponse(response);
		writer.println(fullResponseHtml);
	}
	
	/**
	 * Creates the html to display the favorites list
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static String favoritesToHtml(ArrayList<ArrayList<String>> favorites) throws SQLException {
		String favoriteHtml = null;

		StringBuilder sb = new StringBuilder();

		sb.append("<br>Here are the songs you have favorited!<br/>");

		sb.append("<table border=\"2px\" width=\"100%\">" +				
				"<tr><td><strong>Artist</strong></td><td><strong>Title</strong></td></tr>");
		
		for(int i = 0; i < favorites.size(); i++)
		{
			for(int j = 0; j < favorites.get(i).size(); j++)
			{
				String artist = favorites.get(i).get(j);
				j++;
				String title = favorites.get(i).get(j);
				sb.append("<tr><td>" + artist + "</td><td>" + title + "</td></tr>");
			}
		}
		
		sb.append("</table>");
		
		favoriteHtml = sb.toString();

		return favoriteHtml;
	}
	

}

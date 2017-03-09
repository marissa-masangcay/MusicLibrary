package project.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.concurrent.ThreadSafeMusicLibrary;
import project.database.DBConfig;
import project.database.DBHelper;

public class SortedPlayCountServlet extends BaseServlet{
	
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
		String playCountTable = null;
		ArrayList<String> playCountList = new ArrayList<String>();
		
		DBConfig dbConfig = new DBConfig();
		
		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		
		String responseHtml = queryInput();
		
		try {
			playCountList = DBHelper.getSortedPlayCount(dbConfig);
			playCountTable = playCountToHtml(playCountList);
		} catch (SQLException e) {
			System.err.println("Error in trying to display favorites");
			e.printStackTrace();
		}
		
		if(playCountTable.isEmpty()){
			playCountTable = "<center><b>Sorry, but there are no artists in this library!</b>";
		}

		String fullResponseHtml = header(name) + responseHtml + playCountTable + footer();

		PrintWriter writer = prepareResponse(response);
		writer.println(fullResponseHtml);
	}
	
	/*
	 * Return the html for button to display song info
	 */
	protected static String showArtistInfoButton(String artist) {
		
		try {
			artist = URLEncoder.encode(artist, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error in encoding in favorite button");
			e.printStackTrace();
		}		
		String showSongInfo =
				"<form action=\"artistInfo\" method=\"post\" name=\"showArtistInfoButton\">" +
						"<input type=\"hidden\" name=\"artist\" value="+artist+"> </input>"+
						"<input type=\"submit\" value=\"Get Artist Info\">" +
						"</input></form>";

		return showSongInfo;
	}
	
	
	/**
	 * Creates the html to display the favorites list
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static String playCountToHtml(ArrayList<String> favorites) throws SQLException {
		String favoriteHtml = null;

		StringBuilder sb = new StringBuilder();

		sb.append("<br>Here are the artists in the library sorted by play count!<br/>");

		sb.append("<table border=\"2px\" width=\"100%\">" +				
				"<tr><td><strong>Artist</strong></td><td><strong>Artist Info</strong></td></tr>");

		for(int i = 0; i < favorites.size(); i++)
		{
			String artist = favorites.get(i);
			sb.append("<tr><td>" + artist + "</td><td>" + showArtistInfoButton(artist) + "</td></tr>");
		}
		
		sb.append("</table>");
		
		favoriteHtml = sb.toString();

		return favoriteHtml;
	}
}

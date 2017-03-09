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

public class SortedArtistsServlet extends BaseServlet{
	
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
		String artistsTable = null;
		TreeSet<String> sortedArtists = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		
		DBConfig dbConfig = new DBConfig();
		
		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		
		String responseHtml = queryInput();
		
		//retrieve the musiclibrary from the context
		ThreadSafeMusicLibrary musicLibrary = (ThreadSafeMusicLibrary) request.getServletContext().getAttribute("musicLibrary");
		
		sortedArtists = musicLibrary.getSortedArtists();
		artistsTable = artistsToHtml(sortedArtists);
		
		if(artistsTable.isEmpty()){
			artistsTable = "<center><b>Sorry, but there are no artists in this library!</b>";
		}

		String fullResponseHtml = header(name) + responseHtml + artistsTable + footer();

		PrintWriter writer = prepareResponse(response);
		writer.println(fullResponseHtml);
	}
	
	
	/**
	 * Creates the html to display the favorites list
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	protected static String artistsToHtml(TreeSet<String> artists) {
		String artistsHtml = null;
		Iterator iterator = artists.iterator();
		StringBuilder sb = new StringBuilder();

		sb.append("<br>Here are all of the artists in the library!<br/>");

		sb.append("<table border=\"2px\" width=\"100%\">" +				
				"<tr><td><strong>Artist</strong></td><td><strong>Artist Info</strong></td></tr>");
		
		while(iterator.hasNext())
		{
			String artist = iterator.next().toString();
			sb.append("<tr><td>" + artist + "</td><td>"+ showArtistInfoButton(artist) +"</td></tr>");
		}
		
		sb.append("</table>");
		
		artistsHtml = sb.toString();

		return artistsHtml;
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

}

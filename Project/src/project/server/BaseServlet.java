package project.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import project.database.DBConfig;

/**
 * A Servlet superclass with methods common to all servlets for this application.
 *
 */
public class BaseServlet extends HttpServlet {
	
	public static final String NAME = "name";
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String VERIFY_PASSWORD = "verifyPassword";
	public static final String STATUS = "status";
	public static final String ERROR = "error";
	public static final String NOT_LOGGED_IN = "not_logged_in";
	
	public static final DBConfig dbConfig = new DBConfig();

	
	/*
	 * Prepare a response of HTML 200 - OK.
	 * Set the content type and status.
	 * Return the PrintWriter.
	 */
	protected PrintWriter prepareResponse(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		return response.getWriter();
	}
	
	
	/*
	 * Return the beginning part of the HTML page.
	 */
	protected String header(String name) {
		String title = "<center><font size=\"6\"><b>Song Finder</b></font>";
		String logout = logoutButton(name);
		String fullTitle = "<html><body bgcolor=\"#E8D9F9\"><head><title>" + title + "</title></head><body>";
		String welcomeTitle ="<br>Welcome to song finder! Search for an artist, song title, or tag "+
		"and we will give you a list of similar songs you might like.<br/><hr>";
		return title  + logout + fullTitle + welcomeTitle;		
	}
	
	/*
	 * Return the user input query section of the HTML page. 
	 */
	protected String queryInput() {
		String responseHtmlSearch =       
				"<form action=\"/songs\" method=\"get\">" + 
						"Search Type: "+
						"<select name=\"queryType\">" +
						"<option value=\"Artist\">Artist</option>" +
						"<option value=\"Title\">Title</option>" +
						"<option value=\"Tag\">Tag</option>" +
						"</select>"+
						" Query: "+
						"<input type=\"text\" name=\"query\">" +
						"<input type=\"submit\" value=\"Search\">" +
						"</form>" +
						showAllFavoritesButton() + showSortedArtistsButton() + 
						showSortedPlayCountButton() + showSearchHistoryButton();
		
		return responseHtmlSearch;
	}
	
	
	/*
	 * Return the last part of the HTML page. 
	 */
	protected String footer() {
		return "</body></html>";
	}
	
	
	/*
	 * Given a request, return the value of the parameter with the
	 * provided name or null if none exists.
	 */
	protected String getParameterValue(HttpServletRequest request, String key) {
		return request.getParameter(key);
	}
	
	
	/*
	 * Return the html for logout button
	 */
	protected String logoutButton(String name) {
		return "<p align = \"right\">"+
	           "Hi "+ name +
	            "<form action=\"logout\" method=\"get\">"+
				"<input type=\"submit\" name=\"logoutButton\" value=\"Logout\" style=\"float: right;\"> </input></form>"+
				"<br></p>";
	}
	
	
	/*
	 * Return the html for logout button
	 */
	protected String favoriteButton(String userName, String trackId, String artist, String title, String query, String queryType) {
		
		try {
			query = URLEncoder.encode(query, "UTF-8");
			artist = URLEncoder.encode(artist, "UTF-8");
			title = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error in encoding in favorite button");
			e.printStackTrace();
		}		
		
		return 
	            "<form action=\"favorites\" method=\"post\" name=\"favoritesButton\">"+
	            "<input type=\"hidden\" name=\"trackId\" value="+trackId+"> </input>"+
	            "<input type=\"hidden\" name=\"query\" value="+query+"> </input>"+
	            "<input type=\"hidden\" name=\"queryType\" value="+queryType+"> </input>"+
	            "<input type=\"hidden\" name=\"artist\" value="+artist+"> </input>"+
	            "<input type=\"hidden\" name=\"title\" value="+title+"> </input>"+
				"<input type=\"submit\" name=\"favoriteButton\" value=\"Favorite\"> </input></form>";
	}
	
	
	/*
	 * Return the html for button to display all songs
	 */
	protected String showAllFavoritesButton() {
		String showFavorites =
				"<form action=\"showFavorites\" method=\"post\" name=\"showFavoritesButton\">" +
						"<input type=\"submit\" value=\"Show My Favorites\">" +
						"</form>" ;
		
		return showFavorites;
	}
	
	/*
	 * Return the html for button to display all songs
	 */
	protected String showSortedArtistsButton() {
		String showArtists =
				"<form action=\"showArtists\" method=\"post\" name=\"showSortedArtistsButton\">" +
						"<input type=\"submit\" value=\"Show All Artists\">" +
						"</form>" ;
		
		return showArtists;
	}
	
	/*
	 * Return the html for button to display all songs
	 */
	protected String showSortedPlayCountButton() {
		String showPlayCount =
				"<form action=\"showPlayCount\" method=\"post\" name=\"showSortedPlayCountButton\">" +
						"<input type=\"submit\" value=\"Show All Artists By PlayCount\">" +
						"</form>" ;
		
		return showPlayCount;
	}
	
	
	/*
	 * Return the html for button to display search history
	 */
	protected String showSearchHistoryButton() {
		String showSearchHistory =
				"<form action=\"searchHistory\" method=\"post\" name=\"searchHistoryButton\">" +
						"<input type=\"submit\" value=\"Show Search History\">" +
						"</form>" ;
		
		return showSearchHistory;
	}
	

}
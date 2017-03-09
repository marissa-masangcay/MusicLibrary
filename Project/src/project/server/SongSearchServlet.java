package project.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

public class SongSearchServlet extends BaseServlet{

	
	/**
	 * Helper method to process songs for both GET and POST
	 */
	public void processSongsRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		String name = (String) session.getAttribute(NAME);
		String userName = (String) session.getAttribute(USER_NAME);
		String status = (String) session.getAttribute(STATUS);
		Date lastLogin = (Date) session.getAttribute("lastLogin");
		
		DBConfig dbConfig = new DBConfig();
		
		//user is not logged in, redirect to login page
		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		   
		String query;
		String queryType;
		
		//Get query parameter if it exists
		if(request.getParameter("query")!=null){
			query = request.getParameter("query");
		}
		else{
			query = "";
		}
		
		//Get queryType parameter if it exists
		if(request.getParameter("queryType")!=null){
			queryType = request.getParameter("queryType");
		}
		else{
			queryType="";
		}
		
		DBHelper.addToSearchHistoryTable(userName, queryType, query, dbConfig);

		//retrieve the musiclibrary from the context
		ThreadSafeMusicLibrary musicLibrary = (ThreadSafeMusicLibrary) request.getServletContext().getAttribute("musicLibrary");

		JSONArray result = new JSONArray();

		//craft the HTML response
		String responseHtmlHead = header(name);

		//body of response will vary based on whether songs were found
		String responseHtmlContent = null;

		if(queryType.equals("Artist")) {
			result = musicLibrary.searchByArtist(query);
		}

		else if(queryType.equals("Title")) {
			result = musicLibrary.searchByTitle(query);
		}

		else if(queryType.equals("Tag")) {
			result = musicLibrary.searchByTag(query);
		}


		String responseHtmlSearch = queryInput();

		if(!result.isEmpty()) {

			StringBuilder sb = new StringBuilder();

			sb.append("<br>Here are some songs you might like!<br/>");

			sb.append("<table border=\"2px\" width=\"100%\">" +				
					"<tr><td><strong>Artist</strong></td><td><strong>Title</strong></td><td><strong>Favorites</strong></td><td><strong>Song Info</strong></td></tr>");

			for(int i = 0; i < result.size(); i++)
			{
				JSONObject jsonSongObject = new JSONObject();
				jsonSongObject = (JSONObject) result.get(i);
				String artist = null;
				String title = null;
				String trackId = null;

				if(jsonSongObject.containsKey("artist"))
				{
					artist = jsonSongObject.get("artist").toString();
				}

				if(jsonSongObject.containsKey("title"))
				{
					title = jsonSongObject.get("title").toString();
				}
				
				if(jsonSongObject.containsKey("trackId"))
				{
					trackId = jsonSongObject.get("trackId").toString();
				}
				
				try {
					if(DBHelper.checkIfFavorited(dbConfig, userName, trackId)){
						sb.append("<tr><td>" + artist + "</td><td>" + title + "</td><td>" + "favorited" + "</td><td>" + showSongInfoButton(artist, title) + "</td></tr>");
					}
					else{
						sb.append("<tr><td>" + artist + "</td><td>" + title + "</td><td>" + favoriteButton(userName, trackId,artist, title, query, queryType) + "</td><td>" + showSongInfoButton(artist, title) + "</td></tr>");
					}
				} catch (SQLException e) {
					System.err.println("error in checking if favorited");
					e.printStackTrace();
				}

			}

			sb.append("</table>");

			responseHtmlContent = sb.toString();
		}

		else
		{
			responseHtmlContent = "<center>Sorry, but no matches were found!";
		}

		String responseHtmlFoot = footer();


		String responseHtml = responseHtmlHead + responseHtmlSearch + "<br>Last login: "+ lastLogin+ "<br>"+  responseHtmlContent + responseHtmlFoot;		

		PrintWriter writer = prepareResponse(response);
		writer.println(responseHtml);
	}
	
	/*
	 * Return the html for button to display song info
	 */
	protected String showSongInfoButton(String artist, String title) {
		
		try {
			artist = URLEncoder.encode(artist, "UTF-8");
			title = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error in encoding in favorite button");
			e.printStackTrace();
		}		
		String showSongInfo =
				"<form action=\"songInfo\" method=\"post\" name=\"showSongInfoButton\">" +
						"<input type=\"hidden\" name=\"artist\" value="+artist+"> </input>"+
						"<input type=\"hidden\" name=\"title\" value="+title+"> </input>"+
						"<input type=\"submit\" value=\"Get Song Info\">" +
						"</input></form>";

		return showSongInfo;
	}


}

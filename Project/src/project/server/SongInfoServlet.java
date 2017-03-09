package project.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import project.concurrent.ThreadSafeMusicLibrary;
import project.database.DBConfig;
import project.database.DBHelper;

public class SongInfoServlet extends BaseServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//VerifyUser does not accept GET requests. Redirects to login with error status.
		response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
		return;
	}
	
	/**
	 * GET /search returns a web page containing the name of the song and the similars to it.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute(USER_NAME);
		String name = (String) session.getAttribute(NAME);
		String similarsTable = null;
		JSONArray artistResult = new JSONArray();
		JSONArray titleResult = new JSONArray();
		
		String artist = null;
		String title = null;

		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		
		//retrieve the musiclibrary from the context
		ThreadSafeMusicLibrary musicLibrary = (ThreadSafeMusicLibrary) request.getServletContext().getAttribute("musicLibrary");

		if(request.getParameter("artist")!=null){
			artist = request.getParameter("artist");
			artist = URLDecoder.decode(artist, "UTF-8");
			artistResult = musicLibrary.searchByArtist(artist);
		}
		else{
			artist = "";
		}

		if(request.getParameter("title")!=null){
			title = request.getParameter("title");
			title = URLDecoder.decode(title, "UTF-8");
			titleResult = musicLibrary.searchByTitle(title);
		}
		else{
			title = "";
		}
		
		
		String responseHtml = queryInput();
		String songHtml = "<center><b>Song Information<br>Artist</b>: "+artist+"<br><b>Title</b>: "+title+ "<br>";
		
		similarsTable = songSimilarsToHtml(artistResult, titleResult);
		
		if(similarsTable == null){
			similarsTable = "<center><b>Sorry, but there are no similars!</b>";
		}

		String fullResponseHtml = header(name) + responseHtml + songHtml + similarsTable + footer();

		PrintWriter writer = prepareResponse(response);
		writer.println(fullResponseHtml);
	}
	
	
	/**
	 * Creates the html to display the favorites list
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	protected static String songSimilarsToHtml(JSONArray artists, JSONArray titles) {
		String similarsHtml = null;
		StringBuilder sb = new StringBuilder();

		sb.append("<br>Here are similar songs!<br/>");

		sb.append("<table border=\"2px\" width=\"100%\">" +				
				"<tr><td><strong>Artist</strong></td><td><strong>Title</strong></td></tr>");

		if(!artists.isEmpty()) {
			for(int i = 0; i < artists.size(); i++)
			{
				JSONObject jsonArtistSongObject = new JSONObject();
				jsonArtistSongObject = (JSONObject) artists.get(i);
				String artist = null;
				String title = null;

				if(jsonArtistSongObject.containsKey("artist") )
				{
					artist = jsonArtistSongObject.get("artist").toString();
				}

				if(jsonArtistSongObject.containsKey("title"))
				{
					title = jsonArtistSongObject.get("title").toString();
				}

				sb.append("<tr><td>" + artist + "</td><td>" + title + "</td></tr>");
			}
		}

		if(!titles.isEmpty()) {
			for(int i = 0; i < titles.size(); i++)
			{
				JSONObject jsonTitleSongObject = new JSONObject();
				jsonTitleSongObject = (JSONObject) titles.get(i);
				if(!artists.contains(titles.get(i))){
					String artist = null;
					String title = null;

					if(jsonTitleSongObject.containsKey("artist"))
					{
						artist = jsonTitleSongObject.get("artist").toString();
					}

					if(jsonTitleSongObject.containsKey("title"))
					{
						title = jsonTitleSongObject.get("title").toString();
					}

					sb.append("<tr><td>" + artist + "</td><td>" + title + "</td></tr>");
				}
			}
		}

		sb.append("</table>");

		similarsHtml = sb.toString();

		return similarsHtml;
	}

}

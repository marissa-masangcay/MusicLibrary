package project.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.concurrent.ThreadSafeMusicLibrary;
import project.database.DBConfig;
import project.database.DBHelper;
import project.web.DBSong;

public class DisplayArtistInfoServlet extends BaseServlet{
	
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
		DBSong artistsInfo;
		String artist = null;
		String artistInfoHtml = null;
		TreeSet<String> sortedArtists = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		
		DBConfig dbConfig = new DBConfig();
		
		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		
		if(request.getParameter("artist")!=null){
			artist = request.getParameter("artist");
			artist = URLDecoder.decode(artist, "UTF-8");
		}
		else{
			artist = "";
		}	
		
		//retrieve the musiclibrary from the context
		ThreadSafeMusicLibrary musicLibrary = (ThreadSafeMusicLibrary) request.getServletContext().getAttribute("musicLibrary");
		
		sortedArtists = musicLibrary.getSortedArtists();
		
		artistsInfo = DBHelper.getArtistInfo(dbConfig, artist, sortedArtists);
		
		String responseHtml = queryInput();	
		
		artistInfoHtml = "<center><b>Artist</b>: " + artistsInfo.getArtistName() + "<br><b>Listeners</b>: " + artistsInfo.getNumberOfListeners()
		+ "<br><b>Play count</b>: "+artistsInfo.getPlayCount() + "<br><b>Bio</b>: " + artistsInfo.getBio();

		String fullResponseHtml = header(name) + responseHtml + artistInfoHtml + footer();

		PrintWriter writer = prepareResponse(response);
		writer.println(fullResponseHtml);
	}

}

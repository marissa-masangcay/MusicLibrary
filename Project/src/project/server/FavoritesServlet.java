package project.server;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.database.DBConfig;
import project.database.DBHelper;

public class FavoritesServlet extends BaseServlet{
	
	
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
		String trackId = null;
		String query = null;
		String queryType = null;
		String artist = null;
		String title = null;

		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}

		DBConfig dbConfig = new DBConfig();


		if(request.getParameter("trackId")!=null){
			trackId = request.getParameter("trackId");
		}
		else{
			trackId = "";
		}

		if(request.getParameter("query")!=null){
			query = request.getParameter("query");
			query = URLDecoder.decode(query, "UTF-8");
		}
		else{
			query = "";
		}

		if(request.getParameter("queryType")!=null){
			queryType = request.getParameter("queryType");
		}
		else{
			queryType = "";
		}

		if(request.getParameter("artist")!=null){
			artist = request.getParameter("artist");
			artist = URLDecoder.decode(artist, "UTF-8");
		}
		else{
			artist = "";
		}

		if(request.getParameter("title")!=null){
			title = request.getParameter("title");
			title = URLDecoder.decode(title, "UTF-8");
		}
		else{
			title = "";
		}

		if(!trackId.isEmpty() && userName!=null){
			DBHelper.addToFavoritesTable(userName, trackId, artist, title, dbConfig);
		}

		response.sendRedirect(response.encodeRedirectURL("/songs?query="+query+"&queryType="+queryType));
		return;

	}

}

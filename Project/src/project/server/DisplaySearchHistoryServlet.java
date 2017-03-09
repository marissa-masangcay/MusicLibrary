package project.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import project.database.DBConfig;
import project.database.DBHelper;

public class DisplaySearchHistoryServlet extends BaseServlet{
	
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
		String searchHistoryTable = null;
		ArrayList<ArrayList<String>> historyList = new ArrayList<ArrayList<String>>();
		
		DBConfig dbConfig = new DBConfig();
		
		if(userName == null) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		
		String responseHtml = queryInput();
		
		try {
			historyList = DBHelper.getSearchHistory(dbConfig, userName);
			searchHistoryTable = searchHistoryToHtml(historyList);
		} catch (SQLException e) {
			System.err.println("Error in trying to display search history");
			e.printStackTrace();
		}
		
		if(searchHistoryTable == null){
			searchHistoryTable = "<center><b>Sorry, but you haven't searched for anything yet!</b>";
		}
		
		String fullResponseHtml = header(name) + responseHtml + clearHistoryButton(userName) + searchHistoryTable + footer();
		
		PrintWriter writer = prepareResponse(response);
		writer.println(fullResponseHtml);
	}
	
	/**
	 * Creates the html to display the favorites list
	 * 
	 * @param dbconfig
	 * @param inputUserName
	 */
	public static String searchHistoryToHtml(ArrayList<ArrayList<String>> searchHistory) throws SQLException {
		String searchHistoryHtml = null;

		StringBuilder sb = new StringBuilder();

		sb.append("<br>Here is your search history!<br/>");

		sb.append("<table border=\"2px\" width=\"100%\">" +				
				"<tr><td><strong>Tag</strong></td><td><strong>Query</strong></td></tr>");
		
		for(int i = 0; i < searchHistory.size(); i++)
		{
			for(int j = 0; j < searchHistory.get(i).size(); j++)
			{
				String tag = searchHistory.get(i).get(j);
				j++;
				String query = searchHistory.get(i).get(j);
				sb.append("<tr><td>" + tag + "</td><td>" + query + "</td></tr>");
			}
		}
		
		sb.append("</table>");
		
		searchHistoryHtml = sb.toString();

		return searchHistoryHtml;
	}
	
	
	/*
	 * Return the html for button to display search history
	 */
	protected String clearHistoryButton(String userName) {
		
		try {
			userName = URLEncoder.encode(userName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error in encoding in favorite button");
			e.printStackTrace();
		}		
		
		String clearHistory =
				"<form action=\"clearHistory\" method=\"post\" name=\"clearHistoryButton\">" +
						"<input type=\"hidden\" name=\"userName\" value="+userName+"> </input>"+
						"<input type=\"submit\" value=\"Clear History\">" +
						"</form>" ;
		
		return clearHistory;
	}
	
	
	

}

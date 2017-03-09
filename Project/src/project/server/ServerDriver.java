package project.server;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import project.concurrent.ThreadSafeMusicLibrary;
import project.concurrent.ThreadedSongParser;

public class ServerDriver {

	public static final int DEFAULT_PORT = 11051;
	
	public static void main(String[] args) throws Exception {
		
		Server server = new Server(DEFAULT_PORT);

		//create a ServletHander to attach servlets
		ServletContextHandler servhandler = new ServletContextHandler(ServletContextHandler.SESSIONS);        
		server.setHandler(servhandler);

		servhandler.addEventListener(new ServletContextListener() {

			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				// TODO Auto-generated method stub
			}

			@Override
			public void contextInitialized(ServletContextEvent sce) {
				Path path = Paths.get("input/lastfm_subset");
				ThreadedSongParser songParser = new ThreadedSongParser(10);
				ThreadSafeMusicLibrary musicLibraryForServer = new ThreadSafeMusicLibrary(false, false, false);
				songParser.buildThreadedMusicLibrary(path, musicLibraryForServer);

				//sets attribute
				sce.getServletContext().setAttribute("musicLibrary", musicLibraryForServer);
				
			}
		});


		//add a servlet for searching for similars songs 
		servhandler.addServlet(SearchServlet.class, "/search");

		//add a servlet for displaying similars songs per input by user
		servhandler.addServlet(SongsServlet.class, "/songs");
		
		//add a servlet for logging in user
		servhandler.addServlet(LoginServlet.class, "/login");

		//add a servlet for logging out a user
		servhandler.addServlet(LogoutServlet.class, "/logout");
		
		//add a servlet for registering a new user
		servhandler.addServlet(NewUserServlet.class, "/newUser");
		
		//add a servlet for verifying a user is logged in
		servhandler.addServlet(VerifyUserServlet.class, "/verifyUser");
		
		//add a servlet to remap all invalid paths to login
		servhandler.addServlet(DirectToDefaultServlet.class, "/*");
		
		//add a servlet to add favorites to database
		servhandler.addServlet(FavoritesServlet.class, "/favorites");
		
		//add a servlet to display favorites to database
		servhandler.addServlet(DisplayFavoritesServlet.class, "/showFavorites");
		
		//add a servlet to view sorted artists in database
		servhandler.addServlet(SortedArtistsServlet.class, "/showArtists");
		
		//add a servlet to add favorites to database
		servhandler.addServlet(SortedPlayCountServlet.class, "/showPlayCount");
		
		//add a servlet to add favorites to database
		servhandler.addServlet(SongInfoServlet.class, "/songInfo");
		
		//add a servlet to show artist info
		servhandler.addServlet(DisplayArtistInfoServlet.class, "/artistInfo");
		
		//add a servlet to show user search history
		servhandler.addServlet(DisplaySearchHistoryServlet.class, "/searchHistory");
		
		//add a servlet to clear search history
		servhandler.addServlet(ClearHistoryServlet.class, "/clearHistory");


		//set the list of handlers for the server
		server.setHandler(servhandler);

		server.start();
		server.join();
		}

}

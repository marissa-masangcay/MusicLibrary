package project.web;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import project.database.DBConfig;
import project.database.DBHelper;

public class LastFMClient {
	
	/**
	 * For each artist in the list artists, does the following: 
	 * 1. Fetches the information about that artist from the last.fm artist 
	 * API: http://www.last.fm/api/show/artist.getInfo
	 * 2. Extracts the artist name, number of listeners, playcount, and bio.
	 * 3. Stores all four pieces of information in a relational database table.
	 * The table must be called artist and must have columns name, listeners, playcount, and bio.
	 * The information stored in dbconfig must be used to connect to the database.
	 * This method assumes the table exists, but should catch any exceptions that occur if
	 * the table does not exist.
	 * 
	 * @param artists
	 * @param dbconfig
	 */
	public static void fetchAndStoreArtists(ArrayList<String> artists, DBConfig dbconfig) {
		final String host = "ws.audioscrobbler.com";
		String apiKey = "c125402dbab1a1d9163139d443c632f7";
		ArrayList<DBSong> songs = new ArrayList<DBSong>();
		
		String artistName = null;
		int numberOfListeners = 0;
		int playCount = 0;
		String bio = null;
		
		for(int i = 0; i < artists.size(); i++)
		{
			String path = "/2.0/?method=artist.getinfo&artist="+artists.get(i)+"&api_key="+apiKey+"&format=json";
			JSONObject artistInfo = HTTPFetcher.download(host, path);
			
			artistName = artists.get(i);
			
			if(artistInfo.containsKey("artist"))
			{
				JSONObject artistObject = (JSONObject) artistInfo.get("artist");
				
				if(artistObject.containsKey("stats"))
				{
					JSONObject statsObject = (JSONObject) artistObject.get("stats");
					if(statsObject.containsKey("listeners"))
					{
						String numListeners = statsObject.get("listeners").toString();
						numberOfListeners = Integer.parseInt(numListeners);
					}
					if(statsObject.containsKey("playcount"))
					{
						String plays = statsObject.get("playcount").toString();
						playCount = Integer.parseInt(plays);
					}
				}
				
				if(artistObject.containsKey("bio"))
				{
					JSONObject bioObject = (JSONObject) artistObject.get("bio");
					if(bioObject.containsKey("summary"))
					{
						bio = bioObject.get("summary").toString();
					}
				}
			}
			
			DBSong songToAdd = new DBSong(artistName, numberOfListeners, playCount, bio);
			songs.add(songToAdd);
			
		}
		
		for(int l = 0; l<songs.size(); l++)
		{
			DBHelper.buildArtistInfoTable(dbconfig, songs.get(l));
		}
		
	}


}

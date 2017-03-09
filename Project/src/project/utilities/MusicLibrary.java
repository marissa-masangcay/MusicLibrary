package project.utilities;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import project.comparator.ArtistComparator;
import project.comparator.TitleComparator;


/**
 * Maintains a music library of Song objects.
 *
 */
public class MusicLibrary {

	private TreeMap<String, TreeSet<Song>> artistMap;
	private TreeMap<String, TreeSet<Song>> titleMap;
	private TreeMap<String, TreeSet<String>> tagMap;
	private HashMap<String, Song> trackIdMap;

	private boolean sortByArtist;
	private boolean sortByTitle;
	private boolean sortByTag;

	/**
	 * Constructor 
	 * @param sortByArtist
	 * @param sortByTitle
	 * @param sortByTag
	 */
	public MusicLibrary(boolean sortByArtist, boolean sortByTitle, boolean sortByTag) {

		this.sortByArtist = sortByArtist;
		this.sortByTitle = sortByTitle;
		this.sortByTag = sortByTag;

		this.artistMap = new TreeMap<String, TreeSet<Song>>();
		this.titleMap = new TreeMap<String, TreeSet<Song>>();
		this.tagMap = new TreeMap<String, TreeSet<String>>();
		this.trackIdMap = new HashMap<String, Song>();
	}

	/**
	 * Add a song to the library in the appropriate map.
	 * @param song - song to add
	 */
	public void addSong(Song song) {

		//Add into tradIdMap
		if (!this.trackIdMap.containsKey(song.getTrackId())) 
		{
			this.trackIdMap.put(song.getTrackId(), song);
		}

		//Add by artist
		if (!this.artistMap.containsKey(song.getArtist()))
		{
			this.artistMap.put(song.getArtist(), new TreeSet<Song>(new ArtistComparator()));
		}
		this.artistMap.get(song.getArtist()).add(song);

		//Add by title
		if (!this.titleMap.containsKey(song.getTitle()))
		{
			this.titleMap.put(song.getTitle(), new TreeSet<Song>(new TitleComparator()));
		}
		this.titleMap.get(song.getTitle()).add(song);

		//Add by tag
		ArrayList<String> songTags = song.getTags();
		for (int i = 0; i < songTags.size(); i++)
		{
			if (!this.tagMap.containsKey(songTags.get(i)))
			{
				this.tagMap.put(songTags.get(i), new TreeSet<String>());
			}
			this.tagMap.get(songTags.get(i)).add(song.getTrackId());
		}


	}


	/**
	 * Writes the songs in the order specified to the given output path. 
	 * 
	 * @param output - file to write to 
	 */
	public void writeLibraryToFile(String outputFile) {
		Path inputFile = Paths.get(outputFile);

		try(
				BufferedWriter bufferedWriter = Files.newBufferedWriter(inputFile, StandardCharsets.UTF_8);
				)
		{
			if (sortByArtist)
			{
				helperWriteToFile(outputFile, this.artistMap, bufferedWriter);
			}

			else if (sortByTitle)
			{
				helperWriteToFile(outputFile, this.titleMap, bufferedWriter);
			}

			else if (sortByTag)
			{
				for (Map.Entry<String, TreeSet<String>> entry : this.tagMap.entrySet())
				{
					String key = entry.getKey();
					TreeSet<String> songsToPrint = tagMap.get(key);
					bufferedWriter.write(key+ ": ");
					String firstSong = songsToPrint.first();
					for (String trackID : songsToPrint.tailSet(firstSong, true))
					{
						bufferedWriter.write(trackID + " ");
					}
					bufferedWriter.write(System.lineSeparator());
				}
			}

		} catch (IOException e) {
			System.err.println("Error trying to write to output file " + outputFile);
		}

	}


	/**
	 * Helper method for writing by artist and title 
	 * 
	 * @param output - file to write to 
	 */
	private void helperWriteToFile(String outputFile, TreeMap<String, TreeSet<Song>> map,
			BufferedWriter bufferedWriter) {

		for (Map.Entry<String, TreeSet<Song>> entry : map.entrySet())
		{
			String key = entry.getKey();
			TreeSet<Song> songsToPrint = map.get(key);
			Song firstSong = songsToPrint.first();
			for (Song song : songsToPrint.tailSet(firstSong, true))
			{
				try {
					bufferedWriter.write(song.getArtist()+ " "+ "-"+ " "+song.getTitle());
					bufferedWriter.write(System.lineSeparator());
				} catch (IOException e) {
					System.err.println("Error trying to write to output file " + outputFile);
				} 
			}
		}

	}


	/**
	 * Given an artist, returns all songs that are similar to any song by that artist. 
	 * 
	 * @param inputArtist - artist to search for 
	 */
	public JSONArray searchByArtist(String inputArtist) {
		LinkedHashSet<JSONObject> resultList = new LinkedHashSet<JSONObject>();
		TreeSet<Song> artistSongList = new TreeSet<Song>();
		ArrayList<String> similarsList = new ArrayList<String>();
		JSONArray similarsJSONArray = new JSONArray();

		if(this.artistMap.containsKey(inputArtist))
		{
			artistSongList = this.artistMap.get(inputArtist);
			
			Song firstSong = artistSongList.first();
			for (Song song : artistSongList.tailSet(firstSong, true))
			{
				similarsList = song.getSimilars();
				for(int l = 0; l < similarsList.size(); l++) 
				{
					String id = similarsList.get(l);
					if (this.trackIdMap.containsKey(id)) {
						Song tmpSong = this.trackIdMap.get(id);
						JSONObject tmpObj = jsonConverter(tmpSong);
						resultList.add(tmpObj);
					}
				}
			}
			
			for(JSONObject test : resultList)
			{
				similarsJSONArray.add(test);
			}
			
			 Collections.sort(similarsJSONArray, new Comparator<JSONObject>() {
			        @Override
			        public int compare(JSONObject s1, JSONObject s2) {
			            return ( (String) s1.get("trackId")).compareTo((String)s2.get("trackId"));
			        }
			    });
			 
			return similarsJSONArray;
		}
		return similarsJSONArray;
	}

	/**
	 * Given a song title, returns all songs that are similar to that title. 
	 * 
	 * @param inputTitle - title to search for 
	 */
	public JSONArray searchByTitle(String inputTitle) {
		LinkedHashSet<JSONObject> resultList = new LinkedHashSet<JSONObject>();
		TreeSet<Song> titleSongList = new TreeSet<Song>();
		ArrayList<String> similarsList;
		JSONArray similarsJSONArray = new JSONArray();

		if(this.titleMap.containsKey(inputTitle)){
			titleSongList = this.titleMap.get(inputTitle);
			Song firstSong = titleSongList.first();
			for (Song song : titleSongList.tailSet(firstSong, true))
			{
				similarsList = new ArrayList<String>();
				similarsList = song.getSimilars();
				Collections.sort(similarsList);
				for(int l = 0; l < similarsList.size(); l++) 
				{
					String id = similarsList.get(l);
					if (this.trackIdMap.containsKey(id)) {
						Song tmpSong = this.trackIdMap.get(id);
						JSONObject tmpObj = jsonConverter(tmpSong);
						resultList.add(tmpObj);
					}
				}
			}

			for(JSONObject test : resultList)
			{
				similarsJSONArray.add(test);
			}
			
			return similarsJSONArray;
		}
		return similarsJSONArray;

	}


	/**
	 * Given a song tag, returns all songs that have that tag. 
	 * 
	 * @param inputTag - title to search for 
	 */
	public JSONArray searchByTag(String inputTag) {
		LinkedHashSet<JSONObject> resultList = new LinkedHashSet<JSONObject>();
		TreeSet<String> tagSongList = new TreeSet<String>();
		JSONArray similarsJSONArray = new JSONArray();

		if(this.tagMap.containsKey(inputTag)){
			tagSongList = this.tagMap.get(inputTag);
			String firstSong = tagSongList.first();
			for (String song : tagSongList.tailSet(firstSong, true))
			{
				if (this.trackIdMap.containsKey(song)) {
					Song tmpSong = this.trackIdMap.get(song);
					JSONObject tmpObj = jsonConverter(tmpSong);
					resultList.add(tmpObj);
				}
			}

			for(JSONObject test : resultList)
			{
				similarsJSONArray.add(test);
			}
			
			return similarsJSONArray;
		}
		return similarsJSONArray;

	}
	
	
	/**
	 * Returns all artists stored in the artist map.
	 * 
	 * @param inputArtist - artist to search for 
	 */
	public TreeSet<String> getSortedArtists() {

		Set<String> artistSongList = this.artistMap.keySet();
		Iterator iterator = artistSongList.iterator();
		TreeSet<String> sortedArtists = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		
		while(iterator.hasNext()){
			sortedArtists.add(iterator.next().toString());
		}

		return sortedArtists;
	}


	/**
	 * Converts a given song object into a JSONObject with the following format:
	 * {
         "artist":"DJ Quik",
         "trackId":"TRAAMJY128F92F5919",
         "title":"Born and Raised In Compton"
       }
	 * 
	 * @param inputSong - song to convert
	 */
	private JSONObject jsonConverter(Song inputSong) {
		String artist = inputSong.getArtist();
		String trackId = inputSong.getTrackId();
		String title = inputSong.getTitle();
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("artist", artist);
		jsonObject.put("trackId", trackId);
		jsonObject.put("title", title);

		return jsonObject;
	}
}


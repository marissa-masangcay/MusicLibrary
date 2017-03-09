package project.concurrent;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import project.utilities.MusicLibrary;
import project.utilities.Song;

/**
 * Maintains a threaded music library of Song objects.
 *
 */
public class ThreadSafeMusicLibrary extends MusicLibrary{
	
	private final ReentrantLock lock;

	public ThreadSafeMusicLibrary(boolean sortByArtist, boolean sortByTitle, boolean sortByTag) {
		super(sortByArtist, sortByTitle, sortByTag);
		lock = new ReentrantLock();
	}
	
	
	/**
	 * Add a song to the library in the appropriate map.
	 * @param song - song to add
	 */
	public void addSong(Song song) {
		lock.lockWrite();
		try {
			super.addSong(song);
		}
		finally{
			lock.unlockWrite();
		}
	}
	
	
	/**
	 * Writes the songs in the order specified to the given output path. 
	 * 
	 * @param output - file to write to 
	 */
	public void writeLibraryToFile(String outputFile) {
		lock.lockRead();
		try{
			super.writeLibraryToFile(outputFile);
		}
		finally{
			lock.unlockRead();
		}
	}
	
	/**
	 * Writes the songs in the order specified to the given output path. 
	 * 
	 * @param output - file to write to 
	 * @return 
	 */
	public JSONArray searchByArtist(String inputArtist) {
		JSONArray resultList = new JSONArray();
		lock.lockRead();
		try{
			resultList = super.searchByArtist(inputArtist);
		}
		finally{
			lock.unlockRead();
		}
		return resultList;
	}
	
	/**
	 * Writes the songs in the order specified to the given output path. 
	 * 
	 * @param output - file to write to 
	 * @return 
	 */
	public JSONArray searchByTitle(String inputTitle) {
		JSONArray resultList = new JSONArray();
		lock.lockRead();
		try{
			resultList = super.searchByTitle(inputTitle);
		}
		finally{
			lock.unlockRead();
		}
		return resultList;
	}
	
	/**
	 * Writes the songs in the order specified to the given output path. 
	 * 
	 * @param output - file to write to 
	 * @return 
	 */
	public JSONArray searchByTag(String inputTag) {
		JSONArray resultList = new JSONArray();
		lock.lockRead();
		try{
			resultList = super.searchByTag(inputTag);
		}
		finally{
			lock.unlockRead();
		}
		return resultList;
	}
	
	/**
	 * Returns all artists stored in the artist map.
	 * 
	 * @param inputArtist - artist to search for 
	 */
	public TreeSet<String> getSortedArtists() {
		TreeSet<String> sortedArtists = new TreeSet<String>();
		lock.lockRead();
		try{
			sortedArtists = super.getSortedArtists();
		}
		finally{
			lock.unlockRead();
		}
		return sortedArtists;
	}


}

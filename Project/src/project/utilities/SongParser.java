package project.utilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * This class builds the music library that is passed in via the 
 * recursiveDirectoryTraverse method. 
 */
public class SongParser {


	/**
	 * Recursively traverse the directory. 
	 * @param directory - directory to traverse
	 */
	public static void recursiveDirectoryTraverse(Path directory, MusicLibrary musicLibrary) {
		if (Files.isDirectory(directory))
		{
			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory))
			{
				for (Path directoryPaths: directoryStream)
				{
					recursiveDirectoryTraverse(directoryPaths, musicLibrary);
				}
			} catch (IOException e) {
				System.err.println("Failed to open file: "+ directory.toString());
			}
		}
		else
		{
			String fileName = directory.toString().toLowerCase();

			if (fileName.endsWith(".json"))
			{
				parseSong(directory, musicLibrary);
			}
		}

	}



	/**
	 * Parses song in JSON file and adds song to music library
	 * @param path 
	 */
	public static void parseSong(Path path, MusicLibrary musicLibrary){

		JSONParser parser = new JSONParser();

		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {

			JSONObject contents = (JSONObject) parser.parse(reader);

			//Creates new song from parsed JSONObject and adds it to MusicLibrary
			Song parsedSong = new Song(contents);
			musicLibrary.addSong(parsedSong);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}	

	}


}


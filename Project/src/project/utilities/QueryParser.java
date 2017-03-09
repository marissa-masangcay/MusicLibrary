package project.utilities;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class QueryParser {
	
	/**
	 * Parses the JSON file path given for queries to search for.  
	 * 
	 * @param path - path of the JSON file to parse queries from 
	 */
	public static JSONObject jsonFileParser(Path path) {

		JSONObject queries = new JSONObject();
		JSONParser parser = new JSONParser();

		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {

			queries = (JSONObject) parser.parse(reader);

		} catch (ParseException e) {
			System.err.println("Error parsing query JSON File");
		} catch (IOException e1) {
			System.err.println("Error with IO in jsonFileParser");
		}
		return queries;
	}
	
	
	/**
	 * Parses the queries from the given JSONObject  
	 * 
	 * @param object - the JSONObject to parse queries from
	 */
	public static JSONObject jsonQueryParser(JSONObject object, MusicLibrary musicLibrary) {
		JSONArray artistArray;
		JSONArray titleArray;
		JSONArray tagArray;
		
		JSONArray artistObject = new JSONArray();
		JSONArray titleObject = new JSONArray();
		JSONArray tagObject = new JSONArray();
		JSONObject allQueriesObject = new JSONObject();

		//If query has searchByArtist
		if (object.containsKey("searchByArtist")) { 
			artistArray =  (JSONArray) object.get("searchByArtist");
			artistObject = jsonArtistParser(artistArray, musicLibrary);	
			allQueriesObject.put("searchByArtist", artistObject);
		}
		
		//If query has searchByTag
		if(object.containsKey("searchByTag")) {
			tagArray = ((JSONArray) object.get("searchByTag"));
			tagObject = jsonTagParser(tagArray, musicLibrary);
			allQueriesObject.put("searchByTag", tagObject);
		}
		
		//If query has searchByTitle
		if (object.containsKey("searchByTitle")) {
			titleArray =  (JSONArray) object.get("searchByTitle");
			titleObject = jsonTitleParser(titleArray, musicLibrary);
			allQueriesObject.put("searchByTitle", titleObject);
		}
		return allQueriesObject;
	}
	
	
	/**
	 * Parses the artists queries from the given JSONObject and returns an artist JSONObject
	 * 
	 * @param artist - the JSONObject to parse queries from
	 */
	public static JSONArray jsonArtistParser(JSONArray artistArray, MusicLibrary musicLibrary) {
		JSONArray jsonObjectArray = new JSONArray();
		
		for(int i = 0; i < artistArray.size(); i++)
		{
			JSONArray tmp = new JSONArray();
			
			String artist = artistArray.get(i).toString();
			tmp = musicLibrary.searchByArtist(artist);
			
			//Adding JSONArray of similar song JSONObjects to new JSONObect
			if (tmp.isEmpty())
			{
				JSONArray emptyObject = new JSONArray();
				JSONObject newObj1 = new JSONObject();
				
				//Inserts artist and similars into JSONObject
				newObj1.put("artist", artist);
				newObj1.put("similars", emptyObject);
				
				//Adds JSONObject to JSONArray
				jsonObjectArray.add(newObj1);
			}
			else{
				JSONObject newObj2 = new JSONObject();
				
				//add similar key to JSONArray of similar songs
				newObj2.put("artist", artist);
				newObj2.put("similars", tmp);
				
				//Adds JSONObject to JSONArray
				jsonObjectArray.add(newObj2);
			}
		}
		return jsonObjectArray;
	}
	
	
	/**
	 * Parses the titles queries from the given JSONObject and returns a title JSONObject
	 * 
	 * @param title - the JSONObject to parse queries from
	 */
	public static JSONArray jsonTitleParser(JSONArray titleArray, MusicLibrary musicLibrary) {
		JSONArray titleObjectArray = new JSONArray();
		
		for(int k = 0; k < titleArray.size(); k++)
		{
			JSONArray tmp2 = new JSONArray();
			
			String title = titleArray.get(k).toString();
			tmp2 = musicLibrary.searchByTitle(title);
			
			if(tmp2.isEmpty())
			{
				JSONArray emptyObject = new JSONArray();
				JSONObject newObj1 = new JSONObject();
				
				//Inserts artist and similars into JSONObject
				newObj1.put("similars", emptyObject);
				newObj1.put("title", title);
				
				//Adds JSONObject to JSONArray
				titleObjectArray.add(newObj1);

			}
			else {
				JSONObject newObj2 = new JSONObject();

				//add similar key to JSONArray of similar songs
				newObj2.put("similars", tmp2);
				newObj2.put("title", title);

				//Adds JSONObject to JSONArray
				titleObjectArray.add(newObj2);
			}
		}
		return titleObjectArray;
	}
	
	/**
	 * Parses the tags queries from the given JSONObject and returns a tag JSONObject
	 * 
	 * @param tag - the JSONObject to parse queries from
	 */
	public static JSONArray jsonTagParser(JSONArray tagArray, MusicLibrary musicLibrary) {
		JSONArray tagObjectArray = new JSONArray();
		
		for(int k = 0; k < tagArray.size(); k++)
		{
			JSONArray tmp3 = new JSONArray();
			
			String tag = tagArray.get(k).toString();
			tmp3 = musicLibrary.searchByTag(tag);
			
			if(tmp3.isEmpty())
			{
				JSONArray emptyObject = new JSONArray();
				JSONObject newObj3 = new JSONObject();
				
				//Inserts artist and similars into JSONObject
				newObj3.put("similars", emptyObject);
				newObj3.put("tag", tag);
				
				//Adds JSONObject to JSONArray
				tagObjectArray.add(newObj3);

			}
			else {
				JSONObject newObj4 = new JSONObject();

				//add similar key to JSONArray of similar songs
				newObj4.put("similars", tmp3);
				newObj4.put("tag", tag);

				//Adds JSONObject to JSONArray
				tagObjectArray.add(newObj4);
			}
		}		
		return tagObjectArray;
	}
	
	
	/**
	 * Writes the JSONObject with parsed queries to the given file. 
	 * 
	 * @param outputFile - file to write to 
	 */
	public void writeQueriesToFile(String outputFile, JSONObject queriesObject) {
		Path inputFile = Paths.get(outputFile);

		try(
				BufferedWriter bufferedWriter = Files.newBufferedWriter(inputFile, StandardCharsets.UTF_8);
				)
		{
			bufferedWriter.write(queriesObject.toJSONString());

		} catch (IOException e) {
			System.err.println("Error trying to write to output file " + outputFile);
		}
	}

}

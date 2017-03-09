import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;

import project.concurrent.ThreadSafeMusicLibrary;
import project.concurrent.ThreadedSongParser;
import project.utilities.ArgumentParser;
import project.utilities.MusicLibrary;
import project.utilities.QueryParser;
import project.utilities.SongParser;

public class Driver {

	/**
	 * Flag used to indicate the following value is an input directory of text
	 * files to use when building the music library.
	 * 
	 */
	public static final String INPUT_FLAG = "-input";

	/**
	 * Flag used to indicate the following value is a valid text
	 * file to use when writing the music library.
	 * 
	 */
	public static final String OUTPUT_FLAG = "-output";

	/**
	 * Flag used to indicate the following value is a valid order
	 * to use when sorting the music library.
	 * 
	 */
	public static final String ORDER_FLAG = "-order";
	
	/**
	 * Flag used to indicate how many threads to use when
	 * executing the program, if invalid will resolve to default value.
	 * 
	 */
	public static final String THREAD_FLAG = "-threads";
	
	/**
     * Default to use when the value for the THREAD_FLAG is missing.
     */
    public static final int THREAD_DEFAULT = 10;
    
    /**
     * Flag used to indicate the following value is a path to
     *  a json file containing search queries. 
     */
    public static final String SEARCH_INPUT_FLAG = "-searchInput";
    
    /**
     * Flag used to indicate the following value is a path to
     *  a json file to write the search queries to.
     */
    public static final String SEARCH_OUTPUT_FLAG = "-searchOutput";


	/**
	 * Parses the provided arguments and, if appropriate, will build a music 
	 * library from a specified directory and write the library in specified
	 * ordered given the appropriate order flag.
	 * 
	 * @param args
	 *            set of flag and value pairs
	 */
	public static void main(String[] args) {

		ArgumentParser argumentParser = new ArgumentParser(args);
		MusicLibrary musicLibrary;
		
		ThreadSafeMusicLibrary threadedMusicLibrary;
		ThreadedSongParser threadedSongParser;
		QueryParser queryParser = new QueryParser();

		String directoryToTraverse = null;
		
		JSONObject queries = null;

		boolean sortByArtist = false;
		boolean sortByTitle = false;
		boolean sortByTag = false;

		Path directory = null;
		Path fileToWriteTo = null;
		Path jsonQueryFileToReadFrom = null;
		Path jsonQueryFileToWriteTo = null;
		
		
		int numberOfThreads = -1;


		try {
			
			//THREADED
			if(argumentParser.hasFlag(THREAD_FLAG))
			{
				try {
					numberOfThreads = Integer.parseInt(argumentParser.getValue(THREAD_FLAG));
					//if number of threads is less than 1 or greater than 100
					if ( numberOfThreads < 1 || numberOfThreads > 1000 )
					{
						numberOfThreads = THREAD_DEFAULT;
					}
					//if number of threads is floating point number
					if (numberOfThreads % 1 != 0)
					{
						numberOfThreads = THREAD_DEFAULT;
					}
				} catch ( NumberFormatException e ) {
					numberOfThreads = THREAD_DEFAULT;
				}
	    		catch ( NullPointerException e ) {
	    			numberOfThreads = THREAD_DEFAULT;
	    		}
				
				
				/**order = order to sort songs in*/
				if (argumentParser.argIsValid(ORDER_FLAG))
				{
					String inputTag = argumentParser.getValue(ORDER_FLAG);

					if (inputTag.equalsIgnoreCase("artist"))
					{
						sortByArtist = true;
					}
					else if (inputTag.equalsIgnoreCase("title"))
					{
						sortByTitle = true;
					}
					else if (inputTag.equalsIgnoreCase("tag"))
					{
						sortByTag = true;
					}
					else
					{
						System.err.println("Invalid sort type.");
					}
				}
				else
				{
					System.err.println("Missing order type");
				}


				//Initializes music library
				threadedMusicLibrary = new ThreadSafeMusicLibrary(sortByArtist, sortByTitle, sortByTag);
				threadedSongParser = new ThreadedSongParser(numberOfThreads);


				/**input = directory to traverse through*/
				if (argumentParser.argIsValid(INPUT_FLAG))
				{
					directoryToTraverse = argumentParser.getValue(INPUT_FLAG);
					directory = Paths.get(directoryToTraverse);

					if (!Files.isDirectory(directory))
					{
						System.err.println("Invalid directory");
					}

					//Traverses through the directory given by user and builds music library
					threadedSongParser.buildThreadedMusicLibrary(directory, threadedMusicLibrary);
				}
				else 
				{
					System.err.println("Please enter valid directory");
				}



				/**output = file name to print query results to*/
				if (argumentParser.argIsValid(OUTPUT_FLAG))
				{
					fileToWriteTo = Paths.get(argumentParser.getValue(OUTPUT_FLAG));

					if (!fileToWriteTo.isAbsolute()) 
					{
						System.err.println("Invalid output file");
					}

					//Writes music library to given text file in given order
					threadedMusicLibrary.writeLibraryToFile(fileToWriteTo.toString());
					
				}
				else
				{
					System.err.println("Invalid output file.");
				}
				
				
				/**searchInput = file name to read query searches from*/
				if (argumentParser.argIsValid(SEARCH_INPUT_FLAG))
				{
					jsonQueryFileToReadFrom = Paths.get(argumentParser.getValue(SEARCH_INPUT_FLAG));

					if (!jsonQueryFileToReadFrom.isAbsolute()) 
					{
						System.err.println("Invalid input file in search input");
					}
					
					
					JSONObject queries2 = queryParser.jsonFileParser(jsonQueryFileToReadFrom);
					queries = queryParser.jsonQueryParser(queries2, threadedMusicLibrary);
					
				}
				else
				{
					System.err.println("Invalid output file.");
				}
				
				
				/**searchInput = file name to read query searches from*/
				if (argumentParser.argIsValid(SEARCH_OUTPUT_FLAG))
				{
					jsonQueryFileToWriteTo = Paths.get(argumentParser.getValue(SEARCH_OUTPUT_FLAG));

					if (!jsonQueryFileToWriteTo.isAbsolute()) 
					{
						System.err.println("Invalid output file in search output");
					}
					
					queryParser.writeQueriesToFile(jsonQueryFileToWriteTo.toString(), queries);
					
				}
				else
				{
					System.err.println("Invalid output file.");
				}

			}
			
			
			//NOT THREADED
			else {
				/**order = order to sort songs in*/
				if (argumentParser.argIsValid(ORDER_FLAG))
				{
					String inputTag = argumentParser.getValue(ORDER_FLAG);

					if (inputTag.equalsIgnoreCase("artist"))
					{
						sortByArtist = true;
					}
					else if (inputTag.equalsIgnoreCase("title"))
					{
						sortByTitle = true;
					}
					else if (inputTag.equalsIgnoreCase("tag"))
					{
						sortByTag = true;
					}
					else
					{
						System.err.println("Invalid sort type.");
					}
				}
				else
				{
					System.err.println("Missing order type");
				}


				//Initializes music library
				musicLibrary = new MusicLibrary(sortByArtist, sortByTitle, sortByTag);
				


				/**input = directory to traverse through*/
				if (argumentParser.argIsValid(INPUT_FLAG))
				{
					directoryToTraverse = argumentParser.getValue(INPUT_FLAG);
					directory = Paths.get(directoryToTraverse);

					if (!Files.isDirectory(directory))
					{
						System.err.println("Invalid directory");
					}

					//Traverses through the directory given by user
					SongParser.recursiveDirectoryTraverse(directory, musicLibrary);
				}
				else 
				{
					System.err.println("Please enter valid directory");
				}



				/**output = file name to print query results to*/
				if (argumentParser.argIsValid(OUTPUT_FLAG))
				{
					fileToWriteTo = Paths.get(argumentParser.getValue(OUTPUT_FLAG));

					if (!fileToWriteTo.isAbsolute()) 
					{
						System.err.println("Invalid output file");
					}
					
					//Writes music library to given text file in given order
					musicLibrary.writeLibraryToFile(fileToWriteTo.toString());
				}
				else
				{
					System.err.println("Invalid output file.");
				}
				
				
				/**searchInput = file name to read query searches from*/
				if (argumentParser.argIsValid(SEARCH_INPUT_FLAG))
				{
					jsonQueryFileToReadFrom = Paths.get(argumentParser.getValue(SEARCH_INPUT_FLAG));

					if (!jsonQueryFileToReadFrom.isAbsolute()) 
					{
						System.err.println("Invalid input file in search input");
					}
					
					
					JSONObject queries2 = queryParser.jsonFileParser(jsonQueryFileToReadFrom);
					queries = queryParser.jsonQueryParser(queries2, musicLibrary);
					
				}
				else
				{
					System.err.println("Invalid output file.");
				}
				
				
				/**searchInput = file name to read query searches from*/
				if (argumentParser.argIsValid(SEARCH_OUTPUT_FLAG))
				{
					jsonQueryFileToWriteTo = Paths.get(argumentParser.getValue(SEARCH_OUTPUT_FLAG));

					if (!jsonQueryFileToWriteTo.isAbsolute()) 
					{
						System.err.println("Invalid output file in search output");
					}
					
					queryParser.writeQueriesToFile(jsonQueryFileToWriteTo.toString(), queries);
					
				}
				else
				{
					System.err.println("Invalid output file.");
				}
				
			}
	
		} catch( NullPointerException e ) { 
        	System.err.println("No input found");
        } 
		

	}
	
	

}

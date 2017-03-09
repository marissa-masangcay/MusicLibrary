package project.utilities;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * A class to maintain data about a single song object.
 * Java object representation of a JSON object with schema below.
 *
 */

/*
{  
   "artist":"The Primitives",
   "timestamp":"2011-09-07 12:34:34.851502",
   "similars":[  
      [  
         "TROBUDC128F92F7F0B",
         1
      ],
      [  
         "TRWSCCK128F92F7EDB",
         0.98714400000000002
      ]
   ],
   "tags":[  
      [  
         "1980s",
         "100"
      ],
      [  
         "80s",
         "33"
      ],
      [  
         "pop",
         "33"
      ],
      [  
         "alternative",
         "33"
      ]
   ],
   "track_id":"TRBDCAB128F92F7EE4",
   "title":"Never Tell"
} 

 */

public class Song {


	private String artist;
	private String trackId;
	private String title;
	private ArrayList<String> similars;
	private ArrayList<String> tags;

	/**
	 * Constructor 
	 * @param artist
	 * @param trackId
	 * @param title
	 * @param similars
	 * @param tags
	 */
	public Song(String artist, String trackId, String title, ArrayList<String> similars, ArrayList<String> tags) {
		this.artist = artist;
		this.trackId = trackId;
		this.title = title;
		this.similars = similars;
		this.tags = tags;
	}

	/**
	 * Constructor that takes as input a single JSONObject as illustrated in the example above and
	 * constructs a Song object by extracting the relevant data.
	 * @param object
	 */
	public Song(JSONObject object) {

		this.similars = new ArrayList<>();
		this.tags = new ArrayList<>();

		this.artist = object.get("artist").toString();

		this.trackId = object.get("track_id").toString();

		this.title = object.get("title").toString();


		JSONArray similarsArray = (JSONArray) object.get("similars");
		for (int i = 0; i < similarsArray.size(); i++)
		{			
			JSONArray tmpArray = (JSONArray) similarsArray.get(i);	
			this.similars.add(tmpArray.get(0).toString());		
		}

		JSONArray tagsArray = (JSONArray) object.get("tags");
		for (int i = 0; i < tagsArray.size(); i++)
		{
			JSONArray tmpArray = (JSONArray) tagsArray.get(i);
			this.tags.add(tmpArray.get(0).toString());
		}	

	}

	/**
	 * Return artist.
	 * @return
	 */
	public String getArtist() {
		return this.artist;
	}

	/**
	 * Return track ID.
	 * @return
	 */
	public String getTrackId() {
		return this.trackId;
	}

	/**
	 * Return title.
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Return a list of the track IDs of all similar tracks.
	 * @return
	 */
	public ArrayList<String> getSimilars() {
		return this.similars;
	}

	/**
	 * Return a list of all tags for this track.
	 * @return
	 */
	public ArrayList<String> getTags() {
		return this.tags;
	}	
		


}

package project.comparator;
import java.util.Comparator;

import project.utilities.Song;

public class TitleComparator implements Comparator<Song> {

	/**
	 * Compares songs and sorts them by title, artist, and trackId
	 *
	 * @param o1
	 * @param o2
	 *    Songs to be compared 
	 * @return the value of the two songs after being compared
	 */
	@Override
	public int compare(Song o1, Song o2) {

		if (!o1.getTitle().equals(o2.getTitle()))
		{
			return o1.getTitle().compareTo(o2.getTitle());
		}

		if (!o1.getArtist().equals(o2.getArtist())) 
		{
			return o1.getArtist().compareTo(o2.getArtist());
		}

		return o1.getTrackId().compareTo(o2.getTrackId());
	}

}

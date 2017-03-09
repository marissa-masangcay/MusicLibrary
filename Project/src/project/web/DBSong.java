package project.web;

public class DBSong {
	
	String artistName = null;
	int numberOfListeners = -1;
	int playCount = -1;
	String bio = null;
	
	public DBSong(String artistName, int numberOfListeners, int playCount, String bio)
	{
		this.artistName = artistName;
		this.numberOfListeners = numberOfListeners;
		this.playCount = playCount;
		this.bio = bio;
	}

	public String getArtistName() {
		return artistName;
	}

	public int getNumberOfListeners() {
		return numberOfListeners;
	}

	public int getPlayCount() {
		return playCount;
	}

	public String getBio() {
		return bio;
	}
	
	@Override
	public String toString() {
		return "DBSong [artistName=" + artistName + ", numberOfListeners=" + numberOfListeners + ", playCount="
				+ playCount + ", bio=" + bio + "]";
	}

}

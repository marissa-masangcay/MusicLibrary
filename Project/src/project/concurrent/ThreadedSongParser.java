package project.concurrent;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import project.utilities.SongParser;

/**
 * This class concurrently builds the music library that is passed in via the 
 * recursiveDirectoryTraverse method. 
 */
public class ThreadedSongParser extends SongParser{
	
	/**Work queue used to handle multi-threading for this class. */
	private final WorkQueue workers;
	
	
	/**Initializes this multi-threaded Song Parser. */
	public ThreadedSongParser(int numberOfThreads) {
		workers = new WorkQueue(numberOfThreads);
	}
	
	
	/**
     * Shuts down the work queue after all pending work is finished. After this
     * point, all additional calls to {@link #parseTextFiles(Path, String)} will
     * no longer work.
     */
    private void shutdown() { 
        workers.shutdown();
    }
    
    /**
	 * Helper method, that helps a thread wait until all of the current
	 * work is done. This is useful for resetting the counters or shutting
	 * down the work queue.
	 */
	private void awaitTermination() { 
		workers.awaitTermination();
	}
	
	
	/**
	 * Recursively traverses the directory and calls one thread per file found. 
	 * @param directory - directory to traverse
	 */
    private void threadedTraverse(Path path, ThreadSafeMusicLibrary musicLibrary) throws IOException { 
        if ( Files.isDirectory(path) ) {
            try (
                DirectoryStream<Path> directory = Files.newDirectoryStream(path);
            ) {
                for ( Path current : directory ) {
                    threadedTraverse(current, musicLibrary);
                }
            }
            catch ( IOException e ) {
            	System.err.println("Error in threadedTraverse");
                System.err.println(e.getMessage());
            }
        }
        else if ( Files.isReadable(path) && path.toString().toLowerCase().endsWith(".json") ) {
            workers.execute(new FileMinion(path, musicLibrary));
        }
    } 
    
    
    public void buildThreadedMusicLibrary(Path path, ThreadSafeMusicLibrary musicLibrary) {
    	try {
			threadedTraverse(path, musicLibrary);
		} catch (IOException e) {
			System.err.println("Error in buildThreadedMusicLibrary");
			System.err.println(e.getMessage());
		}
    	
    	shutdown();
    	awaitTermination();
    }
    
    
    /**
	 * Handles per-directory parsing. If a file is found a new FileMinion is
	 * created to parse that song.
	 */
	private class FileMinion implements Runnable {
		
		private Path file;
		private ThreadSafeMusicLibrary musicLibrary;

		public FileMinion(Path file, ThreadSafeMusicLibrary musicLibrary) {
			this.file = file;
			this.musicLibrary = musicLibrary;
		}

		@Override
		public void run() {
			SongParser.parseSong(file, musicLibrary);
		}
	}

}

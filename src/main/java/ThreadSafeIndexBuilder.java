import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This class takes in an inverted index and populates it in a multithreaded way.
 *
 * @author Adon Anglon
 */
public class ThreadSafeIndexBuilder extends InvertedIndexBuilder {
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final WorkQueue queue;
	
	/**
	 * Passes an invertedIndex into the class to be altered.
	 *
	 * @param invertedIndex invertedIndex to be entered
	 */
	public ThreadSafeIndexBuilder(InvertedIndex index, WorkQueue queue) {
		super(index);
		this.queue = queue;
	}

	@Override
	public void readFile(Path path) throws IOException {
		readFile(path, this.invertedIndex, this.queue);
	}
	
	

	@Override
	public void build(Path mainPath) throws IOException {
		super.build(mainPath);
		try {
			queue.finish();
		}
		catch(InterruptedException e) {
			log.debug("An Interruption error was thrown and needs to be handled.");
		}
	}

	/**
	 * Reads the file path into the specified invertedIndex. 
	 *
	 * @param path file path to be read
	 * @param invertedIndex the index that will append the stemmed words from the
	 * 	file
	 * @throws IOException file is invalid or can not be found
	 */
	public static void readFile(Path path, InvertedIndex invertedIndex, WorkQueue queue) throws IOException {
		queue.execute(new Task(path, invertedIndex));
	}
	
	public static class Task implements Runnable {
		Path path;
		InvertedIndex index;
		
		public Task(Path path, InvertedIndex index) {
			this.path = path;
			this.index = index;
		}

		@Override
		public void run() {
			InvertedIndex tempIndex = new InvertedIndex();
			try{
				InvertedIndexBuilder.readFile(path, tempIndex);
				synchronized(index) {
					index.addAll(tempIndex);
				}
			}
			catch(IOException e) {
					log.debug("An IO error was thrown and needs to be handled.");
			}
		}
	}
}
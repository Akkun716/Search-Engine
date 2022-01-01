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
public class ThreadSafeIndexBuilder implements IndexBuilder {
	/**
	 * This InvertedIndex will be a reference for the index passed into the function.
	 */
	private final ThreadSafeInvertedIndex invertedIndex;
	
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final WorkQueue queue;

	/**
	 * Passes an invertedIndex into the class to be altered.
	 *
	 * @param invertedIndex invertedIndex to be entered
	 */
	public ThreadSafeIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		this.invertedIndex = index;
		this.queue = queue;
	}

	@Override
	public void readFile(Path path) throws IOException {
		queue.execute(new Task(path));
	}

	@Override
	public void build(Path mainPath) throws IOException {
		if(Files.isDirectory(mainPath)) {
			readFiles(mainPath);
		}
		else {
			readFile(mainPath);
		}
		queue.finish();
	}

	/**
	 * This inner class represents a Runnable task that can be added to a work
	 * queue for multithreading.
	 */
	private class Task implements Runnable {
		Path path;

		public Task(Path path) {
			this.path = path;
		}

		@Override
		public void run() {
			InvertedIndex tempIndex = new InvertedIndex();
			try{
				InvertedIndexBuilder.readFile(path, tempIndex);
				invertedIndex.addAll(tempIndex);
			}
			catch(IOException e) {
					log.debug("An IO error was thrown and needs to be handled.");
			}
		}
	}
}
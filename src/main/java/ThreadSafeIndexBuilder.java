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
 * This class holds word stems and all of the files and positions within those
 * files they have been found in.
 *
 * @author Adon Anglon
 */
public class ThreadSafeIndexBuilder extends InvertedIndexBuilder {
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final WorkQueue queue;

	// TODO private final ThreadSafeInvertedIndex index;

	/**
	 * Passes an invertedIndex into the class to be altered.
	 *
	 * @param invertedIndex invertedIndex to be entered
	 */
	public ThreadSafeIndexBuilder(InvertedIndex index, WorkQueue queue) { // TODO ThreadSafeInvertedIndex
		super(index);
		this.queue = queue;
	}

	@Override
	public void readFile(Path path) throws IOException {
		readFile(path, this.invertedIndex, this.queue);
		// TODO queue.execute(new Task(path, invertedIndex));
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

	// TODO Remove
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

	public static class Task implements Runnable { // TODO private class Task (no static keyword)
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
				synchronized(index) { // TODO Remove after the index is made thread-safe
					index.addAll(tempIndex);
				}
			}
			catch(IOException e) {
					log.debug("An IO error was thrown and needs to be handled.");
			}
		}
	}
}
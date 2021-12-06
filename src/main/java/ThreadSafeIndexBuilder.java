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
	
	/** The log4j2 logger. */
	protected static final Logger log = LogManager.getLogger();
	
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
		catch(InterruptedException e) {}
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
			synchronized(index) {
				try(BufferedReader br = Files.newBufferedReader(path)) {
					Stemmer stemmer = new SnowballStemmer(TextStemmer.ENGLISH);
					String line, pathString = path.toString();
					int i = 1;
					while((line = br.readLine()) != null) {
						for(String word: TextParser.parse(line)) {
							index.add(stemmer.stem(word).toString(), pathString, i++);
						}
					}
				}
				catch(IOException e) {
					log.debug("An IO error was thrown and needs to be handled.");
				}
			}
		}
	}
}
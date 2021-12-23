import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the interface for the InvertedIndexBuilder and ThreadSafeIndexBuilder
 * classes.
 */
public interface IndexBuilder {
	/** The log4j2 logger. */
	static final Logger log = LogManager.getLogger();
	
	/**
	 * Populates invertedIndex from mainPath.
	 *
	 * @param mainPath file location to be read
	 * @throws IOException file is invalid or can not be found
	 */
	void build(Path mainPath) throws IOException;
	
	/**
	 * Takes in a Path object and uses TextStemmer to parse through the text file(s) indicated
	 * by the Path and adds them to the invertedIndex HashMap.
	 *
	 * @param mainPath path that points to file/dir to be processed
	 * @throws IOException file is invalid or can not be found
	 */
	default void readFiles(Path mainPath) throws IOException {
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
			for(Path path: stream) {
				if(Files.isDirectory(path)) {
					readFiles(path);
				}
				else if(isTextFile(path)) {
					readFile(path);
				}
			}
		}
	}
	
	/**
	 * This checks to see if a path leads to a text file.
	 *
	 * @param path file path to be checked
	 * @return true if the path ends with the .txt or .text extension
	 */
	static boolean isTextFile(Path path) {
		String lower = path.toString().toLowerCase();
		return lower.endsWith(".txt") || lower.endsWith(".text");
	}
	
	/**
	 * Reads the file path into the default invertedIndex map of the builder.
	 *
	 * @param path file path to be read
	 * @throws IOException file is invalid or can not be found
	 */
	void readFile(Path path) throws IOException;
}

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the interface for the QueryResultBuilder and ThreadSafeQueryBuilder
 * classes.
 */
public interface QueryBuilder {
	/** The log4j2 logger. */
	static final Logger log = LogManager.getLogger();
	
	/**
	 * Populates queryList in invertedInverted from mainPath.
	 *
	 * @param mainPath file location to be read
	 * @param exact determines whether exact search should be performed
	 * @throws IOException file is invalid or can not be found
	 */
	void build(Path mainPath, boolean exact) throws IOException;
	
	/**
	 * Takes in a Path object and parses through the directory(s) and text file(s) within
	 * them. Uses the queries within the files to search in the index passed into builder.
	 *
	 * @param mainPath path that points to file/dir to be processed
	 * @param exact determines whether exact search should be performed
	 * @throws IOException file is invalid or can not be found
	 *
	 * @see #readQueryFile(Path, boolean)
	 */
	default void readQueryFiles(Path mainPath, boolean exact) throws IOException {
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
			for(Path path: stream) {
				if(Files.isDirectory(path)) {
					readQueryFiles(path, exact);
				}
				else if(IndexBuilder.isTextFile(path)) {
					readQueryFile(path, exact);
				}
			}
		}
	}
	
	/**
	 * Reads the file path into the  of the builder.
	 *
	 * @param path file path to be read
	 * @param exact determines whether exact search should be performed
	 * @throws IOException file is invalid or can not be found
	 *
	 * @see #readQueryLine(String, boolean)
	 */
	default void readQueryFile(Path path, boolean exact) throws IOException {
		try(BufferedReader br = Files.newBufferedReader(path)) {
			String line;
			while((line = br.readLine()) != null) {
				readQueryLine(line, exact);
			}
		}
	}
	
	/**
	 * Reads the line into the invertedIndex map's queryList of the
	 * builder.
	 *
	 * @param line query line from file to be read and possibly added
	 * @param exact determines whether exact search should be performed
	 */
	void readQueryLine(String line, boolean exact);
	
	/**
	 * Utilizes the JsonWriter class and writes out queryResult in JSON format out
	 * to output file.
	 *
	 * @param output path to the output file
	 * @throws IOException file is invalid or can not be found
	 */
	void resultToJson(Path output) throws IOException;
}

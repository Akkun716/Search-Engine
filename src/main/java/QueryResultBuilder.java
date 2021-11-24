import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class builds a list of queries from file reading as well as storing
 * query search results by an InvertedIndex (stored in a Map).
 */
public class QueryResultBuilder {
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final Map<String, List<InvertedIndex.QueryResult>> queryResult;

	/**
	 * This InvertedIndex will be a reference for the index passed into the function.
	 */
	private final InvertedIndex index;

	/**
	 * Initializes the queryResult and queryList instance members to a new
	 * TreeMap and ArrayList respectively.
	 * 
	 * @param index invertedIndex to be referenced
	 */
	public QueryResultBuilder(InvertedIndex index) {
		this.index = index;
		queryResult = new TreeMap<>();
	}

	/**
	 * Simple build() defaulting exact value to false.
	 *
	 * @param mainPath file location to be read
	 * @throws IOException file is invalid or can not be found
	 * 
	 * @see #build(Path, boolean)
	 */
	public void build(Path mainPath) throws IOException {
		build(mainPath, false);
	}
	
	/**
	 * Populates queryList in invertedInverted from mainPath.
	 *
	 * @param mainPath file location to be read
	 * @param exact determines whether exact search should be performed
	 * @throws IOException file is invalid or can not be found
	 *
	 * @see #readQueryFiles(Path, boolean)
	 * @see #readQueryFile(Path, boolean)
	 */
	public void build(Path mainPath, boolean exact) throws IOException {
		if(Files.isDirectory(mainPath)) {
			readQueryFiles(mainPath, exact);
		}
		else {
			readQueryFile(mainPath, exact);
		}
	}

	/**
	 * This checks to see if a path leads to a text file.
	 *
	 * @param path file path to be checked
	 * @return true if the path ends with the .txt or .text extension
	 */
	public static boolean isTextFile(Path path) {
		String lower = path.toString().toLowerCase();
		return lower.endsWith(".txt") || lower.endsWith(".text");
	}

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
	public void readQueryFiles(Path mainPath, boolean exact) throws IOException {
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
			for(Path path: stream) {
				if(Files.isDirectory(path)) {
					readQueryFiles(path, exact);
				}
				else if(isTextFile(path)) {
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
	public void readQueryFile(Path path, boolean exact) throws IOException {
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
	 * 
	 * @see #addResult(String, List)
	 */
	public void readQueryLine(String line, boolean exact) {
		var queries = TextStemmer.uniqueStems(line);
		var joined = String.join(" ", queries);

		if (!queries.isEmpty() && !queryResult.containsKey(joined)) {
			addResult(joined, index.search(queries, exact));
		}
	}

	/**
	 * Adds a single query match result to queryResult.
	 *
	 * @param queryLine String of query search stems
	 * @param results result of stem search from index
	 */
	public void addResult(String queryLine, List<InvertedIndex.QueryResult> results) {
		queryResult.put(queryLine, results);
	}

	/**
	 * Utilizes the JsonWriter class and writes out queryResult in JSON format out
	 * to output file.
	 *
	 * @param output path to the output file
	 * @throws IOException file is invalid or can not be found
	 */
	public void resultToJson(Path output) throws IOException {
			JsonWriter.asResult(queryResult, output);
	}
}
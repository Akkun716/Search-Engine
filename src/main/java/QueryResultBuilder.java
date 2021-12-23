import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class builds a list of queries from file reading as well as storing
 * query search results by an InvertedIndex (stored in a Map).
 */
public class QueryResultBuilder implements QueryBuilder {
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
		queryResult =  new TreeMap<>();
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
	 * Reads the line into the invertedIndex map's queryList of the
	 * builder.
	 *
	 * @param line query line from file to be read and possibly added
	 * @param exact determines whether exact search should be performed
	 *
	 * @see #addResult(String, Set, boolean)
	 */
	public void readQueryLine(String line, boolean exact) {
		var queries = TextStemmer.uniqueStems(line);
		var joined = String.join(" ", queries);

		if (!queries.isEmpty() && !queryResult.containsKey(joined)) {
			addResult(joined, queries, exact);
		}
	}

	/**
	 * Adds a single query match result to queryResult.
	 *
	 * @param queryLine String of query search stems
	 * @param queries set of queries to use for search
	 *
	 * @see #addResult(String, Set, boolean)
	 */
	private void addResult(String queryLine, Set<String> queries) {
		addResult(queryLine, queries, false);
	}

	/**
	 * Adds a single query match result to queryResult.
	 *
	 * @param queryLine String of query search stems
	 * @param queries set of queries to use for search
	 * @param exact determines whether exact search should be performed
	 */
	private void addResult(String queryLine, Set<String> queries, boolean exact) {
		queryResult.put(queryLine, index.search(queries, exact));
	}

	/**
	 * Determines if queryResult map contains the passed key.
	 *
	 * @param queryKey key to search in queryResult
	 * @return true if queryKey exists as key in queryResult
	 */
	public boolean containsResult(String queryKey) {
		return queryResult.containsKey(queryKey);
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
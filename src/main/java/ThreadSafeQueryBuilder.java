import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class builds a list of queries from file reading as well as storing
 * query search results by an InvertedIndex (stored in a Map).
 */
public class ThreadSafeQueryBuilder implements QueryBuilder {
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final Map<String, List<InvertedIndex.QueryResult>> queryResult = new TreeMap<>();
	
	/**
	 * This InvertedIndex will be a reference for the index passed into the function.
	 */
	private final ThreadSafeInvertedIndex index;
	
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final WorkQueue queue;

	/**
	 * Initializes the queryResult and queryList instance members to a new
	 * TreeMap and ArrayList respectively.
	 *
	 * @param index invertedIndex to be referenced
	 */
	public ThreadSafeQueryBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		this.index = index;
		this.queue = queue;
	}

	public void readQueryLine(String line, boolean exact) {
		queue.execute(new Task(line, exact));
	}

	public void build(Path mainPath, boolean exact) throws IOException {
		if(Files.isDirectory(mainPath)) {
			readQueryFiles(mainPath, exact);
		}
		else {
			readQueryFile(mainPath, exact);
		}
		
		queue.finish();
	}

	private void addResult(String queryLine, Set<String> queries, boolean exact) {
		List<InvertedIndex.QueryResult> tempList = index.search(queries, exact);
		synchronized(queryResult) {
			queryResult.put(queryLine, tempList);
		}
	}
	
	public boolean containsResult(String queryKey) {
		synchronized(queryResult) {
			return queryResult.containsKey(queryKey);
		}
	}
	
	/**
	 * Utilizes the JsonWriter class and writes out queryResult in JSON format out
	 * to output file.
	 *
	 * @param output path to the output file
	 * @throws IOException file is invalid or can not be found
	 */
	public void resultToJson(Path output) throws IOException {
		synchronized(queryResult) {
			JsonWriter.asResult(queryResult, output);
		}
	}

	public class Task implements Runnable {
		String fileLine;
		boolean exact;

		public Task(String line, boolean exact) {
			this.fileLine = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			synchronized(queryResult) {
				var queries = TextStemmer.uniqueStems(fileLine);
				var joined = String.join(" ", queries);

				if (!queries.isEmpty() && !containsResult(joined)) {
					addResult(joined, queries, exact);
				}
			}
		}
	}
}
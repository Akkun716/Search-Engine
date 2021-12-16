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
public class ThreadSafeQueryBuilder extends QueryResultBuilder{
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final WorkQueue queue;

	/** This will be the read/write lock needed for multithreading. */
//	IndexReadWriteLock lock;

	/**
	 * Initializes the queryResult and queryList instance members to a new
	 * TreeMap and ArrayList respectively.
	 *
	 * @param index invertedIndex to be referenced
	 */
	public ThreadSafeQueryBuilder(InvertedIndex index, WorkQueue queue) { // TODO ThreadSafeInvertedIndex
		super(index);
		this.queue = queue;
//		this.lock = new IndexReadWriteLock();
	}

	@Override
	public void readQueryLine(String line, boolean exact) {
		queue.execute(new Task(line, exact));
	}

	@Override
	public void build(Path mainPath, boolean exact) throws IOException {
		super.build(mainPath, exact);
		try {
			queue.finish();
		}
		catch(InterruptedException e) {
			log.debug("An Interruption error was thrown and needs to be handled.");
		}
	}

	@Override
	protected void addResult(String queryLine, Set<String> queries, boolean exact) {
		List<InvertedIndex.QueryResult> tempList = index.search(queries, exact);
		synchronized(queryResult) {
			queryResult.put(queryLine, tempList);
		}
	}

	// TODO Need to make containsResult thread-safe

	public class Task implements Runnable {
		String fileLine;
		boolean exact;

		public Task(String line, boolean exact) {
			this.fileLine = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			synchronized(ThreadSafeQueryBuilder.class) { // TODO Could address this by using an interface instead
				var queries = TextStemmer.uniqueStems(fileLine);
				var joined = String.join(" ", queries);

				if (!queries.isEmpty() && !containsResult(joined)) {
					addResult(joined, queries, exact);
				}
			}
		}
	}
}
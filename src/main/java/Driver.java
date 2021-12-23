import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Adon Anglon
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2021
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		Instant start = Instant.now();

		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index;
		IndexBuilder indexBuilder;
		QueryBuilder queryBuilder;
		WorkQueue queue = null;
		Path input, output;

		if(map.hasFlag("-threads")) {
			Integer threads = map.getInteger("-threads");
			if(threads == null || threads <= 0) {
				queue = new WorkQueue();
			}
			else {
				queue = new WorkQueue(threads);
			}

			System.out.println(queue.size());

			ThreadSafeInvertedIndex safeIndex = new ThreadSafeInvertedIndex();
			index = safeIndex;
			indexBuilder = new ThreadSafeIndexBuilder(safeIndex, queue);
			queryBuilder = new ThreadSafeQueryBuilder(safeIndex, queue);
		}
		else {
			index = new InvertedIndex();
			indexBuilder = new InvertedIndexBuilder(index);
			queryBuilder = new QueryResultBuilder(index);
		}

		if(map.hasFlag("-text")) {
			input = map.getPath("-text");
			try {
				indexBuilder.build(input);
			}
			catch(Exception e) {
				if(input == null) {
					System.out.println("Unable to build the inverted index from unreadable path");
				}
				else {
					System.out.println("Unable to build the inverted index from path: " + input.toString());
				}
			}
		}

		if(map.hasFlag("-query") && map.getPath("-query") != null) {
			input = map.getPath("-query");
			try {
				queryBuilder.build(input, map.hasFlag("-exact"));
			}
			catch(Exception e) {
				System.out.println("Unable to search from path: " + input.toString());
			}
		}

		if(map.hasFlag("-results")) {
			output = map.getPath("-results", Path.of("results.json"));
			try {
				queryBuilder.resultToJson(output);
			}
			catch(Exception e) {
				System.out.println("Unable to write out to file: " + output.toString());
			}
		}

		if(map.hasFlag("-counts")) {
			output = map.getPath("-counts", Path.of("counts.json"));
			try {
				index.countToJson(output);
			}
			catch(Exception e) {
				System.out.println("Unable to write out to file: " + output.toString());
			}
		}

		if(map.hasFlag("-index")) {
			output = map.getPath("-index", Path.of("index.json"));
			try {
				index.indexToJson(output);
			}
			catch(Exception e) {
				System.out.println("Unable to write out to file: " + output.toString());
			}
		}

		if(queue != null) {
			queue.join();
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
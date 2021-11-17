import java.nio.file.Files;
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

		InvertedIndex index = new InvertedIndex();
		InvertedIndexBuilder indexBuilder = new InvertedIndexBuilder(index);
		QueryResultBuilder queryBuilder = new QueryResultBuilder();
		ArgumentMap map = new ArgumentMap(args);

		if(map.hasFlag("-text")) {
			Path input = map.getPath("-text");
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
			Path input = map.getPath("-query");
			try {
				if(Files.exists(input)) {
					queryBuilder.build(input);
				}

				index.search(queryBuilder, map.hasFlag("-exact"));
			}
			catch(Exception e) {
				System.out.println("Unable to search from path: " + input.toString());
			}
		}

		if(map.hasFlag("-results")) {
			Path output = map.getPath("-results", Path.of("results.json"));
			try {
					queryBuilder.resultToJson(output);
			}
			catch(Exception e) {
				System.out.println("Unable to write out to file: " + output.toString());
			}
		}

		if(map.hasFlag("-counts")) {
			Path output = map.getPath("-counts", Path.of("counts.json"));
			try {
				index.countToJson(output);
			}
			catch(Exception e) {
				System.out.println("Unable to write out to file: " + output.toString());
			}
		}

		if(map.hasFlag("-index")) {
			Path output = map.getPath("-index", Path.of("index.json"));
			try {
				index.indexToJson(output);
			}
			catch(Exception e) {
				System.out.println("Unable to write out to file: " + output.toString());
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
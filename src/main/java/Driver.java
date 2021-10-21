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
		InvertedIndex index = new InvertedIndex();
		Instant start = Instant.now();

		ArgumentMap map = new ArgumentMap(args);

		if(map.hasFlag("-text")) {
			Path input = map.getPath("-text");
			try {
				index.readFiles(map.getPath("-text"));
			}
			catch(Exception e) {
				System.out.println("Unable to build the inverted index from path: " + input.toString());
			}
		}
		
		if(map.hasFlag("-index")) {
			Path output = map.getPath("-index", Path.of("index.json"));
			try {
				if(map.hasValue("-index")) {
					index.writeFile(output);
				}
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
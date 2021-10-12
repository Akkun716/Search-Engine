import java.io.IOException;
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
	// TODO Slight improvement to exception handling
	// TODO Variable names
	// TODO Some shift of classes and methods

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		InvertedIndex invertInd = new InvertedIndex(); // TODO index
		Instant start = Instant.now();

		ArgumentMap argMap = new ArgumentMap(args); // TODO map

		try {
			if(argMap.hasFlag("-text")) {
				invertInd.readFiles(argMap.getPath("-text"));
			}
			if(argMap.hasFlag("-index")) {
				if(argMap.hasValue("-index")) {
					invertInd.writeFile(Path.of(System.getProperty("user.dir"), argMap.getString("-index")));
				}
				else {
					invertInd.writeFile(Path.of(System.getProperty("user.dir"), "index.json"));
				}
			}
		} catch(IOException e) {
			System.out.println("[The file could not be found]");
		} catch(Exception e) {
			System.out.println("Something is wrong...");
		}

		/* TODO
		if(argMap.hasFlag("-text")) {
			Path input = argMap.getPath("-text");
			try {
				invertInd.readFiles();
			}
			catch (...) {
				Unable to build the inverted index from path: + input.toString();
			}
		}

		if(argMap.hasFlag("-index")) {
			Path output = argMap.getPath("-index", Path.of("index.json"));

			try {
				invertInd.writeFile(output);
			}
			catch ( ) {
				etc
			}
		}
		*/

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
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
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		InvertedIndex invertInd = new InvertedIndex();
		Instant start = Instant.now();

		ArgumentMap argMap = new ArgumentMap(args);
		
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

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
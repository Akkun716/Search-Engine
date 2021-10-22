import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * This class holds word stems and all of the files and positions within those
 * files they have been found in.
 *
 * @author Adon Anglon
 */
public class InvertedIndex {
	/**
	 * This map will hold stemmed words as keys and a treeMap as values. Those treeMaps
	 * will hold file locations as keys and arrayList of Integers as values. These
	 * Integers represent the position of the stemmed word occurrences
	 */
	
	public final InvertedIndexBuilder invertedIndex = new InvertedIndexBuilder();

	/**
	 * Takes in a Path object and uses TextStemmer to parse through the text file(s) indicated
	 * by the Path and adds them to the invertedIndex HashMap.
	 *
	 * @param mainPath path that points to file/dir to be processed
	 * @throws IOException file is invalid or can not be found
	 */
	public void readFiles(Path mainPath) throws IOException{
		if(Files.isDirectory(mainPath)) {
			try(DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
				for(Path path: stream) {
					if(Files.isDirectory(path)) {
						readFiles(path);
					}
					else if(path.toString().toLowerCase().endsWith(".txt") ||
							path.toString().toLowerCase().endsWith(".text")) {
						passToIndex(path, TextStemmer.listStems(path));
					}
				}
			}
		}
		else {
			passToIndex(mainPath, TextStemmer.listStems(mainPath));
		}
	}

	/**
	 * Parses through input word stem list and places occurrences into invertedIndex.
	 * If word stem does not exist in index or file location does not exist in word stem
	 * index, creates new key and adds relevant information.
	 *
	 * @param path path that points to file/dir to be processed
	 * @param input List of parsed stems from designated Path
	 */
	private void passToIndex(Path path, List<String> input) {
		int i = 1;
		for(String stem: input) {
			invertedIndex.add(stem, path.toString(), i++);
		}
	}

	/**
	 * Utilizes the JsonWriter class and writes out invertedIndex in JSON format out
	 * to output file.
	 *
	 * @param output path to the output file
	 * @throws IOException file is invalid or can not be found
	 */
	public void writeFile(Path output) throws IOException {
			JsonWriter.asNestedObject(invertedIndex.getIndex(), output);
	}
}
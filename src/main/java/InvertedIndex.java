import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author aanglon
 * 
 */
public class InvertedIndex {
	/**
	 * This map will hold stemmed words as keys and a treeMap as values. Those treeMaps
	 * will hold file locations as keys and arrayList of Integers as values. These
	 * Integers represent the position of the stemmed word occurrences
	 */
	private Map<String, TreeMap<String, ArrayList<Integer>>> invertedIndex = 
			new TreeMap<>();

	/**
	 * Takes in a Path object and uses TextStemmer to parse through the text file(s) indicated
	 * by the Path and adds them to the invertedIndex HashMap.
	 * 
	 * @param mainPath path that points to file/dir to be processed
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
			catch(IOException e) {
				System.out.println("[Directory could not be found]");
			}
		}
		else {
			try {
				passToIndex(mainPath, TextStemmer.listStems(mainPath));
			}
			catch(IOException e) {
				System.out.println("[File could not be found]");
			}
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
			if(!invertedIndex.containsKey(stem)) {
				invertedIndex.put(stem, new TreeMap<>());
			}
			if(!invertedIndex.get(stem).containsKey(path.toString())) {
				invertedIndex.get(stem).put(path.toString(), new ArrayList<Integer>());
			}
			invertedIndex.get(stem).get(path.toString()).add(i++);
		}
	}
	
	/**
	 * Returns an unmodifiable invertedIndex for easy printing.
	 * 
	 * @return
	 */
	
	public Map<String, Map<String, ArrayList<Integer>>> getMap() {
		return Collections.unmodifiableMap(invertedIndex);
	}
	
	/**
	 * Utilizes the JsonWriter class and writes out invertedIndex in JSON format out
	 * to output file.
	 * 
	 * @param output path to the output file
	 * @throws IOException
	 */
	public void writeFile(Path output) throws IOException {
			JsonWriter.asNestedObject(invertedIndex, output);
	}
}
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This class holds word stems and all of the files and positions within those
 * files they have been found in.
 *
 * @author Adon Anglon
 */
public class InvertedIndexBuilder {
	/**
	 * This map will hold stemmed words as keys and a treeMap as values. Those treeMaps
	 * will hold file locations as keys and arrayList of Integers as values. These
	 * Integers represent the position of the stemmed word occurrences
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * Passes an invertedIndex into the class to be alterable.
	 * 
	 * @param invertedIndex invertedIndex to be entered
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	/**
	 * Takes in a Path object and uses TextStemmer to parse through the text file(s) indicated
	 * by the Path and adds them to the invertedIndex HashMap.
	 *
	 * @param mainPath path that points to file/dir to be processed
	 * @throws IOException file is invalid or can not be found
	 */
	public void readFiles(Path mainPath) throws IOException {
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
			for(Path path: stream) {
				if(Files.isDirectory(path)) {
					readFiles(path);
				}
				else if(isTextFile(path)) {
					readFile(path);
				}
			}
		}
	}
	
	public void readQueryFile(Path path) throws IOException {
		readQueryFile(path, this.invertedIndex);
	}
	
	public static void readQueryFile(Path path, InvertedIndex invertedIndex) throws IOException {
		try(BufferedReader br = Files.newBufferedReader(path)) {
			Stemmer stemmer = new SnowballStemmer(TextStemmer.ENGLISH);
			String line;
			while((line = br.readLine()) != null) {
				invertedIndex.addQuery(TextStemmer.uniqueStems(line, stemmer));
			}
		}
	}

	/**
	 * This checks to see if a path leads to a text file.
	 *
	 * @param path file path to be checked
	 * @return true if the path ends with the .txt or .text extension
	 */
	public static boolean isTextFile(Path path) {
		return path.toString().toLowerCase().endsWith(".txt") ||
				path.toString().toLowerCase().endsWith(".text");
	}

	/**
	 * Reads the file path into the default invertedIndex map of the builder.
	 *
	 * @param path file path to be read
	 * @throws IOException file is invalid or can not be found
	 */
	public void readFile(Path path) throws IOException {
		readFile(path, this.invertedIndex);
	}

	/**
	 * Reads the file path into the specified invertedIndex.
	 *
	 * @param path file path to be read
	 * @param invertedIndex the index that will append the stemmed words from the
	 * 	file
	 * @throws IOException file is invalid or can not be found
	 */
	public static void readFile(Path path, InvertedIndex invertedIndex) throws IOException {
		try(BufferedReader br = Files.newBufferedReader(path)) {
			Stemmer stemmer = new SnowballStemmer(TextStemmer.ENGLISH);
			String line, pathString = path.toString();
			int i = 1;
			while((line = br.readLine()) != null) {
				for(String word: TextParser.parse(line)) {
					invertedIndex.add(stemmer.stem(word).toString(), pathString, i++);
				}
			}
			invertedIndex.addWordCount(pathString, i - 1);
		}
	}

	/**
	 * Outputs the built invertedIndex.
	 *
	 * @return invertedIndex currently stored in builder
	 */
	public void build(Path mainPath) throws IOException
	{
		if(Files.isDirectory(mainPath)) {
			readFiles(mainPath);
		}
		else {
			readFile(mainPath);
		}
	}
	
	public void buildQuery(Path mainPath) throws IOException {
		readQueryFile(mainPath);
	}

}
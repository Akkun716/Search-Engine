import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This class holds word stems and all of the files and positions within those
 * files they have been found in.
 *
 * @author Adon Anglon
 */
public class InvertedIndexBuilder implements IndexBuilder {
	/**
	 * This InvertedIndex will be a reference for the index passed into the function.
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * Passes an invertedIndex into the class to be altered.
	 *
	 * @param invertedIndex invertedIndex to be entered
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

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
		}
	}

	public void build(Path mainPath) throws IOException
	{
		if(Files.isDirectory(mainPath)) {
			readFiles(mainPath);
		}
		else {
			readFile(mainPath);
		}
	}
}
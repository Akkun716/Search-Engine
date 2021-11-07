import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @see TextParser
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2021
 */
public class TextStemmer {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM ENGLISH = SnowballStemmer.ALGORITHM.ENGLISH;

	/** The default character set used by this class. */
	public static final Charset UTF8 = StandardCharsets.UTF_8;

	/**
	 * This stems the words in a line and adds them to the collection object passed
	 * in the function.
	 * 
	 * @param line line of text to be parsed and each word stemmed
	 * @param stemmer Stemmer object instance which will clean and parse the line
	 * @param output Collection object each stemmed word will be added to 
	 */
	public static void stemLine(String line, Stemmer stemmer, Collection<String> output) {
		for(String word: TextParser.parse(line)) {
			output.add(stemmer.stem(word).toString());
		}
	}

	/**
	 * Parses each line into cleaned and stemmed words.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see stemLine(String, Stemmer, Collection)
	 */
	public static List<String> listStems(String line, Stemmer stemmer) {
		List<String> output = new ArrayList<>();
		stemLine(line, stemmer, output);
		return output;
	}

	/**
	 * Parses each line into cleaned and stemmed words using the default stemmer.
	 *
	 * @param line the line of words to parse and stem
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see SnowballStemmer
	 * @see #ENGLISH
	 * @see #listStems(String, Stemmer)
	 */
	public static List<String> listStems(String line) {
		return listStems(line, new SnowballStemmer(ENGLISH));
	}

	/**
	 * Rather than just a single line (like in listStem), a file is read through
	 * via BufferedReader and listStem is called on each line.
	 * 
	 * @param input the file path to be parsed each word stemmed
	 * @param output the Collection object each stemmed word will be added to
	 * @throws IOException if unable to read or parse file
	 * 
	 * @see SnowballStemmer
	 * @see #ENGLISH
	 * @see stemLine(String, Stemmer, Collection)
	 */
	public static void bufferedStem(Path input, Collection<String> output) throws IOException {
		try(BufferedReader br = Files.newBufferedReader(input)) {
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			String line;
			while((line = br.readLine()) != null) {
				stemLine(line, stemmer, output);
			}
		}
	}
	
	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words
	 * using the default stemmer.
	 *
	 * @param input the input file to parse and stem
	 * @return a list of stems from file in parsed order
	 * @throws IOException if unable to read or parse file
	 *
	 * @see bufferedStem(Path, Collection)
	 */
	public static List<String> listStems(Path input) throws IOException {
		List<String> output = new ArrayList<>();
		bufferedStem(input, output);
		return output;
	}

	/**
	 * Parses the line into unique, sorted, cleaned, and stemmed words.
	 *
	 * @param line the line of words to parse and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see stemLine(String, Stemmer, Collection)
	 */
	public static Set<String> uniqueStems(String line, Stemmer stemmer) {
		Set<String> output = new TreeSet<>();
		stemLine(line, stemmer, output);
		return output;
	}

	/**
	 * Parses the line into unique, sorted, cleaned, and stemmed words using the
	 * default stemmer.
	 *
	 * @param line the line of words to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #ENGLISH
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static Set<String> uniqueStems(String line) {
		return uniqueStems(line, new SnowballStemmer(ENGLISH));
	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned, and
	 * stemmed words using the default stemmer.
	 *
	 * @param input the input file to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see bufferedStem(Path, Collection)
	 */
	public static Set<String> uniqueStems(Path input) throws IOException {
		Set<String> output = new TreeSet<>();
		bufferedStem(input, output);
		return output;
	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned, and
	 * stemmed words using the default stemmer, and adds the set of unique sorted
	 * stems to a list per line in the file.
	 *
	 * @param input the input file to parse and stem
	 * @return a list where each item is the set of unique sorted stems parsed from
	 *   a single line of the input file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #UTF8
	 * @see #ENGLISH
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static List<Set<String>> listUniqueStems(Path input) throws IOException {
		try(BufferedReader br = Files.newBufferedReader(input)) {
			ArrayList<Set<String>> output = new ArrayList<Set<String>>();
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			String line;
			
			while((line = br.readLine()) != null) {
				output.add(uniqueStems(line, stemmer));
			}
			return output;
		}
	}
}
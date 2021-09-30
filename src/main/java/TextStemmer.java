import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
	 * Parses each line into cleaned and stemmed words.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static List<String> listStems(String line, Stemmer stemmer) {
		List<String> output = new ArrayList<>();
		for(String word: TextParser.parse(line)) {
			output.add(stemmer.stem(word.toLowerCase()).toString());
		}
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
	 * Reads a file line by line, parses each line into cleaned and stemmed words
	 * using the default stemmer.
	 *
	 * @param input the input file to parse and stem
	 * @return a list of stems from file in parsed order
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #UTF8
	 * @see #uniqueStems(String, Stemmer)
	 * @see TextParser#parse(String)
	 */
	public static List<String> listStems(Path input) throws IOException {
		List<String> output = new ArrayList<>();
		try(BufferedReader br = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
			String line;
			while((line = br.readLine()) != null) {
				output.addAll(listStems(line));
			}
		} catch(IOException e) {
			throw new IOException();
		}
//		for(String line: Files.readAllLines(input)) {
//			temp = listStems(line);
//			Collections.addAll(output, temp.toArray(new String[temp.size()]));
//		}
//		System.out.println(output);
//		Lets see how this goes
		return output;
	}

	/**
	 * Parses the line into unique, sorted, cleaned, and stemmed words.
	 *
	 * @param line the line of words to parse and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static Set<String> uniqueStems(String line, Stemmer stemmer) {
		Set<String> output = new TreeSet<>();
		for(String word: TextParser.parse(line)) {
			output.add(stemmer.stem(word.toLowerCase()).toString());
		}
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
	 * @see #UTF8
	 * @see #uniqueStems(String, Stemmer)
	 * @see TextParser#parse(String)
	 */
	public static Set<String> uniqueStems(Path input) throws IOException {
		Set<String> output = new TreeSet<>();
		try(BufferedReader br = Files.newBufferedReader(input)) {
			String line;
			while((line = br.readLine()) != null) {
				output.addAll(uniqueStems(line));
			}
		} catch(IOException e) {
			throw new IOException();
		}
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
		ArrayList<Set<String>> output = new ArrayList<Set<String>>();
		
		try(BufferedReader br = Files.newBufferedReader(input)) {
			String line;
			while((line = br.readLine()) != null) {
				output.add(uniqueStems(line));
			}
		} catch(IOException e) {
			throw new IOException();
		}
		return output;
	}

	/**
	 * Demonstrates this class.
	 *
	 * @param args unused
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		String text = """
				practic practical practice practiced practicer practices
				practicing practis practisants practise practised practiser
				practisers practises practising practitioner practitioners
				""";

		System.out.println(listStems(text));
		System.out.println(uniqueStems(text));

		Path base = Path.of("src", "test", "resources", "input.txt");
		System.out.println(listStems(base));
		System.out.println(uniqueStems(base));
		System.out.println(listUniqueStems(base));
	}
}

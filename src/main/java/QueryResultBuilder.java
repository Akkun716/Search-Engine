import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This class builds a list of queries from file reading as well as storing
 * query search results by an InvertedIndex (stored in a Map). 
 */
public class QueryResultBuilder {
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final Map<String, List<InvertedIndex.QueryResult>> queryResult;

	/**
	 * This List hold query line sets for future searching.
	 */
	private final List<Set<String>> queryList;
	
	/**
	 * Initializes the queryResult and queryList instance members to a new
	 * TreeMap and ArrayList respectively.
	 */
	public QueryResultBuilder() {
		queryResult = new TreeMap<>();
		queryList = new ArrayList<>();
	}
	
	/**
	 * Takes in a Path object and uses TextStemmer to parse through the text file(s)
	 * indicated by the Path and adds them to the invertedIndex HashMap.
	 *
	 * @param mainPath path that points to file/dir to be processed
	 * @throws IOException file is invalid or can not be found
	 */
	public void readQueryFiles(Path mainPath) throws IOException {
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
			for(Path path: stream) {
				if(Files.isDirectory(path)) {
					readQueryFiles(path);
				}
				else if(isTextFile(path)) {
					readQueryFile(path);
				}
			}
		}
	}

	/**
	 * Reads the file path into the default invertedIndex map's queryList of the
	 * builder.
	 *
	 * @param path file path to be read
	 * @throws IOException file is invalid or can not be found
	 */
	public void readQueryFile(Path path) throws IOException {
		readQueryFile(path, queryList);
	}

	/**
	 * Reads the file path into the specified invertedIndex's queryList.
	 *
	 * @param path file path to be read
	 * @param queryList the list that will append the stemmed words from the
	 * 	file
	 * @throws IOException file is invalid or can not be found
	 * 
	 * @see #addQuery(Set, List)
	 */
	public static void readQueryFile(Path path, List<Set<String>> queryList) throws IOException {
		try(BufferedReader br = Files.newBufferedReader(path)) {
			Stemmer stemmer = new SnowballStemmer(TextStemmer.ENGLISH);
			String line;
			Set<String> query;
			while((line = br.readLine()) != null) {
				query = TextStemmer.uniqueStems(line, stemmer);
				addQuery(query, queryList);
			}
		}
	}
	
	/**
	 * Adds a single query line to queryList.
	 *
	 * @param query multi stem query represented as a Set
	 * @param queryList list of query sets to be added to
	 * @return true if the set is updated with the query
	 */
	public static boolean addQuery(Set<String> query, List<Set<String>> queryList) {
		return query.isEmpty() || queryList.contains(query)
				? false
				: queryList.add(query);
	}
	
	/**
	 * Adds a single query match result to queryResult.
	 * 
	 * @param queryLine String of query search stems
	 * @param results result of stem search from index
	 */
	public void addResult(String queryLine, List<InvertedIndex.QueryResult> results) {
		queryResult.put(queryLine, results);
	}

	/**
	 * This checks to see if a path leads to a text file.
	 *
	 * @param path file path to be checked
	 * @return true if the path ends with the .txt or .text extension
	 */
	public static boolean isTextFile(Path path) {
		String lower = path.toString().toLowerCase();
		return lower.endsWith(".txt") || lower.endsWith(".text");
	}
	
	/**
	 * Populates queryList in invertedInverted from mainPath.
	 *
	 * @param mainPath file location to be read
	 * @throws IOException file is invalid or can not be found
	 * 
	 * @see #readQueryFiles(Path)
	 * @see #readQueryFile(Path)
	 */
	public void build(Path mainPath) throws IOException {
		if(Files.isDirectory(mainPath)) {
			readQueryFiles(mainPath);
		}
		else {
			readQueryFile(mainPath);
		}
	}
	
	/**
	 * Searches through the invertedIndex using the list of query requests.
	 * 
	 * @param invertedIndex invertedIndex to use
	 * @param exact boolean determining whether to use partial or exact search
	 */
	public void search(InvertedIndex invertedIndex, boolean exact) {
		var queryIterator = queryList.iterator();
		
		if(exact) {
			while(queryIterator.hasNext()) {
				invertedIndex.exactSearch(queryIterator.next(), this);
			}
		}
		else {
			while(queryIterator.hasNext()) {
				invertedIndex.partialSearch(queryIterator.next(), this);
			}
		}
	}
	
	/**
	 * Utilizes the JsonWriter class and writes out queryResult in JSON format out
	 * to output file.
	 *
	 * @param output path to the output file
	 * @throws IOException file is invalid or can not be found
	 */
	public void resultToJson(Path output) throws IOException {
			JsonWriter.asResult(queryResult, output);
	}
}
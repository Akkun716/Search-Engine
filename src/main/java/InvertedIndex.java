import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class represents the data structure of the inverted index.
 */
public class InvertedIndex {
	/**
	 * This map hold the data of the inverted index. The over-arching map holds
	 * word stem keys which are paired to respective maps that has file location
	 * keys which are then paired to an array of positions in the file the word
	 * stem appeared.
	 */
	private final Map<String, TreeMap<String, TreeSet<Object>>> invertedIndex;

	/**
	 * This WordCount map holds the word count of the files included in the
	 * invertedIndex map.
	 */
	public final Map<String, Object> wordCount;

	/*
	 * TODO Move these data structures (queryResult and queryList) into a new
	 * class... QueryParser or QueryResultBuilder class and move the
	 * InvertedIndexBuilder.readQueryFile methods there too.
	 */
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	public final Map<String, List<QueryResult>> queryResult;

	/**
	 * This List hold query line sets for future searching.
	 */
	private final List<Set<String>> queryList;

	/**
	 * Initializes invertedIndex and wordCount to new empty TreeMap objects.
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<>();
		wordCount = new TreeMap<>();
		queryResult = new TreeMap<>();
		queryList = new ArrayList<>();
	}

	/**
	 * Adds  word keys, file location keys, and positions in the index
	 * (if they does not already exist in the index).
	 *
	 * @param word stemmed word
	 * @param location file location where the word stem appeared
	 * @param position position of the word stem in respective file location
	 * @return true if the new key value pair did not exist and was added to map
	 */
	public boolean add(String word, String location, Integer position) {
		invertedIndex.putIfAbsent(word, new TreeMap<>());
		invertedIndex.get(word).putIfAbsent(location, new TreeSet<Object>());
		return invertedIndex.get(word).get(location).add(position);

		/*
		 * TODO Update the word count here instead! Slightly less efficient, but better
		 * correctness and encapsulation. Remove any other public ways of modifying the
		 * word count. There are two ways of solving this problem: (1) add 1 to the
		 * existing word count if a new word was added to the index, or (2) use the max
		 * word position as the word count (e.g. if hello was found in hello.txt in
		 * position 42, you know there were at least 42 words in that file). Either
		 * option works, but (1) is slightly harder to deal with when multithreading.
		 */

	}

	/**
	 * Adds  word keys, file location keys, and positions in the index
	 * (if they does not already exist in the index).
	 *
	 * @param words list of stemmed words
	 * @param location file location where the word stem appeared
	 */
	public void addAll(List<String> words, String location) {
		int i = 1;
		for(String word: words) {
			add(word, location, i++);
		}
	}

	/**
	 * Adds the word count of a file to the wordCount map.
	 *
	 * @param location file location being referenced
	 * @param count the word count of the file location
	 * @return true if the count param changes the stored value in wordCount
	 */
	public boolean addWordCount(String location, Integer count) {
		return count > 0
				? setWordCount(location, count)
				: false;
	}

	/**
	 * Sets a word count value to a file location key.
	 *
	 * @param location name of file location to be assigned as key
	 * @param count word count of referenced file
	 * @return true if the map is updated with new (or changed) key value pair
	 */
	public boolean setWordCount(String location, Integer count) {
		if(wordCount.containsKey(location) && wordCount.get(location) == count) {
				return false;
		}
		wordCount.put(location, count);
		return true;
	}

	/**
	 * Adds a single query line to queryList.
	 *
	 * @param query multi stem query represented as a Set
	 * @return true if the set is updated with the query
	 */
	public boolean addQuery(Set<String> query) {
		return query.isEmpty() || queryList.contains(query)
				? false
				: queryList.add(query);
	}

	/**
	 * Searches through the inverted index for stems that match (exact or partial)
	 * the stems in queries.
	 *
	 * @param exact represents if exact search should be executed
	 */
	public void search(boolean exact) {
		var queryIterator = queryList.iterator();

		if(exact) {
			exactSearch(queryIterator);
		}
		else {
			partialSearch(queryIterator);
		}
	}

	/**
	 * Joins the queryResults of same stem and location, then sorts the list.
	 *
	 * @param results list of queryResults to be cleaned and sorted
	 */
	public void cleanSortResults(List<QueryResult> results) {
		for(int elemIndex = 0; elemIndex < results.size() - 1; elemIndex++) {
			for(int nextElem = elemIndex + 1; nextElem < results.size(); nextElem++) {
				if(results.get(elemIndex).getLocation().equals(results.get(nextElem).getLocation())) {
					results.get(elemIndex).combine(results.get(nextElem));
					results.remove(nextElem);
					nextElem--;
				}
			}
		}
		// TODO Collections.sort(results);
		QuickSort.sort(results);
	}

	/**
	 * Searches through the invertedIndex to fond exact matches to the query stems.
	 *
	 * @param queryIterator iterator of the queryList (list of stems from query)
	 */
	// TODO public void exactSearch(Set<String> elem) {
	public void exactSearch(Iterator<Set<String>> queryIterator) {
		List<QueryResult> results;
		QueryResult newQuery;
		int occurrence = 0;
		//While there are queries in list
		while(queryIterator.hasNext()) {
			/*
			 * TODO Adjust exactSearch so that it starts basically inside this loop
			 * and works on a single Set<String> of queries...
			 *
			 * Move the looping through ALL of the lines into the new query class.
			 */
			results = new ArrayList<>();
			//Retrieved query
			var elem = queryIterator.next();
			//For every stem in query
			for(String stem: elem) {
				//If the index has the query stem
				if(hasStem(stem)) {
					//For every entry under that stem
					for(String fileLocation: getLocations(stem)) {
						occurrence = invertedIndex.get(stem).get(fileLocation).size();
						newQuery = new QueryResult((Integer) wordCount.get(fileLocation), occurrence, fileLocation);
						results.add(newQuery);
					}
				}
			}
			cleanSortResults(results);
			queryResult.put(String.join(" ", elem), results);
		}
	}

	/**
	 * Searches through the invertedIndex to find matches that start with the query
	 * stems.
	 *
	 * @param queryIterator iterator of the queryList (list of stems from query)
	 */
	public void partialSearch(Iterator<Set<String>> queryIterator) {
		List<QueryResult> results;
		QueryResult newQuery;
		int occurrence = 0;
		//While there are queries in list
		while(queryIterator.hasNext()) {
			results = new ArrayList<>();
			//Retrieved query
			var elem = queryIterator.next();
			//For every stem in query
			for(String stem: elem) {
				//For every entry under that stem
				for(String stemKey: invertedIndex.keySet()) {
					if(stemKey.startsWith(stem)) {
						for(String fileLocation: getLocations(stemKey)) {
							occurrence = invertedIndex.get(stemKey).get(fileLocation).size();
							newQuery = new QueryResult((Integer) wordCount.get(fileLocation), occurrence, fileLocation);
							results.add(newQuery);
						}
					}
				}
			}
			cleanSortResults(results);
			queryResult.put(String.join(" ", elem), results);
		}
	}

	/**
	 * Returns an unmodifiable set of word stems from invertedIndex.
	 *
	 * @return an unmodifiable index map
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(invertedIndex.keySet());
	}

	/**
	 * Returns an unmodifiable set of file locations of a word stem from
	 * invertedIndex.
	 *
	 * @param stem word stem that needs to be accessed
	 * @return an unmodifiable Set of positions
	 */
	public Set<String> getLocations(String stem) {
		return hasStem(stem)
				? Collections.unmodifiableSet(invertedIndex.get(stem).keySet())
				: Collections.emptySet();
	}

	/**
	 * Returns an unmodifiable Set of word stem positions at file location.
	 *
	 * @param stem word stem that needs to be accessed
	 * @param location file location that needs to be accessed
	 * @return an unmodifiable Set of positions
	 */
	public Set<Object> getPositions(String stem, String location) {
		return hasLocation(stem, location)
				? Collections.unmodifiableSet(invertedIndex.get(stem).get(location))
				: Collections.emptySet();
	}

	/**
	 * Checks if word stem exists as a key in index.
	 *
	 * @param stem word stem to be found in index
	 * @return true if word stem exists as key in map
	 */
	public boolean hasStem(String stem) {
		return invertedIndex.containsKey(stem);
	}

	/**
	 * Checks if the word stems exists and then if location can be found.
	 *
	 * @param stem word stem to be found in index
	 * @param location file location to be found under word stem key
	 * @return true if location exists under word stem key
	 */
	public boolean hasLocation(String stem, String location) {
		return hasStem(stem) && invertedIndex.get(stem).containsKey(location);
	}

	/**
	 * Checks if the word and location keys exists in index and then if position
	 * can be found.
	 *
	 * @param stem word stem to be found in index
	 * @param location file location to be found under word stem key
	 * @param position position of the word stem at designated file location
	 * @return true if position exists under word stem and designated file location
	 */
	public boolean hasPosition(String stem, String location, Integer position) {
		return hasLocation(stem, location)
				&& invertedIndex.get(stem).get(location).contains(position);
	}

	/**
	 * Returns number of word stem keys in index.
	 *
	 * @return size of invertedIndex map
	 */
	public int stemCount() {
		return invertedIndex.size();
	}

	/**
	 * Returns number of file locations under word stem key in index.
	 *
	 * @param stem word stem key to be accessed
	 * @return size of map assigned to word stem if exists; else 0
	 */
	public int locationCount(String stem) {
		return hasStem(stem)
				? invertedIndex.get(stem).size()
				: 0;
	}

	/**
	 * Returns number of positions under respective file location and word stem
	 * keys in index.
	 *
	 * @param stem word stem key to be accessed
	 * @param location file location to be found under word stem key
	 * @return size of set assigned to location of word stem if exists; else 0
	 */
	public int positionCount(String stem, String location) {
		return hasLocation(stem, location)
				? invertedIndex.get(stem).get(location).size()
				: 0;
	}

	@Override
	public String toString() {
		return this.invertedIndex.toString();
	}

	/**
	 * Utilizes the JsonWriter class and writes out invertedIndex in JSON format out
	 * to output file.
	 *
	 * @param output path to the output file
	 * @throws IOException file is invalid or can not be found
	 */
	public void indexToJson(Path output) throws IOException {
			JsonWriter.asNestedObject(invertedIndex, output);
	}

	/**
	 * Utilizes the JsonWriter class and writes out wordCount in JSON format out
	 * to output file.
	 *
	 * @param output path to the output file
	 * @throws IOException file is invalid or can not be found
	 */
	public void countToJson(Path output) throws IOException {
			JsonWriter.asObject(wordCount, output);
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
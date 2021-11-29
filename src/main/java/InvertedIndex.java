import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * This WordCount map holds the word count of the files included in the
	 * invertedIndex map.
	 */
	public final Map<String, Integer> countMap;

	/**
	 * Initializes invertedIndex and wordCount to new empty TreeMap objects.
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<>();
		countMap = new TreeMap<>();
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
		invertedIndex.get(word).putIfAbsent(location, new TreeSet<Integer>());


		if(!countMap.containsKey(location) || countMap.getOrDefault(location, 0) < position) {
			countMap.put(location, position);
		}

		return invertedIndex.get(word).get(location).add(position);
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
		countMap.put(location, i);
	}

	/**
	 * Sets a word count value to a file location key.
	 *
	 * @param location name of file location to be assigned as key
	 * @param count word count of referenced file
	 * @return true if the map is updated with new (or changed) key value pair
	 */
	private boolean setWordCount(String location, Integer count) {
		if(countMap.containsKey(location) && countMap.get(location) == count) {
				return false;
		}
		countMap.put(location, count);
		return true;
	}

	/**
	 * Searches through the inverted index for stems that match (exact or partial)
	 * the stems in queries.
	 *
	 * @param queries the queries to use for search
	 * @param exact represents if exact search should be executed
	 * @return the list of query search results
	 */
	public List<QueryResult> search(Set<String> queries, boolean exact) {
		return exact
				? exactSearch(queries)
				: partialSearch(queries);
	}

	/**
	 * Searches through the invertedIndex to fond exact matches to the query stems.
	 *
	 * @param elem query line to be parsed and matched to inverted index entries
	 * @return the list of query search results
	 * 
	 * @see #updateResult(String, Map, List)
	 */
	public List<QueryResult> exactSearch(Set<String> elem) {
		List<QueryResult> results = new ArrayList<>();
		Map<String, QueryResult> lookup = new TreeMap<>();

		//For each stem in query set elem
		for(String stem: elem) {
			if(invertedIndex.containsKey(stem)) {
				//For every entry under that stem
				updateResult(stem, lookup, results);
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Searches through the invertedIndex to find matches that start with the query
	 * stems.
	 *
	 * @param elem query line to be parsed and matched to inverted index entries
	 * @return the list of query search results
	 * 
	 * @see #updateResult(String, Map, List)
	 */
	public List<QueryResult> partialSearch(Set<String> elem) {
		List<QueryResult> results = new ArrayList<>();
		Map<String, QueryResult> lookup = new TreeMap<>();

		//For every stem in query
		for(String stem: elem) {
			//For every entry under that stem
			for(String stemKey: invertedIndex.tailMap(stem).keySet()) {
				if(stemKey.startsWith(stem)) {
					updateResult(stemKey, lookup, results);
				}
				//No more stemKeys that start with stem, exit loop
				else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}
	
	/**
	 * Loops through the file locations where the stem key was found and either adds
	 * a new queryResult to result list passed in function or updates the result
	 * found in result list.
	 * 
	 * @param stem stem key to reference
	 * @param lookup lookup map for existing QueryResults in result list
	 * @param results list containing query results from query search
	 */
	private void updateResult(String stem, Map<String, QueryResult> lookup,
			List<QueryResult> results) {
		QueryResult queryResult = null;
		
		for(String fileLocation: invertedIndex.get(stem).keySet()) {
			queryResult = lookup.get(fileLocation);
			if (queryResult == null) {
				queryResult = new QueryResult(fileLocation);
				queryResult.updateMatchCount(stem);
				results.add(queryResult);
				lookup.put(fileLocation, queryResult);
			}
			else {
				queryResult.updateMatchCount(stem);
			}
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
			JsonWriter.asObject(countMap, output);
	}

	/**
	 * This class holds the results from the query search.
	 */
	public class QueryResult implements Comparable<QueryResult>{
		/**
		 * Represents the number of matches found in invertedIndex.
		 */
		private Integer matchCount;

		/**
		 * Represents the ratio of matches from a file location (matchCount / wordCount).
		 */
		private double score;
		
		/**
		 * Represents the file location that was searched.
		 */
		private final String location;

		/**
		 * Sets searched file and sets score and match count to 0.
		 *
		 * @param countMap total count of words from location
		 * @param matchCount amount of stem matches from query
		 * @param location file location searched
		 */
		public QueryResult(String location) {
			this.location = location;
			this.matchCount = 0;
			score = 0;
		}

		/**
		 * Sets matchCount to a new value and recalculates the score.
		 *
		 * @param stemMatch stem key to reference from invertedIndex
		 */
		private void updateMatchCount(String stemMatch) {
			this.matchCount += invertedIndex.get(stemMatch).get(location).size();
			this.score = (double) this.matchCount / countMap.get(location);
		}

		/**
		 * Retrieves amount of matches.
		 *
		 * @return matchCount as an Integer
		 */
		public Integer getMatchCount() {
			return matchCount;
		}

		/**
		 * Retrieves result score.
		 *
		 * @return score as a double
		 */
		public double getScore() {
			return score;
		}

		/**
		 * String formats score value to 8 decimal places.
		 *
		 * @return score String formatted to 8 decimal places
		 */
		public String getScoreString() {
			return String.format("%.8f", score);
		}

		/**
		 * Retrieves location.
		 *
		 * @return location as a String
		 */
		public String getLocation() {
			return location;
		}

		@Override
		public int compareTo(QueryResult o) {
			int output = Double.compare(o.score, score);
			return  output != 0
					? output
					: (output = Integer.compare(o.matchCount, matchCount)) != 0
						? output
						: location.compareToIgnoreCase(o.location.toString());
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("[");
			builder.append("count: ");
			builder.append(matchCount);
			builder.append(", score: ");
			builder.append(score);
			builder.append(", where: ");
			builder.append(location);
			builder.append("]");
			return builder.toString();
		}
	}
}
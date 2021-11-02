import java.io.IOException;
import java.nio.file.Path;
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
	private final Map<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;
	
	/**
	 * This WordCount map holds the word count of the files included in the
	 * invertedIndex map.
	 */
	private final Map<String, Integer> wordCount;
	
	private final QueryResult queryResult;
	
	/**
	 * Initializes invertedIndex and wordCount to new empty TreeMap objects.
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<>();
		wordCount = new TreeMap<>();
		queryResult = new QueryResult();
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
		addWordCount(location, i - 1);
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
	public Set<Integer> getPositions(String stem, String location) {
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
//		TODO: output results to JSON
//			JsonWriter.asObject(wordCount, output);
	}
}
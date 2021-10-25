import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * TODO Refactor this one to InvertedIndex
 */

/**
 * This class represents the data structure of the inverted index.
 */
public class InvertedIndexBuilder {
	// TODO Init invertedIndex in the constructor
	/**
	 * This map hold the data of the inverted index. The over-arching map holds
	 * word stem keys which are paired to respective maps that has file location
	 * keys which are then paired to an array of positions in the file the word
	 * stem appeared
	 */
	private final Map<String, TreeMap<String, TreeSet<Integer>>> invertedIndex =
			new TreeMap<>();

	/**
	 * Adds  word keys, file location keys, and positions in the index
	 * (if they does not already exist in the index)
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

	/*
	 * TODO
	 *
	 * public boolean add or addAll(List<String> words, String location) <--- call add in a loop
	 */

	// TODO Still breaking encapsulation
	/**
	 * Returns an unmodifiable invertedIndex for easy printing.
	 *
	 * @return an unmodifiable index map
	 */
	public Map<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		// TODO getWords() --> returns an unmodifiable set of the invertedIndex.keySet()
		return Collections.unmodifiableMap(invertedIndex);
	}

	/**
	 * Returns an unmodifiable map of file locations of a word stem
	 *
	 * @param stem word stem that needs to be accessed
	 * @return an unmodifiable Set of positions
	 */
	public Map<String, TreeSet<Integer>> getLocations(String stem) {
		// TODO return an unmodifiable view of invertedIndex.get(stem).keySet()
		return Collections.unmodifiableMap(invertedIndex.getOrDefault(stem, new TreeMap<>()));
	}

	/**
	 * Returns an unmodifiable Set of word stem positions at file location
	 *
	 * @param stem word stem that needs to be accessed
	 * @param location file location that needs to be accessed
	 * @return an unmodifiable Set of positions
	 */
	public Set<Integer> getPositions(String stem, String location) {
		return hasLocation(stem, location)
				? Collections.unmodifiableSet(invertedIndex.get(stem).get(location))
						// TODO Collections.emptySet();
				: Collections.unmodifiableSet(new TreeSet<>());
	}

	/**
	 * Checks if word stem exists as a key in index
	 *
	 * @param stem word stem to be found in index
	 * @return true if word stem exists as key in map
	 */
	public boolean hasStem(String stem) {
		return invertedIndex.containsKey(stem);
	}

	/**
	 * Checks if the word stems exists and then if location can be found
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
	 * can be found
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
	 * Returns number of word stem keys in index
	 *
	 * @return size of invertedIndex map
	 */
	public int stemCount() {
		return invertedIndex.size();
	}

	/**
	 * Returns number of file locations under word stem key in index
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
	 * keys in index
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

	/* TODO
	public void toJson(Path output) throws IOException {
		JsonWriter.asNestedObject(invertedIndex, output);
	}
	*/
}
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class represents a map which holds the total word count of each file
 * location entered into this
 */
public class WordCount {
	/**
	 * This map holds file locations as keys with the total word count of those
	 * files as values  
	 */
	private Map<String, Integer> wordCount;
	
	public WordCount() {
		wordCount = new TreeMap<>();
	}
	
	/**
	 * Sets a word count value to a file location key
	 * 
	 * @param location name of file location to be assigned as key
	 * @param count word count of referenced file
	 * @return true if the map is updated with new (or changed) key value pair
	 */
	public boolean set(String location, Integer count) {
		if(wordCount.containsKey(location) && wordCount.get(location) == count) {
				return false;
		}
		wordCount.put(location, count);
		return true;
	}
	
	/**
	 * Retrieves an unmodifiable map of wordCount
	 * 
	 * @return unmodifiable wordCount map
	 */
	public Map<String, Integer> getMap() {
		return Collections.unmodifiableMap(wordCount);
	}
}
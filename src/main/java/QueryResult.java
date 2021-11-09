
/*
 * TODO Don't need QuickSort unfortunately...
 *
 * List<QueryResult> results...
 * Collections.sort(results);
 */

/**
 * This class holds the results from the query search.
 */
public class QueryResult implements Comparable<QueryResult>{
	/*
	 * TODO When all of the data members are dependent on an instance of the same
	 * class, like a specific instance of an inverted index, that is a great use
	 * case for a public non-static inner class! Move this into your inverted index
	 * class. Once you do, we will be able to more efficiently update the member
	 * values and better encapsulate them too.
	 */

	/**
	 * Represents the number of matches found in invertedIndex.
	 */
	private Integer matchCount;
	/**
	 * Represents the number of words found from file location in invertedIndex.
	 */
	private Integer wordCount;
	/**
	 * Represents the ratio of matches from a file location (matchCount / wordCount).
	 */
	private double score;
	/**
	 * Represents the file location that was searched.
	 */
	private String location;

	/**
	 * Initializes instance data and calculates score.
	 *
	 * @param wordCount total count of words from location
	 * @param matchCount amount of stem matches from query
	 * @param location file location searched
	 */
	public QueryResult(int wordCount, int matchCount, String location) {
		this.matchCount = matchCount;
		this.wordCount = wordCount;
		score = ((double) matchCount) / wordCount;
		this.location = location;
	}

	/**
	 * Adds the matchCount from another QueryResult and recalculates the score.
	 *
	 * @param result QueryResult to be absorbed
	 */
	public void combine(QueryResult result) {
		setMatchCount(matchCount + (Integer) result.getMatchCount());
	}

	/**
	 * Sets matchCount to a new value and recalculates the score.
	 *
	 * @param matchCount new value matchCount should be set to
	 */
	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
		setScore((double) this.matchCount / wordCount);
	}

	/**
	 * Sets the score to a new value.
	 *
	 * @param score new value score should be set to
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * Sets the location to a new value.
	 *
	 * @param location new String location should be set to
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Retrieves matchCount as an Object.
	 *
	 * @return matchCount as an Object
	 */
	public Object getMatchCount() { // TODO return correct type
		return matchCount;
	}

	/**
	 * Retrieves score as an Object.
	 *
	 * @return score as an Object
	 */
	public Object getScore() {
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
	 * Retrieves location as an Object.
	 *
	 * @return location as an Object
	 */
	public Object getLocation() {
		return location;
	}

	@Override
	public int compareTo(QueryResult o) {
		// TODO int output = Double.compare(o.score, score);
		int output = Double.compare((double) o.getScore(), score);
		return  output != 0
				? output
				: (output = Integer.compare((Integer) o.getMatchCount(), matchCount)) != 0
					? output
					: location.compareToIgnoreCase(o.getLocation().toString());
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class holds the results from the query search. 
 */
public class QueryResult implements Comparable<QueryResult>{ 
	private Integer matchCount;
	private Integer wordCount;
	private double score;
	private String location;
	
	public QueryResult(int wordCount, int matchCount, String location) {
		this.matchCount = matchCount;
		this.wordCount = wordCount;
		score = ((double) matchCount) / wordCount;
		this.location = location;
	}
	
	public void combine(QueryResult result) {
		matchCount += (Integer) result.getMatchCount(); 
		score = (double) matchCount / wordCount;
	}
	
	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
		setScore(this.matchCount / wordCount);
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Object getMatchCount() {
		return matchCount;
	}
	
	public Object getScore() {
		return score;
	}
	
	public String getScoreString() {
		return String.format("%.8f", score);
	}
	
	public Object getLocation() {
		return location;
	}

	@Override
	public int compareTo(QueryResult o) {
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
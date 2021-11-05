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
	private double score;
	private String location;
	
	public QueryResult(int wordCount, int matchCount, String location) {
		this.matchCount = matchCount;
		score = matchCount / wordCount;
		this.location = location;
	}
	
	public Object getMatchCount() {
		return matchCount;
	}
	
	public Object getScore() {
		return score;
	}
	
	public Object getLocation() {
		return location;
	}

	@Override
	public int compareTo(QueryResult o) {
		// TODO Auto-generated method stub
		return 0;
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
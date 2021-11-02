import java.util.Set;
import java.util.TreeSet;

/**
 * This class holds the results from the query search. 
 */
public class QueryResult implements Comparable<QueryResult>{
	private final Set<String> results;
	
	public QueryResult() {
		results = new TreeSet<>();
	}

	@Override
	public int compareTo(QueryResult o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}
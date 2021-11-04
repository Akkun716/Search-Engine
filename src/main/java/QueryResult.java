import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class holds the results from the query search. 
 */
public class QueryResult implements Comparable<QueryResult>{
	private final Set<String> results;
	private Integer wordCount;
	private Integer matchCount;
	private Path location;
	
	public QueryResult(Path location) {
		results = new TreeSet<>();
		this.location = location;
		wordCount = 0;
		matchCount = 0;
	}
	
	public void query(String line) {
		
	}

	@Override
	public int compareTo(QueryResult o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}
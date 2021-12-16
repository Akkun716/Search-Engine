import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class takes in an inverted index and url, populating the index with
 * the html content extracted from the page.
 *
 * @author Adon Anglon
 */
public class WebCrawlerBuilder extends ThreadSafeIndexBuilder {
	/**
	 * This is the map used to hold http request headers and their relavent info.
	 */
	Map<String, List<String>> httpMap;

	public WebCrawlerBuilder(InvertedIndex index, WorkQueue queue) {
		super(index, queue);
	}
	
	public void readURL(String url) {
		
	}
	
	
	
}
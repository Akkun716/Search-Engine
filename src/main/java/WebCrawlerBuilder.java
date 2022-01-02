import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class takes in an inverted index and url, populating the index with
 * the html content extracted from the page.
 *
 * @author Adon Anglon
 */
public class WebCrawlerBuilder {
	/** The log4j2 logger. */
	static final Logger log = LogManager.getLogger();
	
	/**
	 * This InvertedIndex will be a reference for the index passed into the function.
	 */
	private final ThreadSafeInvertedIndex invertedIndex;
	
	/**
	 * This QueryResult map holds lists of queryResults for each query line key.
	 */
	private final WorkQueue queue;
	
	private Integer maxCrawl;
	
	private Object lock;

	/**
	 * Passes an invertedIndex into the class to be altered.
	 *
	 * @param invertedIndex invertedIndex to be entered
	 */
	public WebCrawlerBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		this.invertedIndex = index;
		this.queue = queue;
	}
	
	public void build(String url, int maxCrawl) throws IOException {
		System.out.println("Beginning build...");
		this.maxCrawl = maxCrawl;
		crawl(url);
		queue.finish();
	}
	
	public void crawl(String url) throws IOException {
		System.out.println("Retrieving link...");
		String html = HtmlFetcher.fetch(url, 3);
		System.out.println("Retrieved!");
		html = HtmlCleaner.stripBlockElements(html);
		System.out.println("Stripped block elements!");
		List<URL> validLinks = LinkParser.getValidLinks(new URL(url), html);
		System.out.println("Found Valid Links!");
		System.out.println("Executing validLink loop...");
		synchronized(maxCrawl) {
			while(validLinks.size() > 0 && maxCrawl > 0) {
				maxCrawl--;
				System.out.println("NEW LINK FOUND! Executing new crawl...");
				crawlURL(validLinks.remove(0));
			}
		}
		System.out.println("Finished loop!");
		System.out.println("Stripping rest of HTML...");
		html = HtmlCleaner.stripHtml(html);
		System.out.println("Stripped all HTML!");
		System.out.println("Beginning page parsing...");
		readPage(html, url);
		System.out.println("Done parsing...");
	}
	
	public void readPage(String html, String url) {
		queue.execute(new ParseTask(html, url));
	}
	
	public void crawlURL(URL url) {
		queue.execute(new WebTask(url));
	}

	/**
	 * This inner class represents a Runnable task that can be added to a work
	 * queue for multithreading.
	 */
	private class WebTask implements Runnable {
		URL url;

		public WebTask(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			try{
				crawl(url.toString());
			}
			catch(IOException e) {
					System.out.println("An IO error was thrown and needs to be handled.");
			}
		}
	}
	
	/**
	 * This inner class represents a Runnable task that can be added to a work
	 * queue for multithreading.
	 */
	private class ParseTask implements Runnable {
		String html;
		String url;

		public ParseTask(String html, String url) {
			this.html = html;
			this.url = url;
		}

		@Override
		public void run() {
			InvertedIndex tempIndex = new InvertedIndex();
			tempIndex.addAll(TextFileStemmer.listStems(html), url);
			invertedIndex.addAll(tempIndex);
			System.out.println("Finished copying index from " + url);
			
		}
	}
}
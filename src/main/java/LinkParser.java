import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses URL links from the anchor tags within HTML text.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2021
 */
public class LinkParser {
	/**
	 * Returns a list of all the valid HTTP(S) links found in the href attribute of
	 * the anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and normalized (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * Any links that are unable to be properly parsed (throwing an
	 * {@link MalformedURLException}) or that do not have the HTTP/S protocol will
	 * not be included.
	 *
	 * @param base the base url used to convert relative links to absolute3
	 * @param html the raw html associated with the base url
	 * @return list of all valid http(s) links in the order they were found
	 *
	 * @see Pattern#compile(String)
	 * @see Matcher#find()
	 * @see Matcher#group(int)
	 * @see #normalize(URL)
	 * @see #isHttp(URL)
	 */
	public static ArrayList<URL> getValidLinks(URL base, String html) {
		ArrayList<URL> links = new ArrayList<URL>();
		String regex = "<[a|A]([^>]|\\s)*?[h|H][r|R][e|E][f|F]\\s*?=\\s*?\".*?\"(.|\\s)*?>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		URL matched;
		String extract;

		while (matcher.find()) {
			//Finds href tag index and sets start and end after that index 
			int start = matcher.group().toLowerCase().indexOf("href=");
			start = matcher.group().indexOf("\"", start) + 1;
			int end = matcher.group().indexOf("\"", start + 1);
			extract = matcher.group().substring(start, end);
			
			try {
				if(extract.toLowerCase().startsWith("http")) {
					matched = normalize(new URL(extract));
				}
				else if(!extract.toLowerCase().startsWith("javascript") &&
						!isEmail(extract.toLowerCase())) {
					matched = normalize(new URL(base, extract));
				}
				else {
					matched = new URL("");
				}
				
				if(isHttp(matched)){
					links.add(matched);
				}
			}catch(Exception e) {
				System.out.println("Something went wrong");
				e.printStackTrace();
			}
		}

		return links;
	}

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url the url to normalize
	 * @return normalized url
	 * @throws URISyntaxException if unable to craft new URI
	 * @throws MalformedURLException if unable to craft new URL
	 */
	public static URL normalize(URL url) throws MalformedURLException, URISyntaxException {
		return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
				url.getPort(), url.getPath(), url.getQuery(), null).toURL();
	}

	/**
	 * Determines whether the URL provided uses the HTTP or HTTPS protocol.
	 *
	 * @param url the url to check
	 * @return true if the URL uses the HTTP or HTTPS protocol
	 */
	public static boolean isHttp(URL url) {
		return url.getProtocol().matches("(?i)https?");
	}
	
	/**
	 * Determines if the entered String is an email
	 * 
	 * @param link string representation of link being checked
	 * @return true if link contains the @ symbol and contains .edu or .org or .com
	 * 	extension
	 */
	public static boolean isEmail(String link) {
		return link.contains("@") &&
				(link.contains(".edu")||link.contains(".org")||link.contains(".com"));
	}

	/**
	 * Demonstrates this class.
	 *
	 * @param args unused
	 * @throws Exception if any issues occur
	 */
	public static void main(String[] args) throws Exception {
//		URL base = new URL("http://www.example.com");
//		String html = """
//				<A HREF="HTTP://WWW.USFCA.EDU">
//				""";
//		System.out.println(getValidLinks(base, html));
//		
//		String regex = "[.|\n]"
//		
//		// this demonstrates cleaning
//		URL valid = new URL("https://docs.python.org/3/library/functions.html?highlight=string#format");
//		System.out.println(" Link: " + valid);
//		System.out.println("Clean: " + normalize(valid));
//		System.out.println();
//
//		// this demonstrates encoding
//		URL space = new URL("https://www.google.com/search?q=hello world");
//		System.out.println(" Link: " + space);
//		System.out.println("Clean: " + normalize(space));
//		System.out.println();
//
//		// this throws an exception
//		URL invalid = new URL("javascript:alert('Hello!');");
//		System.out.println(invalid);
	}
}

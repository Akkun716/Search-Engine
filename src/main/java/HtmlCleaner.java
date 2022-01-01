import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cleans simple, validating HTML 4/5 into plain text.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2021
 */
public class HtmlCleaner {
	/** The log4j2 logger. */
	static final Logger log = LogManager.getLogger();
	
	/**
	 * Replaces all HTML 4 entities with their Unicode character equivalent or, if
	 * unrecognized, replaces the entity code with an empty string. For example:,
	 * {@code 2010&ndash;2012} will become {@code 2010–2012} and
	 * {@code &gt;&dash;x} will become {@code >x} with the unrecognized
	 * {@code &dash;} entity getting removed. (The {@code &dash;} entity is valid
	 * HTML 5, but not HTML 4 which this code uses.)
	 *
	 * <p>
	 * <em>(View this comment as HTML in the "Javadoc" view in Eclipse.)</em>
	 *
	 * @see StringEscapeUtils#unescapeHtml4(String)
	 * @see String#replaceAll(String, String)
	 *
	 * @param html text including HTML entities to remove
	 * @return text with all HTML entities converted or removed
	 */
	public static String stripEntities(String html) {
		html = StringEscapeUtils.unescapeHtml4(html);
		return html.replaceAll("&\\w+?;", "");
	}

	/**
	 * Replaces all HTML tags with an empty string. For example, the html
	 * {@code A<b>B</b>C} will become {@code ABC}.
	 *
	 * <p>
	 * <em>(View this comment as HTML in the "Javadoc" view in Eclipse.)</em>
	 *
	 * @param html text including HTML tags to remove
	 * @return text without any HTML tags
	 *
	 * @see String#replaceAll(String, String)
	 */
	public static String stripTags(String html) {
		return html.replaceAll("<[\\w\\s=\"\\.$&+,:;=?@#|'<>^*()%!-_]*?>", "");
	}

	/**
	 * Replaces all HTML comments with an empty string. For example:
	 *
	 * <pre>
	 * A&lt;!-- B --&gt;C
	 * </pre>
	 *
	 * ...and this HTML:
	 *
	 * <pre>
	 * A&lt;!--
	 * B --&gt;C
	 * </pre>
	 *
	 * ...will both become "AC" after stripping comments.
	 *
	 * <p>
	 * <em>(View this comment as HTML in the "Javadoc" view in Eclipse.)</em>
	 *
	 * @param html text including HTML comments to remove
	 * @return text without any HTML comments
	 *
	 * @see String#replaceAll(String, String)
	 */
	public static String stripComments(String html) {
		return html.replaceAll("<!(--)(.|\\s)*?(--)>", "");
	}

	/**
	 * Replaces everything between the element tags and the element tags
	 * themselves with an empty string. For example, consider the html code:
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed, and
	 * replaced with an empty string.
	 *
	 * <p>
	 * <em>(View this comment as HTML in the "Javadoc" view in Eclipse.)</em>
	 *
	 * @param html text including HTML elements to remove
	 * @param name name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 *
	 * @see String#replaceAll(String, String)
	 */
	public static String stripElement(String html, String name) {
		StringBuilder builder = new StringBuilder("<");
		name = regexInsensitive(name);
		builder.append(name);
		builder.append("(>|\\s)(.|\\s|>)*?/");
		builder.append(name);
		builder.append("(\\s)*?>");
		return html.replaceAll(builder.toString(), "");
	}
	
	/**
	 * Creates a regex pattern that matches case insensitively
	 * 
	 * @param name the string to be converted to case insensitive regex
	 * @return the case insensitive regex pattern
	 */
	private static String regexInsensitive(String name) {
		log.debug("\tStarting building regex...");
		StringBuilder builder = new StringBuilder();
		String nameUpper = name.toUpperCase();
		String nameLower = name.toLowerCase();
		for(int i = 0; i < name.length(); i++) {
			builder.append("[");
			builder.append(nameLower.charAt(i));
			builder.append("|"); 
			builder.append(nameUpper.charAt(i));
			builder.append("]");
		}
		log.debug("\tDone constructing insensitive regex!");
		return builder.toString();
	}

	/**
	 * Removes comments and certain block elements from the provided html. The block
	 * elements removed include: head, style, script, noscript, iframe, and svg.
	 *
	 * @param html the HTML to strip comments and block elements from
	 * @return text clean of any comments and certain HTML block elements
	 */
	public static String stripBlockElements(String html) {
		html = stripComments(html);
		log.debug("\tDone striping comments!");
		html = stripElement(html, "head");
		log.debug("\tDone striping head!");
		html = stripElement(html, "style");
		log.debug("\tDone striping style!");
		html = stripElement(html, "script");
		log.debug("\tDone striping script!");
		html = stripElement(html, "noscript");
		log.debug("\tDone striping noscript!");
		html = stripElement(html, "iframe");
		log.debug("\tDone striping iframe!");
		html = stripElement(html, "svg");
		log.debug("\tDone striping svg!");
		return html;
	}

	/**
	 * Removes all HTML tags and certain block elements from the provided text.
	 *
	 * @see #stripBlockElements(String)
	 * @see #stripTags(String)
	 *
	 * @param html the HTML to strip tags and elements from
	 * @return text clean of any HTML tags and certain block elements
	 */
	public static String stripHtml(String html) {
		log.debug("\nSTART CLEAN...");
		
		log.debug("Starting striping blocks...");
		html = stripBlockElements(html);
		log.debug("Finished striping blocks!");
		
		log.debug("Starting striping tags...");
		html = stripTags(html);
		log.debug("Finished striping tags!");
		
		log.debug("Starting striping entities...");
		html = stripEntities(html);
		log.debug("Finished striping entities!");
		
		log.debug("DONE!");
		return html;
	}
}

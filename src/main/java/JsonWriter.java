import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

// TODO See Piazza on Json writing

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2021
 */
public class JsonWriter {
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		boolean first = true;
		level++;
		edgeFormat(writer, 0, "[]", false);
		for(Integer elem: elements) {
			//Handles comma after each element
			if(!first) {
				writer.write(",");
			}
			else {
				first = false;
			}
			writer.write('\n');
			levelAdjust(writer, level, false);
			writer.write(elem.toString());
		}
		edgeFormat(writer, level, "[]", true);
	}

	/**
	 * Writes the starting or ending specified brackets
	 *
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @param edgeChars the brackets needed for formatting
	 * @param end states if the edge is at the end of Collection or not
	 * @throws IOException if an IO error occurs
	 */
	private static void edgeFormat(Writer writer, int level, String edgeChars, boolean end) throws IOException{
		if(end) {
			writer.write("\n");
		}

		levelAdjust(writer, level, true);
		if(!end) {
			writer.write(edgeChars.charAt(0));
		}
		else {
			writer.write(edgeChars.charAt(1));
		}

	}



	/**
	 * Inserts necessary amount of indents as indicated by level
	 *
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @param offset determines if line needs to be one indent level less than indicated
	 * @throws IOException if an IO error occurs
	 */
	private static void levelAdjust(Writer writer, int level, boolean offset) throws IOException {
		if(offset) {
			level -= 1;
		}
		for(int i = 0; i < level; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		boolean first = true;
		edgeFormat(writer, level++, "{}", false);
		for(var elem: elements.entrySet()) {
			//Handles comma after each element
			if(!first) {
				writer.write(",");
			}
			else {
				first = false;
			}
			writer.write('\n');
			levelAdjust(writer, level, false);
			keyFormat(writer, elem.getKey().toString());
			writer.write(elem.getValue().toString());
		}
		edgeFormat(writer, level, "{}", true);
	}

	/**
	 * Constructs JSON object key formatting by placing quotes ("") around key and colon (:) after
	 *
	 * @param writer the writer to use
	 * @param key the Map key for JSON key formatting
	 * @throws IOException if an IO error occurs
	 */
	private static void keyFormat(Writer writer, String key) throws IOException{
		writer.write("\"");
		writer.write(key);
		writer.write("\": ");
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {
		boolean first = true;
		edgeFormat(writer, level++, "{}", false);
		for(var elem: elements.entrySet()) {
			//Handles comma after each element
			if(!first) {
				writer.write(",");
			}
			else {
				first = false;
			}
			writer.write('\n');
			levelAdjust(writer, level, false);
			keyFormat(writer, elem.getKey().toString());
			asArray(elem.getValue(), writer, level);
		}
		edgeFormat(writer, level, "{}", true);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested objects with nested arrays.
	 * The generic notation used allows this method to be used for any type of map with any
	 * nested map with any collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedObject(Map<String, TreeMap<String, ArrayList<Integer>>> elements,
			Writer writer, int level) throws IOException {
		boolean first = true;
		edgeFormat(writer, level++, "{}", false);
		for(var elem: elements.entrySet()) {
			//Handles comma after each element
			if(!first) {
				writer.write(",");
			}
			else {
				first = false;
			}
			writer.write('\n');
			levelAdjust(writer, level, false);
			keyFormat(writer, elem.getKey().toString());
			asNestedArray(elem.getValue(), writer, level);
		}
		edgeFormat(writer, level, "{}", true);
	}


	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedObject(Map<String, TreeMap<String, ArrayList<Integer>>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedObect(Map<String, TreeMap<String, ArrayList<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
}
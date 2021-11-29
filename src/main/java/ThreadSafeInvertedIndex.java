import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * This is a thread safe version of the inverted index, providing multithreading
 * functionality to the inverted index data structure.
 */
public class ThreadSafeInvertedIndex extends InvertedIndex{
	/** This will hold all of the tasks needed to be executed. */
	WorkQueue queue;
	
	/** This will be the read/wrie lock needed for multithreading. */
	IndexReadWriteLock lock;
	
	/**
	 * Default initialization of index, creates a new work queue.
	 */
	public ThreadSafeInvertedIndex() {
		this(new WorkQueue());
	}
	
	/**
	 * Passes a work queue object to be used and initializes lock object.
	 */
	public ThreadSafeInvertedIndex(WorkQueue queue) {
		this.queue = queue;
		lock = new IndexReadWriteLock();
	}
	
	@Override
	public boolean add(String word, String location, Integer position) {
		synchronized(lock.writeLock()) {
			return super.add(word, location, position);
		}
	}

	@Override
	public List<QueryResult> exactSearch(Set<String> elem) {
		synchronized(lock.readLock()) {
			return super.exactSearch(elem);
		}
	}

	@Override
	public List<QueryResult> partialSearch(Set<String> elem) {
		synchronized(lock.readLock()) {
			return super.partialSearch(elem);
		}
	}

	@Override
	public Set<String> getWords() {
		synchronized(lock.readLock()) {
			return super.getWords();
		}
	}

	@Override
	public Set<String> getLocations(String stem) {
		synchronized(lock.readLock()) {
			return super.getLocations(stem);
		}
	}

	@Override
	public Set<Object> getPositions(String stem, String location) {
		synchronized(lock.readLock()) {
			return super.getPositions(stem, location);
		}
	}

	@Override
	public boolean hasStem(String stem) {
		synchronized(lock.readLock()) {
			return super.hasStem(stem);
		}
	}

	@Override
	public boolean hasLocation(String stem, String location) {
		synchronized(lock.readLock()) {
			return super.hasLocation(stem, location);
		}
	}

	@Override
	public boolean hasPosition(String stem, String location, Integer position) {
		synchronized(lock.readLock()) {
			return super.hasPosition(stem, location, position);
		}
	}

	@Override
	public int stemCount() {
		synchronized(lock.readLock()) {
			return super.stemCount();
		}
	}

	@Override
	public int locationCount(String stem) {
		synchronized(lock.readLock()) {
			return super.locationCount(stem);
		}
	}

	@Override
	public int positionCount(String stem, String location) {
		synchronized(lock.readLock()) {
			return super.positionCount(stem, location);
		}
	}

	@Override
	public String toString() {
		synchronized(lock.readLock()) {
			return super.toString();
		}
	}

	@Override
	public void indexToJson(Path output) throws IOException {
		super.indexToJson(output);
	}

	@Override
	public void countToJson(Path output) throws IOException {
		super.countToJson(output);
	}
}
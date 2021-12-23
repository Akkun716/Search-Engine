import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a thread safe version of the inverted index, providing multithreading
 * functionality to the inverted index data structure.
 */
public class ThreadSafeInvertedIndex extends InvertedIndex{

	/** This will be the read/write lock needed for multithreading. */
	private final IndexReadWriteLock lock;

	/**
	 * Initialization of index and initializes lock object.
	 */
	public ThreadSafeInvertedIndex() {
		lock = new IndexReadWriteLock();
	}

	@Override
	public boolean add(String word, String location, Integer position) {
		lock.writeLock().lock();

		try {
			return super.add(word, location, position);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void addAll(List<String> words, String location) {
		lock.writeLock().lock();
		
		try {
			super.addAll(words, location);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void addAll(InvertedIndex index) {
		lock.writeLock().lock();
		
		try {
			super.addAll(index);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public List<QueryResult> exactSearch(Set<String> elem) {
		lock.readLock().lock();

		try {
			return super.exactSearch(elem);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<QueryResult> partialSearch(Set<String> elem) {
		lock.readLock().lock();

		try {
			return super.partialSearch(elem);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getWords() {
		lock.readLock().lock();

		try {
			return super.getWords();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getLocations(String stem) {
		lock.readLock().lock();

		try {
			return super.getLocations(stem);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> getPositions(String stem, String location) {
		lock.readLock().lock();

		try {
			return super.getPositions(stem, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasStem(String stem) {
		lock.readLock().lock();

		try {
			return super.hasStem(stem);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasLocation(String stem, String location) {
		lock.readLock().lock();

		try {
			return super.hasLocation(stem, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPosition(String stem, String location, Integer position) {
		lock.readLock().lock();

		try {
			return super.hasPosition(stem, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int stemCount() {
		lock.readLock().lock();

		try {
			return super.stemCount();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int locationCount(String stem) {
		lock.readLock().lock();

		try {
			return super.locationCount(stem);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int positionCount(String stem, String location) {
		lock.readLock().lock();

		try {
			return super.positionCount(stem, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();

		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void indexToJson(Path output) throws IOException {
		lock.readLock().lock();

		try {
			super.indexToJson(output);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void countToJson(Path output) throws IOException {
		lock.readLock().lock();

		try {
			super.countToJson(output);
		}
		finally {
			lock.readLock().unlock();
		}
	}
}
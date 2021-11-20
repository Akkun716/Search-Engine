import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class ThreadSafeInvertedIndex extends InvertedIndex{
	WorkQueue queue;
	IndexReadWriteLock lock;
	
	public ThreadSafeInvertedIndex() {
		this(new WorkQueue());
	}
	
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
	public boolean addWordCount(String location, Integer count) {
		synchronized(lock.writeLock()) {
			return super.addWordCount(location, count);
		}
	}

	@Override
	public boolean setWordCount(String location, Integer count) {
		synchronized(lock.writeLock()) {
			return super.setWordCount(location, count);
		}
	}

	@Override
	public void exactSearch(Set<String> elem, QueryResultBuilder queryBuilder) {
		synchronized(lock.readLock()) {
			super.exactSearch(elem, queryBuilder);
		}
	}

	@Override
	public void partialSearch(Set<String> elem, QueryResultBuilder queryBuilder) {
		synchronized(lock.readLock()) {
			super.partialSearch(elem, queryBuilder);
		}
	}

	@Override
	public void cleanSortResults(List<InvertedIndex.QueryResult> results) {
		synchronized(lock.writeLock()) {
			super.cleanSortResults(results);
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
		// TODO Auto-generated method stub
		super.indexToJson(output);
	}

	@Override
	public void countToJson(Path output) throws IOException {
		// TODO Auto-generated method stub
		super.countToJson(output);
	}
}
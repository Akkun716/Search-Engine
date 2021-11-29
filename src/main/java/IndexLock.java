import java.util.concurrent.locks.Lock;

/**
 * A simple lock used for conditional synchronization as an alternative to using
 * a {@code synchronized} block.
 *
 * Similar but simpler than {@link Lock}.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2021
 */
public interface IndexLock {
	/**
	 * Acquires the lock. If the lock is not available then the current thread
	 * becomes disabled for thread scheduling purposes and lies dormant until the
	 * lock has been acquired.
	 */
	public void lock();

	/**
	 * Releases the lock.
	 */
	public void unlock();
}

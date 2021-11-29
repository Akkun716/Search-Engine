import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple work queue implementation based on the IBM developerWorks article by
 * Brian Goetz. Modified to keep track of the amount of pending or unfinished
 * work (or tasks) must still be completed.
 *
 * @see <a href=
 *   "https://web.archive.org/web/20210126172022/https://www.ibm.com/developerworks/library/j-jtp0730/index.html">
 *   Java Theory and Practice: Thread Pools and Work Queues</a>
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2021
 */
public class WorkQueue {
	/** Used to count how many tasks are remaining. */
	private int pending;

	/** Workers that wait until work (or tasks) are available. */
	private final Worker[] workers;

	/** Queue of pending work (or tasks). */
	private final LinkedList<Runnable> tasks;

	/** Used to signal the workers should terminate. */
	private volatile boolean shutdown;

	/** The default number of worker threads to use when not specified. */
	public static final int DEFAULT = 5;

	/** Logger used for this class. */
	private static final Logger log = LogManager.getLogger();

	/**
	 * Starts a work queue with the default number of threads.
	 *
	 * @see #WorkQueue(int)
	 */
	public WorkQueue() {
		this(DEFAULT);
	}

	/**
	 * Starts a work queue with the specified number of threads.
	 *
	 * @param threads number of worker threads; should be greater than 1
	 */
	public WorkQueue(int threads) {
		this.tasks = new LinkedList<Runnable>();
		pending = 0;
		this.workers = new Worker[threads];
		this.shutdown = false;

		// start the threads so they are waiting in the background
		for (int i = 0; i < threads; i++) {
			workers[i] = new Worker();
			workers[i].start();
		}

		log.debug("Work queue initialized with {} worker threads.", workers.length);
	}

	/**
	 * Adds a work (or task) request to the queue and increments the amount of
	 * pending tasks that must be completed. A worker thread will process this
	 * request when available.
	 *
	 * @param task work request (in the form of a {@link Runnable} object)
	 */
	public void execute(Runnable task) {
		synchronized (tasks) {
			tasks.addLast(task);
			pending++;
			tasks.notifyAll();
		}
	}

	/**
	 * Waits for all pending work (or tasks) to be finished. Does not terminate the
	 * worker threads so that the work queue can continue to be used.
	 */
	public synchronized void finish() {
		while(pending > 0) {
			synchronized(tasks) {
				tasks.notifyAll();
			}
			
		}
	}

	/**
	 * Asks the queue to shutdown. Any unprocessed work (or tasks) will not be
	 * finished, but threads in-progress will not be interrupted.
	 */
	public void shutdown() {
		// safe to do unsynchronized due to volatile keyword
		shutdown = true;

		log.debug("Work queue triggering shutdown...");
		synchronized (tasks) {
			tasks.notifyAll();
		}
	}

	/**
	 * Similar to {@link Thread#join()}, waits for all the work to be finished and
	 * the worker threads to terminate. The work queue cannot be reused after this
	 * call completes.
	 */
	public void join() {
		try {
			finish();
			shutdown();

			for (Worker worker : workers) {
				worker.join();
			}

			log.debug("All worker threads terminated.");
		}
		catch (InterruptedException e) {
			System.err.println("Warning: Work queue interrupted while joining.");
			log.catching(Level.DEBUG, e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Returns the number of worker threads being used by the work queue.
	 *
	 * @return number of worker threads
	 */
	public int size() {
		return workers.length;
	}
	
	/**
	 * Returns the amount of remaining tasks to be executed.
	 * 
	 * @return number of remaining tasks in work queue
	 */
	public int remaining() {
		return pending;
	}

	/**
	 * Waits until work (or a task) is available in the work queue. When work is
	 * found, will remove the work from the queue and run it. Decrements the amount
	 * of pending work when the work is completed.
	 *
	 * <p>
	 * If a shutdown is detected, will exit instead of grabbing new work from the
	 * queue. These threads will continue running in the background until a shutdown
	 * is requested.
	 */
	private class Worker extends Thread {
		/**
		 * Initializes a worker thread with a custom name.
		 */
		public Worker() {
			setName("Worker" + getName());
		}

		@Override
		public void run() {
			Runnable task = null;

			try {
				while (true) {
					synchronized (tasks) {
						while (tasks.isEmpty() && !shutdown) {
							log.debug("Work queue worker waiting...");
							tasks.wait();
						}

						// exit while for one of two reasons:
						// (a) queue has work, or (b) shutdown has been called

						if (shutdown) {
							log.debug("Worker detected shutdown...");
							break;
						}
						else {
							task = tasks.removeFirst();
							pending--;
						}
					}

					try {
						log.trace("Work queue worker found work.");
						task.run();
					}
					catch (RuntimeException e) {
						// catch runtime exceptions to avoid leaking threads
						System.err.printf("Warning: Worker thread %s encountered an exception while running.%n", this.getName());
						log.catching(Level.DEBUG, e);
					}
				}
			}
			catch (InterruptedException e) {
				System.err.printf("Warning: Worker thread %s interrupted while waiting.%n", this.getName());
				log.catching(Level.DEBUG, e);
				Thread.currentThread().interrupt();
			}

			log.debug("Worker thread terminating...");
		}
	}
}

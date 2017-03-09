package project.concurrent;
import java.util.HashMap;

/**
 * A read/write lock that allows multiple readers, disallows multiple writers, and allows a writer to 
 * acquire a read lock while holding the write lock. 
 * 
 * 
 * lock read
 * 1. what if there is a writer and it is not me (Thread.currentThread().getId())
 * 2. Grant lock if no writers or one and the writer is me
 * 
 */
public class ReentrantLock {
	private int writers;
	private HashMap<Long, Integer> threadMap;
	private long currentWriter;

	/**
	 * Construct a new ReentrantLock.
	 */
	public ReentrantLock() {
		this.writers = 0;
		this.currentWriter = 0;
		this.threadMap = new HashMap<Long, Integer>();
	}

	/**
	 * Returns true if the invoking thread holds a read lock.
	 * @return
	 */
	public synchronized boolean hasRead() {
		if(this.threadMap.containsKey(Thread.currentThread().getId()))
		{
			return true;
		}
		return false;
	}

	
	/**
	 * Returns true if the invoking thread holds a write lock.
	 * @return
	 */
	public synchronized boolean hasWrite() {
		if (Thread.currentThread().getId() == this.currentWriter)
		{
			return true;
		}
		return false;
	}

	
	/**
	 * Non-blocking method that attempts to acquire the read lock.
	 * Returns true if successful.
	 * @return
	 */
	public synchronized boolean tryLockRead() {
		if(this.writers == 0 ||  Thread.currentThread().getId() == this.currentWriter)
		{
			if(!threadMap.containsKey(Thread.currentThread().getId()))
			{
				threadMap.put(Thread.currentThread().getId(), 1);
			}
			else
			{
				threadMap.put(Thread.currentThread().getId(), threadMap.get(Thread.currentThread().getId()+1));
			}
			
			return true;
		}
		return false;
	}

	
	/**
	 * Non-blocking method that attempts to acquire the write lock.
	 * Returns true if successful.
	 * @return
	 */	
	public synchronized boolean tryLockWrite() {
		if((this.writers == 0 && threadMap.size() == 0) || Thread.currentThread().getId() == this.currentWriter)
		{
			this.currentWriter = Thread.currentThread().getId();
			this.writers++;
			return true;
		}
		return false;
	}

	
	/**
	 * Blocking method that will return only when the read lock has been 
	 * acquired.
	 */	 
	public synchronized void lockRead() {
		while(!tryLockRead())
		{
			try {
				this.wait();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}

	
	/**
	 * Releases the read lock held by the calling thread. Other threads may continue
	 * to hold a read lock.
	 */
	public synchronized void unlockRead() {
		if(threadMap.containsKey(Thread.currentThread().getId())) {
			if(threadMap.get(Thread.currentThread().getId()) > 1)
			{
				threadMap.put(Thread.currentThread().getId(), threadMap.get(Thread.currentThread().getId()-1));
			}
			else if(threadMap.get(Thread.currentThread().getId()) == 1)
			{
				threadMap.remove(Thread.currentThread().getId());
			}
		}
		if(threadMap.size() == 0)
		{
			this.notify();
		}
	}

	
	/**
	 * Blocking method that will return only when the write lock has been 
	 * acquired.
	 */
	public synchronized void lockWrite() {
		while(!tryLockWrite())
		{
			try {
				this.wait();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}

	
	/**
	 * Releases the write lock held by the calling thread. The calling thread may continue to hold
	 * a read lock.
	 */
	public synchronized void unlockWrite() {
		if (Thread.currentThread().getId() == this.currentWriter)
		{
			this.writers--;
			if(this.writers == 0)
			{
				this.currentWriter = 0;
				this.notifyAll();
			}
		}
	}
	
	
}

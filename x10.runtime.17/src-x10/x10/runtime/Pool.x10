/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.runtime.kernel.Thread;
import x10.util.Stack;

/**
 * Thread pool
 * @author tardieu
 */
class Pool {
	// TODO convert to value type
	
	/**
	 * Instance lock
	 */
	private val monitor = new Monitor();

	/**
	 * Jobs not yet assigned to a worker
	 */
	private val jobs = new Stack[Job]();
	
	/**
	 * Pool size
	 */
	private var count: int;
	
	/** 
	 * Number of blocked activities in the pool
	 */
	private var busy: int = 0;
	
	/** 
	 * Start count threads
	 */
	def this(count: nat) {
		this.count = count;
		for(var i: int = 0; i<count; i++) allocate(i);
	}
	
	/**
	 * Submit a new job to the pool
	 */
	def execute(job: Job): void {
		monitor.lock();
		jobs.push(job);
		
		// wake up available worker thread if any
		monitor.unpark();

		monitor.unlock();
	}
	
	/**
	 * Assign a job to calling worker
	 */
	def job(thread: Thread): Job {
		monitor.lock();

		// park worker thread
		while (jobs.isEmpty()) monitor.park();

		val job = jobs.pop();
		monitor.unlock();
		return job;
	}
	
	/**
	 * Start a new thread
	 */
	def allocate(id: int): void {
		new Thread(Place.FIRST_PLACE, new Worker(this), "thread-" + id.toString()).start();
	}

	/**
	 * Increment number of blocked activities
	 */
	def increase(): void {
		monitor.lock();
		if (++busy >= count) allocate(count++);
		monitor.unlock();
    }

	/**
	 * Decrement number of blocked activities
	 */
	def decrease(): void {
		monitor.lock();
		--busy;
		monitor.unlock();
    }
}

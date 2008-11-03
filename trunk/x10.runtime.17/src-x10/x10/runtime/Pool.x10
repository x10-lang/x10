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
	private var size: int;
	
	/** 
	 * Number of blocked activities in the pool
	 */
	private var busy: int = 0;
	
	/** 
	 * Start nb threads
	 */
	def this(nb: nat) {
		size = nb;
		for(var i: int = 0; i<nb; i++) allocate();
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
		while (jobs.empty()) monitor.park();

		val job = jobs.pop();
		monitor.unlock();
		return job;
	}
	
	/**
	 * Start a new thread
	 */
	def allocate(): void {
		val thread = new Thread(new Worker(this));
		
		// must attach the thread to a place here!
		thread.place(Place.FIRST_PLACE);
		
		thread.start();
	}

	/**
	 * Increment number of blocked activities
	 */
	def increase(): void {
		monitor.lock();
		if (++busy >= size) {
		
			// enlarge pool
			size++;
			allocate();
		}
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

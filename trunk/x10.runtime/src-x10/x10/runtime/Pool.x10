/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.Random;

/**
 * @author tardieu
 */
public class Pool implements ()=>Void {
	private val latch:Latch{self.at(this)};
	private var size:Int; // the number of workers in the pool

	private var spares:Int = 0; // the number of spare workers in the pool

	private val lock = new Lock();

	private val semaphore = new Semaphore(0);
	
	// an upper bound on the number of workers
	private const MAX = 1000; 

	// the workers in the pool
	private val workers:Rail[Worker{self.at(this)}]{self.at(this)};
	
	// the threads in the pool
	private val threads:Rail[Thread{self.at(this)}];

	def this(latch:Latch{self.at(here)}, size:Int) {
		this.latch = latch;
	    this.size = size;
	    val workers = Rail.makeVar[Worker{self.at(here)}](MAX);
	    val threads = Rail.makeVar[Thread{self.at(here)}](size);

	    // worker for the master thread
	    val master = new Worker(latch, 0);
	    workers(0) = master;
	    threads(0) = Thread.currentThread();
	    Thread.currentThread().worker(master);
	    
	    // other workers
	    for (var i:Int = 1; i<size; i++) {
			val worker = new Worker(latch, i);
			workers(i) = worker;
			threads(i) = new Thread(worker.apply.(), "thread-" + i);
			threads(i).worker(worker);
	    }
	    this.workers = workers;
	    this.threads = threads;
	}

	public def apply():Void {
		for (var i:Int = 1; i<size; i++) {
	    	threads(i).start();
	    }
	    workers(0)();
   		while (size > 0) Thread.park();
	}
	
	
	// all methods are local
	
	// notify the pool a worker is about to execute a blocking operation
	def increase():Void {
		lock.lock();
		if (spares > 0) {
			// if a spare is available increase parallelism
			spares--;
			lock.unlock();
			semaphore.release();
		} else {
			// allocate and start a new worker
			val i = size++;
			lock.unlock();
			assert (i < MAX);
			if (i >= MAX) {
				NativeRuntime.println("TOO MANY THREADS... ABORTING");
				System.exit(1);
			}
			// vj: This cast should not be needed.
			val worker = new Worker(latch as Latch{self.at(here)}, i);
			workers(i) = worker;
			val thread = new Thread(worker.apply.(), "thread-" + i);
			thread.worker(worker);
			thread.start();
		}
    }

	// notify the pool a worker resumed execution after a blocking operation
	def decrease(n:Int):Void {
		// increase number or spares
		lock.lock();
		spares += n;
		lock.unlock();
		// reduce parallelism
		semaphore.reduce(n);
    }

	// release permit (called by worker upon termination)
	def release() {
		semaphore.release();
		lock.lock();
		size--;
		if (size == 0) Thread.unpark(threads(0));
		lock.unlock();
	}

	// scan workers for activity to steal
	def scan(random:Random{self.at(here)}, latch:Latch, block:Boolean):Activity{self.at(here)} {
		var activity:Activity{self.at(here)}= null;
		var next:Int = random.nextInt(size);
		for (;;) {
			NativeRuntime.event_probe();
			if (null != workers(next)) { // avoid race with increase method
				activity = workers(next).steal();
			}
			if (null != activity || latch()) return activity;
			if (semaphore.available() < 0) {
				if (block) {
					semaphore.release();
					semaphore.acquire();
				} else {
					return activity;
				}
			}
			if (++next == size) next = 0;
		}
	}
}

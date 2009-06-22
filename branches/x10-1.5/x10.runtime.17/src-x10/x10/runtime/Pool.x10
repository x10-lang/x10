/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.Random;
import x10.compiler.Native;

/**
 * @author tardieu
 */
public value Pool {
	// the state of the pool
	static class That {
		var size:Int; // the number of workers in the pool
		var spares:Int = 0; // the number of spare workers in the pool
	}
	private val that = new That();

	private val lock = new Lock();

	private val semaphore = new Semaphore(0);

	// an upper bound on the number of workers
	private const MAX = 1000; 

	// the workers in the pool
	private val workers = Rail.makeVar[Worker](MAX);

	def this(size:Int) {
		that.size = size;
		
		// allocate and assign worker for the master thread
		workers(0) = new Worker(this, 0);
		Thread.currentThread().worker(workers(0));
		
		// allocate and start other workers
		for (var i:Int = 1; i<size; i++) {
			val worker = new Worker(this, i);
			workers(i) = worker;
			val thread = new Thread(()=>worker.apply(), "thread-" + i);
			thread.worker(worker);
			thread.start();
		}
	}

	public def worker():Worker = Thread.currentThread().worker();
	
	// submit an activity to the pool (global method)
	def execute(activity:Activity):Void {
		NativeRuntime.runAtLocal(that.location.id, ()=>{
			worker().push(activity);
		});
	}
	
	// notify the pool a worker is about to execute a blocking operation (global method)
	def increase():Void {
		NativeRuntime.runAtLocal(that.location.id, ()=>{
			lock.lock();
			if (that.spares > 0) {
				// if a spare is available increase parallelism
				that.spares--;
				lock.unlock();
				semaphore.release();
			} else {
				// allocate and start a new worker
				val i = that.size++;
				lock.unlock();
				assert (i < MAX);
				if (i >= MAX) {
					NativeRuntime.println("TOO MANY THREADS... ABORTING");
					System.exit(1);
				}
				val worker = new Worker(this, i);
				workers(i) = worker;
				val thread = new Thread(()=>worker.apply(), "thread-" + i);
				thread.worker(worker);
				thread.start();
			}
		});
    }

	// notify the pool a worker resumed execution after a blocking operation (global method)
	def decrease(n:Int):Void {
		NativeRuntime.runAtLocal(that.location.id, ()=>{
			// increase number or spares
			lock.lock();
			that.spares += n;
			lock.unlock();
			// reduce parallelism
			semaphore.reduce(n);
		});
    }

	// steal activity from given worker
	private def steal(var r:Int):Activity {
		if (null != workers(r)) { // avoid race with increase method
			return workers(r).steal();
		} else {
			return null;
		}
	}

	// run pending activities while waiting on conditioon (global method)
	def join(cond:FinishState) {
		NativeRuntime.runAtLocal(that.location.id, ()=>{
			worker().join(cond);
		});
	}
	
	// return the number of pending activities for the current worker
	def pendingActivityCount():Int = worker().size();

	// release permit (called by worker upon termination)
	def release() {
		semaphore.release();
	}

	// scan workers for activity to steal
	def scan(random:Random, cond:FinishState, block:Boolean):Activity {
		var activity:Activity;
		var next:Int = random.nextInt(that.size);
		for (;;) {
			activity = steal(next);
			if (null != activity || check(cond)) return activity;
			if (Thread.currentThread() == Runtime.listener) {
				NativeRuntime.event_probe();
				activity = worker().poll();
				if (null != activity || check(cond)) return activity;
			} else {
				if (semaphore.available() < 0) {
					if (block) {
						semaphore.release();
						semaphore.acquire();
					} else {
						return activity;
					}
				}
			}
			if (++next == that.size) next = 0;
		}
	}

	@Native("java", "#1.latch.count == 0")
	def check(cond:FinishState):Boolean = cond.latch.count == 0;
}

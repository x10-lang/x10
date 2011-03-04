/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import x10.util.Stack;
import x10.util.Random;
import x10.lang.Math;
import x10.util.OptionsParser;
import x10.util.Option;
/** 
 * A global load balancing scheduler that uses lifeline graphs.
 * Application code should create an instance of GlobalRunner (with the
 * appropriate parameters), submit a task to it using <code>run</code>
 * and then optionally print out statistics by invoking <code>stats</code>.
 * 
 */
public final class GlobalRunner[T, Z]  implements Runner[T,Z] {
	static type PLH[T,Z]= PlaceLocalHandle[LocalRunner[T,Z]];
	val st:PLH[T,Z];
	/**
	 * @param nu -- number of tasks to process before probing for incoming asyncs
	 * @param w -- number of random steals to attempt before using lifelines
	 * @param e -- enable event logging
	 * @param l -- kind of lifeline network to use. 
	 * <ul><li> 0 -- linear chain, 
	 * <li> 1 -- (do not use this)
	 * <li> 2--  (do not use this)
	 * <li> 3 -- k-hypercube embedding
	 * @param dist -- the distribution over which the globalRunner will operate. dist must be unique.
	 * @param maker -- a TaskFrame maker used to initialize the GlobalRunner in each place.
	 */
	public def this(maker:() => GlobalRef[TaskFrame[T,Z]]) {
		this(new Array[String](0,(i:Int)=>""), Dist.makeUnique(), maker);
	}
	public def this(dist:Dist, maker:() => GlobalRef[TaskFrame[T,Z]]) {
		this(new Array[String](0,(i:Int)=>""), dist, maker);
	}
	public def this(args:Array[String](1), maker:() => GlobalRef[TaskFrame[T,Z]]) {
		this(args, Dist.makeUnique(), maker);
	}
	public def this(args:Array[String](1), dist:Dist, maker: ()=> GlobalRef[TaskFrame[T, Z]]) {
		val opts = new OptionsParser(args, null,
				[
				 Option("v", "", "Verbose if 1"),
				 Option("e", "", "Event logs, default 0 (no)."),
				 Option("k", "", "Number of items to steal; default 0. If 0, steal half. "),
				 Option("n", "", "Number of nodes to process before probing."),
				 Option("w", "", "Number of thieves to send out."),
				 Option("z", "", "Dimension of the hypercube")
				 ]);
		val e = opts("-e", 0)==1;
		val nu = opts("-n",200) as UInt;
		val w = opts("-w", 1) as UInt;
		val z = opts("-z", 1) as UInt;
		val verbose = opts("-v", 0) == 1;
		Console.OUT.println("e=" + e +
				" n=" + nu +
				      " w=" + w +
				            " z=" + z +
				                  " base=" + NetworkGenerator.findW(Place.MAX_PLACES, z));
		
		val lifelineNetwork:Rail[Rail[Int]] = 
			NetworkGenerator.generateSparseEmbedding (Place.MAX_PLACES, z);
		this.st = PlaceLocalHandle.make[LocalRunner[T, Z]](dist, 
				()=>new LocalRunner[T, Z](  nu, w, e, lifelineNetwork(here.id), 
						(maker() as GlobalRef[TaskFrame[T,Z]]{self.home==here})()));
		NetworkGenerator.printNetwork(lifelineNetwork);
	}
	/**
	 * Run the given task as the root task. Tasks created during its execution will be globally load balanced
	 * across the places specified in the distribution supplied on creation of this object. On completion
	 * of this method, the task has completed. 
	 */
	public def apply(t:T, reducer:Reducible[Z]):Z = st().main(st, t, reducer);
	/**
	 * Return the results of the computation. Should only be called after <code>run(t:T)</code>
	 * has returned.
	 * @param time -- the time the computation took (input)
	 * @param verbose -- if set, per place statistics are printed out.
	 */
	public def stats(time:Long, verbose:Boolean) {
		val st = this.st;
		val allCounters 
		= Rail.make[Counter](Place.MAX_PLACES,
				(i:Int) => at(Place(i)) st().counter);
		st().counter.stats(allCounters, time, verbose);
	}
	
	/**
	 * Utility 
	 */
	final static class FixedSizeStack[S] {
		val data:Rail[S];
		var last:Int=-1;
		val size:Int;
		def this(n:Int, t:S) {
			data = Rail.make[S](n, (i:Int) => t);
			size= n;
		}
		def empty():Boolean= last < 0;
		def pop():S = data(last--);
		def push(t:S) {
			data(++last)=t;
		}
		def size():Int=last+1;
	}
	
	/**
	 * An instance of <code>LocalRunner</code> lives in each place.
	 * It is the workhorse for the global load balancing scheduler.
	 */
	final static class LocalRunner[T, Z] {
		
		val width:Int, nu:Int;
		
		// Holds the incoming activated lifelines
		val thieves:FixedSizeStack[Int];
		
		// Holds tasks to be executed
		val stack = new Stack[T]();
		
		// The outgoing edges in the lifeline graph
		val myLifelines:Rail[Int];
		
		// Which of the lifelines have I actually activated?
		// Used to avoid activating a lifeline which has already been
		// activated (e.g. in a previous incarnaation) and 
		// which hasnt returned loot yet. 
		val lifelinesActivated: Rail[Boolean];
		
		val logEvents:Boolean;
		
		// Used for random stealing
		val myRandom = new Random();
		
		// Holds statistics for the engine execution.
		public val counter = new Counter[T]();
		
		// Is an async active at this place?
		var active:Boolean=false;
		
		// Indicator reset when loot arrives. Can be tested by current async
		// after network activity (probe, at) to determine if any loot was
		// received during this network activity.
		var noLoot:Boolean=true;
		
		// the frame for execution of the task.
		val frame:TaskFrame[T,Z];
		
		// the dimensionality of the hypercube in which the lifeline graph is embedded
		val z:Int;
		
		/** Initialize the state. 
		 * @param nu -- Number of tasks to be processed before network is probed
		 * @param w -- Number of random steals to be attempted before resorting to lifeline network
		 * @param e -- True if events are to be logged
		 * @param lifelineNetwork -- Outgoing edges in the lifeline graph
		 * @param f -- TaskFrame for the tasks to be executed at this place.
		 */
		public def this (
				nu:Int, 
				w:Int, 
				e:Boolean, 
				lifelineNetwork:Rail[Int],
				f:TaskFrame[T,Z]) {
			this.nu=nu; 
			this.myLifelines = lifelineNetwork;
			this.z = lifelineNetwork.length; // assume symmetric.
			this.width=w;
			this.logEvents=e;
			this.thieves = new FixedSizeStack[Int](z, 0);
			this.lifelinesActivated = Rail.make[Boolean](Place.MAX_PLACES, false);
			this.frame = f;
		}
		
		/** Process the current task.
		 */
		final def processSubtree (task:T):Void offers Z {
			++counter.nodesCounter;
			frame.runTask(task, stack);
		}
		
		/**
		 * Process the loot - the Rail[T] of tasks received from the environment.
		 * 
		 */
		final def processLoot(loot: Rail[T], lifeline:Boolean):Void offers Z {
			Event.event("Processing loot of size " + loot.length + " with stack "+ stack.size());
			counter.incRx(lifeline, loot.length);
			val time = System.nanoTime();
			for (r in loot) 
				processSubtree(r);
			counter.timeComputing += (System.nanoTime() - time);    
			Event.event("Processing finishes with loot, stack "+ stack.size());
		}
		
		/** Process at most n tasks from the stack.
		 */
		final def processAtMostN(n:Int):void offers Z{
			val time = System.nanoTime();
			Event.event("Processing " + n + " with size "+ stack.size());
			for (var count:Int=0; count < n; count++) {
				val e = stack.pop();
				processSubtree(e);
			}
			Event.event("Processing finishes with size "+ stack.size());
			counter.timeComputing += (System.nanoTime() - time);
		}
		
		/** Heart of the GlobalRunner engine.
		 * <p> Process each task in the stack, in chunks of size at mot nu. Probe for incoming
		 * messages. Distribute loot to active lifelines (if any). Repeat until stack is empty.
		 * Attempt to steal. If attempt succeeds, process loot. This will result in tasks
		 * being added to the stack, so repeat the loop. Check if loot was received asynchronously,
		 * e.g. through a previously activated lifeline and if so, repeat the loop.
		 * <p> Exit loop when stack is empty, and terminate. 
		 * 
		 */
		final def processStack(st:PLH[T, Z]):void offers Z {
			while (true) {
				var n:Int = Math.min(stack.size(), nu);
				while (n > 0) {
					processAtMostN(n);
					val time:Long =  System.nanoTime();
					Runtime.probe();
					val time2 = System.nanoTime();
					counter.timeProbing += (time2 - time);
					
					val numThieves = thieves.size();
					if (numThieves > 0)
						distribute(st, 1, numThieves);
					counter.timeDistributing += (System.nanoTime() -time2);
					
					n = Math.min(stack.size(), nu);
				}
				noLoot=true;
				val loot = attemptSteal(st);
				if (null==loot) { 
					if (noLoot) {
						break;
					} else  { // loot received impicitly, will be in stack
						noLoot=true;
						Event.event("Loot received.");
						continue;
					}
				} else 
					processLoot(loot, false);
			}
			Event.event("Finished main loop.");
		}
		
		/** Distribute loot to activated incoming lifelines.
		 */
		def distribute(st:PLH[T, Z], depth:Int):Void offers Z {
			val time = System.nanoTime();
			val numThieves = thieves.size();
			if (numThieves > 0)
				distribute(st, depth+1, numThieves);
			counter.timeDistributing += (System.nanoTime()  - time);
		}
		def distribute(st:PLH[T,Z], depth:Int, var numThieves:Int) offers Z {
			var lootSize:Int= stack.size();
			if (lootSize > 2) {
				numThieves = Math.min(numThieves, lootSize-2);
				val numToSteal = lootSize/(numThieves+1);
				while  (true) {
					val thief = thieves.pop();
					val loot = stack.pop(numToSteal);
					counter.incTxNodes(numToSteal);
					Event.event("Distributing " + loot.length() + " to " + thief);
					val victim = here.id;
					async at(Place(thief)) 
					   st().launch(st, false, loot, depth+1, victim);
					numThieves --;
					if (numThieves == 0) break;
					// Now some loot may have been lost to an asynchronous
					// steal that got processed in the async. Or some loot may have arrived.
					// So reset lootSize.
					lootSize = stack.size();
					if (lootSize <= 2) {
						Event.event("Breaking off distribution (" + thieves.size() + " left");
						break;
					}
				}
			}
		}
		
		/** This is the code invoked locally by each node when there are no 
		 * more nodes left on the stack. In other words, this function is 
		 * the basis of all pull-based stealing. The push based stealing 
		 * happens through the lifeline system. First, we attempt to get 
		 * work from randomly chosen neighbors (for a certain number of 
		 * tries). If we are not successful, we invoke our lifeline system.
		 */
		def attemptSteal(st:PLH[T,Z]):Rail[T] {
			val P = Place.MAX_PLACES;
			if (P == 1) return null;
			val time = System.nanoTime();
			try {
				val p = here.id;
				for (var i:Int=0; i < width && noLoot; i++) {
					var q_:Int = 0;
					while((q_ =  myRandom.nextInt(P)) == p) ;
					val q = q_;
					counter.incStealsAttempted();
					Event.event("Stealing from Place(" + q +").");
					
					// Potential communication attempt.
					// May suffer incoming thefts or distributions.
					val loot = at (Place(q)) st().trySteal(p);
					if (loot != null) {
						Event.event("Received loot (" + loot.length +
								") from victim(" + q + ").");
						return loot;
					}
				}
				if (! noLoot) {
					Event.event("Interrupted attempt to steal on receiving asynchronous loot.");
					return null;
				}
				
				Event.event("No loot; establishing lifeline(s).");
				var loot:Rail[T] = null;
				val n = myLifelines.length();
				for (var i:Int=0; (i<n) && (noLoot) && (0<=myLifelines(i)); ++i) {
					val lifeline:Int = myLifelines(i);
					if (!lifelinesActivated(lifeline) ) {
						lifelinesActivated(lifeline) = true;
						loot = at(Place(lifeline)) st().trySteal(p, true);
						if (null!=loot) {
							lifelinesActivated(lifeline) = false;
							Event.event("Received loot (" + loot.length +
									") from lifeline(" + lifeline + ")");
							break;
						}
						Event.event("Received no loot from Place(" + lifeline+")");
					}
				}
				Event.event("Established " + n + " lifelines.");
				return loot;
			} finally {
				counter.timeStealing += (System.nanoTime() - time);
			}
		}
		
		/** Try to steal from the local stack --- invoked by either a 
		 * thief at a remote place using asyncs (during attemptSteal) 
		 * or by the owning place itself when it wants to give work to 
		 * a fallen buddy.
		 */
		def trySteal(p:Int)=trySteal(p, false);
		def trySteal(p:Int, isLifeLine:Boolean) : Rail[T] {
			counter.stealsReceived++;
			val length = stack.size();
			if (length <= 2) {
				if (isLifeLine)
					thieves.push(p);
				Event.event("Returning no loot to Place(" + p+ ") on " 
						+ (isLifeLine ? "lifeline " : "") + "steal.");
				return null;
			}
			val numSteals = length/2;
			counter.nodesGiven += numSteals;
			counter.stealsSuffered++;
			return stack.pop(numSteals);
		}
		
		def launch(st:PLH[T,Z], 
				init:Boolean, 
				loot:Rail[T], 
				depth:Int, 
				source:Int):Void offers Z {
			// assert loot != null;
			try {
				Event.event("Place (" + source + ") launches " 
						+ (init ? "init " : "dealt ") + "async with " + 
						(loot == null ? "0" : "" + loot.length) + " tasks.");
				lifelinesActivated(source) = false;
				if (active) {
					noLoot = false;
					processLoot(loot, true);
					// assert (! init);
					// distribute immediately. Note multiple distribution may already be in progress.
					//if (depth > 0) 
					//      distribute(st, depth+1);
					Event.event("Async terminates early.");
					return;
				}
				active=true;
				counter.startLive();
				counter.updateDepth(depth);
				processLoot(loot, true);
				if (depth > 0) 
					distribute(st, depth+1);
				processStack(st);
				counter.stopLive();
				active=false;
				Event.event("Async terminates.");
			} catch (v:Throwable) {
				Console.OUT.println("Exception at " + here);
				v.printStackTrace();
			}
		}
		
		/** Called only on the initial task. The task should create more tasks
		 * and push them on the runner's stack. The global work stealing 
		 * mechanism implemented by this GlobalRunner will take over
		 * and execute the tasks on all available places.
		 * 
		 */
		
		def main (st:PLH[T,Z], rootTask:T, reducer: Reducible[Z]):Z  {
			val P=Place.MAX_PLACES;
			Event.event("Start main finish.");
			val startAtZero = System.nanoTime();
			counter.lastTimeStamp = startAtZero;
			counter.startLive();
			val result:Z =  finish (reducer) {
				Event.event("Launch main.");
				frame.runRootTask(rootTask, stack);
				
				val lootSize = stack.size()/P;
				// Break out loot ahead of time because
				// a thief might come in the middle and steal some from you.
				val loots = Rail.make(P-1,(i:Int)=> stack.pop(lootSize));
				for (var pi:Int=1 ; pi<P ; ++pi) {
					val loot = loots(pi-1);
					Event.event("Launching at place " + pi+".");
					// Possible entry point for a thief.
					val pi_ = pi;
					async at(Place(pi_)) 
					   st().launch(st, true, loot, 0, 0);
					counter.incTxNodes(lootSize);
				}
				active=true;
				processStack(st);
				active=false;
				Event.event("Finish main.");
				counter.stopLive();
			}; 
			counter.totalTimeAtZero = (System.nanoTime() - startAtZero);
			Event.event("End main finish.");
			return result;
		}
	}
}


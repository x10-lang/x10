import x10.util.Stack;
import x10.util.Random;
import x10.lang.Math;

/** 
 * A global load balancing scheduler that uses lifeline graphs.
 * Application code should create an instance of GlobalRunner (with the
 * appropriate parameters), submit a task to it using <code>run</code>
 * and then optionally print out statistics by invoking <code>stats</code>.
 * 
 */
public final class GlobalRunner[T]  {
	static type PLH[T]= PlaceLocalHandle[LocalRunner[T]];
    global val st:PLH[T];
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
    def this(nu:Int, w:Int, e:Boolean, l:Int, z:Int, dist:Dist, maker: ()=> TaskFrame[T]) {
    	   val lifelineNetwork:ValRail[ValRail[Int]] = 
               (0==l) ? NetworkGenerator.generateRing(Place.MAX_PLACES) :
               (1==l) ? NetworkGenerator.generateHyperCube(Place.MAX_PLACES):
               (2==l) ? NetworkGenerator.generateChunkedGraph (Place.MAX_PLACES, z):
                    NetworkGenerator.generateSparseEmbedding (Place.MAX_PLACES, z);
  	  this.st = PlaceLocalHandle.make[LocalRunner[T]](dist, 
        		()=>new LocalRunner[T](  nu, w, e, lifelineNetwork(here.id), 
        				maker() as TaskFrame[T]!));
      
    }
    /**
     * Run the given task as the root task. Tasks created during its execution will be globally load balanced
     * across the places specified in the distribution supplied on creation of this object. On completion
     * of this method, the task has completed. 
     */
    public def run(t:T) {
    	st().main(st, t);
    }
    /**
     * Return the results of the computation. Should only be called after <code>run(t:T)</code>
     * has returned.
     * @param time -- the time the computation took (input)
     * @param verbose -- if set, per place statistics are printed out.
     */
    public def stats(time:Long, verbose:Boolean) {
		  val allCounters 
		   = ValRail.make[Counter.ValCounter](Place.MAX_PLACES,
				   (i:Int) => at(Place(i)) this.st().valCounter());
    	  st().counter.stats(allCounters, time, verbose);
    }
  
    /**
     * Utility 
     */
    final static class FixedSizeStack[S] {
    	val data:Rail[S]!;
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
    final static class LocalRunner[T] {

    	val width:Int, nu:Int;
    
        // Holds the incoming activated lifelines
	    val thieves:FixedSizeStack[Int]!;
    
        // Holds tasks to be executed
    	val stack = new Stack[T]();
    	
    	// The outgoing edges in the lifeline graph
    	val myLifelines:ValRail[Int];

    	// Which of the lifelines have I actually activated?
    	// Used to avoid activating a lifeline which has already been
    	// activated (e.g. in a previous incarnaation) and 
    	// which hasnt returned loot yet. 
    	val lifelinesActivated: Rail[Boolean]!;
    	
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
    	val frame:TaskFrame[T]!;
    	
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
    			lifelineNetwork:ValRail[Int],
    			f:TaskFrame[T]!) {
    		this.nu=nu; 
    		this.myLifelines = lifelineNetwork;
    		this.z = lifelineNetwork.length; // assume symmetric.
    		this.width=w;
    		this.logEvents=e;
    		this.thieves = new FixedSizeStack[Int](z, 0);
    		this.lifelinesActivated = Rail.make[Boolean](Place.MAX_PLACES, (Int)=>false);
    		this.frame = f;
    		// NetworkGenerator.printMyEdges(myLifelines);
    	}
    	
    	def valCounter() = counter.toVal();

    	/** Process the current task.
    	 */
    	final def processSubtree (task:T) {
    		++counter.nodesCounter;
    		frame.runTask(task, stack);
    	}

    	/**
    	 * Process the loot - the ValRail of tasks received from the environment.
    	 * 
    	 */
    	final def processLoot(loot: ValRail[T], lifeline:Boolean) {
    		counter.incRx(lifeline, loot.length);
    		val time = System.nanoTime();
    		for (r in loot) 
    			processSubtree(r);
    		counter.timeComputing += (System.nanoTime() - time);	
    	}
    	
    	/** Process at most N tasks from the stack.
    	 */
    	final def processAtMostN(n:Int) {
    		val time = System.nanoTime();
    		for (var count:Int=0; count < n; count++) {
    			val e = stack.pop();
    			processSubtree(e);
    		}
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
    	final def processStack(st:PLH[T]) {
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
    		    val loot = attemptSteal(st);
    		    if (null==loot) { 
    		    	if (noLoot) {
    		    		break;
    		    	} else  {
    		    		noLoot=true;
    		    		continue;
    		    	}
    		    } else 
    		    	processLoot(loot, false);
    		}
    		Event.event("Finished main loop.");
    	}

    	/** Distribute loot to activated incoming lifelines.
    	 */
    	def distribute(st:PLH[T], depth:Int) {
    		val time = System.nanoTime();
    		val numThieves = thieves.size();
    		if (numThieves > 0)
    			distribute(st, 1, numThieves);
    		counter.timeDistributing += (System.nanoTime()  - time);
    	}
    	def distribute(st:PLH[T], depth:Int, var numThieves:Int) {
    		val lootSize= stack.size();
    		if (lootSize > 2) {
    			numThieves = Math.min(numThieves, lootSize-2);
    			val numToSteal = lootSize/(numThieves+1);
    			for (var i:Int=0; i < numThieves; ++i) {
    				val thief = thieves.pop();
    				val loot = stack.pop(numToSteal);
    				counter.incTxNodes(numToSteal);
    				
    				Event.event("Distributing " + loot.length() + " to " + thief);
    				val victim = here.id;
    				async (Place(thief)) 
    				   st().launch(st, false, loot, depth, victim);
    			}
    		}
    	}

    	/** This is the code invoked locally by each node when there are no 
	 more nodes left on the stack. In other words, this function is 
	 the basis of all pull-based stealing. The push based stealing 
	 happens through the lifeline system. First, we attempt to get 
	 work from randomly chosen neighbors (for a certain number of 
	 tries). If we are not successful, we invoke our lifeline system.
    	 */
    	def attemptSteal(st:PLH[T]):ValRail[T] {
    		val time = System.nanoTime();
    		val P = Place.MAX_PLACES;
    		if (P == 1) return null;
    		val p = here.id;
    		for (var i:Int=0; i < width && noLoot; i++) {
    			var q_:Int = 0;
    		    while((q_ =  myRandom.nextInt(P)) == p) ;
    		    val q = q_;
    		    counter.incStealsAttempted();
    		    Event.event("Stealing from " + q);
    		    
    		    // Potential communication attempt.
    		    // May receive incoming thefts or distributions.
    		    val loot = at (Place(q)) st().trySteal(p);
    		    if (loot != null) {
    		    	counter.timeStealing += (System.nanoTime() - time);
    		    	return loot;
    		    }
    		}
    		if (! noLoot) {
    			counter.timeStealing += (System.nanoTime() - time);
    			return null;
    		}
    		
    		Event.event("No loot; establishing lifeline(s).");
    		var loot:ValRail[T] = null;
    		for (var i:Int=0; (i<myLifelines.length()) && (noLoot) && (0<=myLifelines(i)); 
    		      ++i) {
    			val lifeline:Int = myLifelines(i);
    		    if (!lifelinesActivated(lifeline) ) {
    			   lifelinesActivated(lifeline) = true;
    			   loot = at(Place(lifeline)) st().trySteal(p, true);
    			   if (null!=loot) {
    				  lifelinesActivated(lifeline) = false;
    				  break;
    			   }
    		   }
    		}
    		counter.timeStealing += (System.nanoTime() - time);
    		return loot;
    	}

    	/** Try to steal from the local stack --- invoked by either a 
	      thief at a remote place using asyncs (during attemptSteal) 
	      or by the owning place itself when it wants to give work to 
	      a fallen buddy.
    	 */
    	def trySteal(p:Int):ValRail[T]=trySteal(p, false);
    	def trySteal(p:Int, isLifeLine:Boolean) : ValRail[T] {
    		counter.stealsReceived++;
    		val length = stack.size();
    		if (length <= 2) {
    			if (isLifeLine)
    				thieves.push(p);
    			Event.event("Returning no loot on steal.");
    			return null;
    		}
    		val numSteals = length/2;
    		counter.nodesGiven += (numSteals);
    		counter.stealsSuffered++;
    		return stack.pop(numSteals);
    	}

    	def launch(st:PLH[T], 
    			init:Boolean, 
    			loot:ValRail[T], 
    			depth:Int, 
    			source:Int) {
    		// assert loot != null;
    		try {
    			Event.event("Place" + source + ") launches " 
    					+ (init ? "init " : "dealt ") + "async with " + 
    					(loot == null ? "0" : "" + loot.length) + " tasks.");
    			lifelinesActivated(source) = false;
    			if (active) {
    				noLoot = false;
    				processLoot(loot, true);
    				// assert (! init);
    			    // distribute immediately. Note multiple distribution may already be in progress.
    				//if (depth > 0) 
    				//	distribute(st, depth+1);
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
    		} catch (v:Error) {
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
    	def main (st:PLH[T], 
    			rootTask:T) {
    		val P=Place.MAX_PLACES;
    		Event.event("Start main finish");
    		val startAtZero = System.nanoTime();
    		counter.lastTimeStamp = startAtZero;
    		counter.startLive();
    		finish {
    			Event.event("Launch main");
    			frame.runRootTask(rootTask, stack);
    			++counter.nodesCounter; // root node is never pushed on the stack.

    			val lootSize = stack.size()/P;
    			for (var pi:Int=1 ; pi<P ; ++pi) {
    				val loot = stack.pop(lootSize);
    				async (Place(pi))
    				st().launch(st, true, loot, 0, 0);
    				counter.incTxNodes(lootSize);
    			}
    			active=true;
    			processStack(st);
    			active=false;
    			Event.event("Finish main");
    			counter.stopLive();
    		} 
    		counter.totalTimeAtZero = (System.nanoTime() - startAtZero);
    		Event.event("End main finish");
    	}
    }
}


import x10.util.Stack;
import x10.util.Random;
import x10.lang.Math;

final class BinomialState {
	/* var thief:Int; */
  var thiefs:Rail[Boolean] = 
        Rail.make[Boolean] (x10.lang.Math.log2(Place.MAX_PLACES));
	val width:Int;
	var lifelines:Long=0L;
	var lifelineNodes:Long=0L;
	val stack = new Stack[UTS.SHA1Rand]();
  val myLifelines:ValRail[Int] = 
        ValRail.make[Int](x10.lang.Math.log2(Place.MAX_PLACES),
                          (i:Int) => (1 << i) ^ here.id);


	val q:Long, m:Int, k:Int, nu:Int;
	val logEvents:Boolean;
	val myRandom = new Random();
  public val counter = new Counter();
	static type PLH= PlaceLocalHandle[BinomialState];
	static type SHA1Rand = UTS.SHA1Rand;

	/** Initialize the state. Executed at all places when executing the 
	 PlaceLocalHandle.make command in main (of UTS).
	 */
	public def this (q:Long, m:Int, k:Int, nu:Int, w:Int, e:Boolean) {
		this.q = q; this.m = m; this.k=k; this.nu=nu;
		width=w;
		/* thief= -1; */
		logEvents=e;
	}

	def event(s:String) {
		event(logEvents, s);
	}

	def event(verbose:Boolean, s:String) {
		if (verbose)
			Console.OUT.println("[Place(" + here.id+"), at " 
					+ System.nanoTime() + "] " + s);
	}

	/** Check if the current node (governed by the SHA1Rand state) has any
	 children. If so, push it onto the local stack.
	 */
	final def processSubtree (rng:SHA1Rand) {
		processSubtree(rng, (rng() < q) ? m : 0);
	}

	/** Same as above, but called when we have already made a decision that
	 there are going to be this "numChildren" children. Its useful to 
	 split the functionality from the function (processSubtree) above as 
	 in some cases (for eg., processing the root node), we just want to 
	 directly create children instead of basing the decision off of a 
	 random number state.
	 */
	final def processSubtree (rng:SHA1Rand, numChildren:Int) {
		counter.nodesCounter++;
		// Iterate over all the children and push on stack
		for (var i:Int=0 ; i<numChildren ; ++i) 
			stack.push(SHA1Rand(rng, i));
	}

	/** Return an array of "k" elements popped from the local stack.*/
	final def pop(k:Int) = ValRail.make[SHA1Rand](k, (int)=> stack.pop());

  /** A trivial function to calculate minimum of 2 integers */
	def min(i:int,j:int) = i < j ? i : j;

	/** Go through each element in the stack, process it (generate its
	 children, and add them to the stack) until there is nothing left
	 on the stack. At this point, attempt to steal. If nothing can be 
	 stolen, terminate for now. Also, after processing a particular 
	 number of nodes, check if there are any outstanding messages to
	 handle and also, distribute a chunk of the local stack (work) to 
	 our lifeline buddy.
	 */
	def processStack(st:PLH) {
		while (true) {
			var n:Int = min(stack.size(), nu);
			l: while (n > 0) {
				val time = System.nanoTime();
				for (var count:Int=0; count < n; count++) {
					val e = stack.pop();
					processSubtree(e);
				}
				counter.incTimeComputing(System.nanoTime() - time);
				Runtime.probe();	
				distribute(st, 1);
				n = min(stack.size(), nu);
			}
			val loot = attemptSteal(st);
			if (loot == null) {
					break;
			}
			counter.incRxNodes(loot.length);
			val time = System.nanoTime();
			for ( r in loot) 
				processSubtree(r);
			counter.incTimeComputing(System.nanoTime() - time);
		}
	    event("Finished main loop.");
	}

	/** If our buddy has requested a lifeline, and we have ample supply 
	 of nodes, give him half (i.e, launch a remote async).
	 */
	def distribute(st:PLH, depth:Int) {
    /*
		if (thief >= 0) {
			event("Distributing to " + thief);
			val loot = trySteal(thief);
			if (loot != null) {
				async (Place(thief))
				st().launch(st, false, loot, depth);
				thief = -1;
			}
		} */
    /* We will go through myLifelines and see how many buddies requested 
       a lifeline. Count the number, and divide the work we have equally
       amongst the buddies. 
       ANJU: What if the people requested the lifeline are mutually 
       registered as each other's lifelines? There will be a cascade of
       activity where people are giving each other a part of their work!
       */
    val thiefsIterator:Iterator[Boolean]  = thiefs.iterator();
    var myLifelinesPosition:Int = 0;
    while (thiefsIterator.hasNext()) {
      if (true == thiefsIterator.next()) {
        val thief:Int = myLifelines(myLifelinesPosition);
        val loot = trySteal (thief);
        if (null != loot) {
				  async (Place(thief))
				  st().launch(st, false, loot, depth);
          thiefs (myLifelinesPosition) = false;
        } else {
          /* No point iterating any further --- there does not seem to be 
             anything to steal here. */
          break;
        }
      }
      ++myLifelinesPosition;
    }
	}

	/** This is the code invoked locally by each node when there are no 
	 more nodes left on the stack. In other words, this function is 
	 the basis of all pull-based stealing. The push based stealing 
	 happens through the lifeline system. First, we attempt to get 
	 work from randomly chosen neighbors (for a certain number of 
	 tries). If we are not successful, we invoke our lifeline system.
	 */
	def attemptSteal(st:PLH):ValRail[SHA1Rand] {
		event("Attempt steal");
		val P = Place.MAX_PLACES;
		if (P == 1) return null;
		val p = here.id;
		   for (var i:Int=0; i < width; i++) {
			  var q_:Int = 0;
		    while((q_ =  myRandom.nextInt(P)) == p) ;
		    val q = q_;
		    counter.incStealsAttempted();
		    event("Stealing from " + q);
		    val loot = at (Place(q)) st().trySteal(p);
		    if (loot != null) {
			   event("Steal succeeded with " + 
					(loot == null ? 0 : loot.length()) + " items");
			   return loot;
		    }
		}
		event("No loot; establishing lifeline.");
/*
		// resigned to make a lifeline steal.
		val lifeline =  (p+1) % P;
		val loot = at(Place(lifeline)) st().trySteal(p); 
		event("Lifeline steal result " + 
				(loot==null ? 0 : loot.length()));
		return loot;
*/
    /* Did not get anything from normal channels --- try lifeline
       instead. We have log_2(Places.MAX_PLACES) lifelines. We will
       try to steal from each of them. If none of them succeed, then 
       setup the lifeline and getback
    */
    val lifelineIter:Iterator[Int] = myLifelines.iterator();
    var loot:ValRail[SHA1Rand] = null;
    while (lifelineIter.hasNext()) {
      val lifeline:Int = lifelineIter.next();
		  loot = at(Place(lifeline)) st().trySteal(p); 
		  event("Lifeline steal result " + (loot==null ? 0 : loot.length()));
      if (null!=loot) break;
    }
    return loot;
	}

  /* Check if a particular node is the current node's lifeline */
  def storeIfLifeline (val node:Int) {
    val lifelineIter:Iterator[Int] = myLifelines.iterator();
    var myLifelinesPosition:Int = 0;
    while (lifelineIter.hasNext()) {
      if (node == lifelineIter.next()) {
        thiefs (myLifelinesPosition) = true;    
        break;
      }
      ++myLifelinesPosition;
    }
  }

	/** Try to steal from the local stack --- invoked by either a 
	 theif at a remote place using asyncs (during attemptSteal) 
	 or by the owning place itself when it wants to give work to 
	 a fallen buddy.
	 */
	def trySteal (p:Int) : ValRail[SHA1Rand] {
		counter.incStealsReceived();
		val length = stack.size();
		if (length <= 2) {
      /*
			if (here.id == (p+1)% Place.MAX_PLACES) {
				thief = p;
				event("Established lifeline donee " + thief);
			}
      */
      /* We want to check if this is from one of our neighbors, in which 
         case, they were trying to establish a lifeline. So, store theif
         so that we can later send him a lifeline if need be. */
      storeIfLifeline (p);
			event("Returning null");
			return null;
		}
		val numSteals = length/2;
		counter.incTxNodes(numSteals);
		return stack.pop(numSteals);
	}

	def launch(st:PLH, init:Boolean, loot:ValRail[SHA1Rand], depth:Int) {
		counter.startLive();
		counter.updateDepth(depth);
		val n = loot == null ? 0 : loot.length;
		if (! init) {
			counter.incRx(depth > 0, n);
		}
		if (loot != null) {
			val time = System.nanoTime();
			for (r in loot) processSubtree(r);
			counter.incTimeComputing(System.nanoTime() - time);
		}
		if (depth > 0) distribute(st, depth+1);
		processStack(st);
		counter.stopLive();
	}

	/** Called only for the root node. Processes all the children of 
	 the root node and then proceeds to divide these children up 
	 evenly amongst all the places. This is the bootstrap mechanism
	 for distributed UTS.
	 */
	def main (st:PLH, b0:Int, rng:SHA1Rand) {
		val P=Place.MAX_PLACES;
		event("Start main finish");
		counter.lastTimeStamp = System.nanoTime();
		finish {
			event("Launch main");
			processSubtree(rng, b0);
			val lootSize = stack.size()/P;
			for (var pi:Int=1 ; pi<P ; ++pi) {
				val loot = pop(lootSize);
				async (Place(pi))
				   st().launch(st, true, loot, 1);
			}
			processStack(st);
			event("Finish main");
		} 
		event("End main finish");
	}
}


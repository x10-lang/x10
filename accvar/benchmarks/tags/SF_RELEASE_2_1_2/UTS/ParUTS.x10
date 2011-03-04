
import x10.util.Random;
import x10.lang.Math;
import x10.util.Stack;
import x10.util.List;
import x10.util.ArrayList;

final class ParUTS {
    static type PLH= PlaceLocalHandle[ParUTS];
    static type SHA1Rand = UTS.SHA1Rand;
    static type TreeNode = UTS.TreeNode;
    static type Constants = UTS.Constants;

    static val gatherTimes = false;
    
    final static class FixedSizeStack[T] {
	val data:Rail[T];
	var last:Int;
	val size:Int;
	def this(n:Int, t:T) {
	    data = Rail.make[T](n, (i:Int) => t);
	    last = -1;
	    size= n;
	}
	def empty():Boolean= last < 0;
	def pop():T = data(last--);
	def push(t:T) {
	    data(++last)=t;
	}
	def size():Int=last+1;
        
    }
    val thieves:FixedSizeStack[Int];
    
    val width:Int;
    val stack = new Stack[TreeNode]();
    val myLifelines:Rail[Int];
    
    // Which of the lifelines have I actually activated?
    val lifelinesActivated: Rail[Boolean];
    
    val treeType:Int; // 0=BINOMIAL, 1=GEOMETRIC
    val b0:Int; // root branching factor
    val q:Long, m:Int, k:Int, nu:Int; // For the binomial tree
    val a:Int, d:Int; // For the geometric tree
    
    val l:Int; 
    val z:Int;
    val logEvents:Boolean;
    val myRandom = new Random();
    public val counter:Counter;
    var active:Boolean=false;
    var noLoot:Boolean=true;
    
    /** Initialize the state. Executed at all places when executing the 
	PlaceLocalHandle.make command in main (of UTS). BINOMIAL
    */
    public def this (b0:Int, 
		     q:Long, 
		     m:Int, 
		     k:Int, 
		     nu:Int, 
		     w:Int, 
		     e:Boolean, 
		     l:Int,
		     lifelineNetwork:Rail[Int]) {
	this.treeType = Constants.BINOMIAL;
	this.b0 = b0;
	this.q = q; 
	this.m = m; 
	this.k=k; 
	this.nu=nu; 
	this.l = l;
	this.myLifelines = lifelineNetwork;
	this.z = lifelineNetwork.length; // assume symmetric.
	this.width=w;
	this.logEvents=e;
	this.counter = new Counter(logEvents);
	thieves = new FixedSizeStack[Int](z, 0);
	lifelinesActivated = Rail.make[Boolean](Place.MAX_PLACES, (Int)=>false);
//	printLifelineNetwork();
	
	this.a = -1; 
	this.d = -1;
    }
    
    /** Initialize the state. Executed at all places when executing the 
	PlaceLocalHandle.make command in main (of UTS). GEOMETRIC
    */
    public def this (b0:Int, 
		     a:Int, 
		     d:Int, 
		     k:Int, 
		     nu:Int, 
		     w:Int, 
		     e:Boolean, 
		     l:Int,
		     lifelineNetwork:Rail[Int]) {
	this.treeType = Constants.GEOMETRIC;
	this.b0 = b0;
	this.a = a; 
	this.d = d; 
	this.k=k; 
	this.nu=nu; 
	this.l = l;
	this.myLifelines = lifelineNetwork;
	this.z = lifelineNetwork.length; // assume symmetric.
	this.width=w;
	this.logEvents=e;
	this.counter = new Counter(logEvents);
	thieves = new FixedSizeStack[Int](Place.MAX_PLACES, 0);
	lifelinesActivated = Rail.make[Boolean](Place.MAX_PLACES, (Int)=>false);
	printLifelineNetwork();
	
	this.q = -1; 
	this.m = -1;
    }
    
    def printLifelineNetwork () {
	Console.OUT.print (here.id + " =>");
	for (var i:Int=0; i<myLifelines.length(); ++i) 
	    if (-1 != myLifelines(i)) 
		Console.OUT.print  (" " + myLifelines(i) +  " " +
				    new PAdicNumber(NetworkGenerator.findW(Place.MAX_PLACES, z), z, myLifelines(i)));
	    else Console.OUT.print (" X");
	Console.OUT.println ();
    }
    
    def event(s:String) { 
        /*      event(false, s); */
    }
    
    /*
      def event(verbose:Boolean, s:String) {
      if (verbose)
      Console.OUT.println("[Place(" + here.id+"), at " 
      + System.nanoTime() + "] " + s);
      }
    */
    
    /** Check if the current node (governed by the SHA1Rand state) has any
	children. If so, push it onto the local stack.
    */
    final def processSubtree (node:TreeNode) {
	++counter.nodesCounter;
	if (Constants.BINOMIAL==treeType) 
	    TreeExpander.binomial (q, m, node, stack);
	else 
	    TreeExpander.geometric (a, b0, d, node, stack);
    }
    
    final def processLoot(loot: Rail[TreeNode], lifeline:Boolean) {
	counter.incRx(lifeline, loot.length);
	val time = gatherTimes ? System.nanoTime() : 0;
	for (r in loot) processSubtree(r);
	if (gatherTimes) counter.incTimeComputing (System.nanoTime() - time);    
    }
    
    final def processAtMostN(n:Int) {
	val time = gatherTimes ? System.nanoTime() : 0;
	for (var count:Int=0; count < n; count++) {
	    val e = stack.pop();
	    processSubtree(e);
	}
	if (gatherTimes) counter.incTimeComputing (System.nanoTime() - time);  
    }
    
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
    final def processStack(st:PLH) {
	while (true) {
	    var n:Int = min(stack.size(), nu);
	    while (n > 0) {
		processAtMostN(n);
                
		val time:Long =  gatherTimes ? System.nanoTime() : 0;
		Runtime.probe();
		if (gatherTimes) counter.incTimeProbing (System.nanoTime() - time);
		val numThieves = thieves.size();
		if (numThieves > 0) distribute(st, 1, numThieves);
		n = min(stack.size(), nu);
	    }
	    val loot = attemptSteal(st);
	    if (null==loot) { 
		if (! noLoot) {
		    noLoot=true;
		    continue;
		}
		else {
		    break;
		}
	    } else {
		processLoot(loot, false);
	    }
	}
	event("Finished main loop.");
    }
    
    /** If our buddy/buddies have requested a lifeline, and we have ample supply 
	of nodes, give him half (i.e, launch a remote async). We are not timing this 
	section because it ultimately turns around and calls the distribute() function
	below, which is timed.
    */
    def distribute(st:PLH, depth:Int) {
	val numThieves = thieves.size();
	if (numThieves > 0) distribute(st, 1, numThieves);
    }
    
    def distribute(st:PLH, depth:Int, var numThieves:Int) {
	val time = gatherTimes ? System.nanoTime() : 0;
	val lootSize= stack.size();
	if (lootSize > 2u) {
	    numThieves = min(numThieves, lootSize-2);
	    val numToSteal = lootSize/(numThieves+1);
	    for (var i:Int=0; i < numThieves; ++i) {
		val thief = thieves.pop();
		val loot = stack.pop(numToSteal);
		counter.incTxNodes(numToSteal);
		// event("Distributing " + loot.length() + " to " + thief);
		val victim = here.id;
		async at(Place(thief)) 
		    st().launch(st, false, loot, depth, victim);
	    }
	}
	if (gatherTimes) counter.incTimeDistributing (System.nanoTime()-time);
    }
    
    /** This is the code invoked locally by each node when there are no 
	more nodes left on the stack. In other words, this function is 
	the basis of all pull-based stealing. The push based stealing 
	happens through the lifeline system. First, we attempt to get 
	work from randomly chosen neighbors (for a certain number of 
	tries). If we are not successful, we invoke our lifeline system.
    */
    def attemptSteal(st:PLH):Rail[TreeNode] {
	val time = gatherTimes ? System.nanoTime() : 0;
	val P = Place.MAX_PLACES;
	if (P == 1) return null;
	val p = here.id;
	for (var i:Int=0; i < width && noLoot; i++) {
	    var q_:Int = 0;
	    while((q_ =  myRandom.nextInt(P)) == p) ;
	    val q = q_;
	    counter.incStealsAttempted();
	    // event("Stealing from " + q);
	    // Potential communication attempt.
	    // May receive incoming thefts or distributions.
	    val loot = at (Place(q)) st().trySteal(p);
	    if (loot != null) {
		//event("Steal succeeded with " + 
		//(loot == null ? 0 : loot.length()) + " items");
		if (gatherTimes) counter.incTimeStealing(System.nanoTime() - time);
		return loot;
	    }
	}
	if (! noLoot) {
	    if (gatherTimes) counter.incTimeStealing(System.nanoTime() - time);
	    return null;
	}
	event("No loot; establishing lifeline(s).");
	
	// resigned to make a lifeline steal from one of our lifelines.
	var loot:Rail[TreeNode] = null;
	for (var i:Int=0; 
	     (i<myLifelines.length()) && (noLoot) && (0<=myLifelines(i)); 
	     ++i) {
	    val lifeline:Int = myLifelines(i);
	    if (!lifelinesActivated(lifeline) ) {
		lifelinesActivated(lifeline) = true;
		loot = at(Place(lifeline)) st().trySteal(p, true);
		// event("Lifeline steal result " + (loot==null ? 0 : loot.length()));
		if (null!=loot) {
		    lifelinesActivated(lifeline) = false;
		    break;
		}
	    }
	}
	if (gatherTimes) counter.incTimeStealing(System.nanoTime() - time);
	return loot;
    }
    
    /** Try to steal from the local stack --- invoked by either a 
	theif at a remote place using asyncs (during attemptSteal) 
	or by the owning place itself when it wants to give work to 
	a fallen buddy.
    */
    def trySteal(p:Int):Rail[TreeNode]=trySteal(p, false);
    def trySteal(p:Int, isLifeLine:Boolean) : Rail[TreeNode] {
	counter.stealsReceived++;
	val length = stack.size();
	val numSteals = k==0u ? (length >=2u ? length/2u : 0u)
	    : (k < length ? k : (k/2u < length ? k/2u : 0u));
	if (numSteals==0u) {
	    if (isLifeLine)
		thieves.push(p);
	    event("Returning null");
	    return null;
	}
	val loot = stack.pop(numSteals);
	counter.nodesGiven += numSteals;
	counter.stealsSuffered++;
	return loot;
    }
    
    def launch(st:PLH, 
	       init:Boolean, 
	       loot:Rail[TreeNode], 
	       depth:Int, 
	       source:Int) {
	assert loot != null;
	try {
	    lifelinesActivated(source) = false;
	    if (active) {
		noLoot = false;
		processLoot(loot, true);
		assert (! init);
		/*
		// Now you can return, the outer activity will handle the data on the stack.
		if (depth > 0) distribute(st, depth+1);
		*/
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
	} catch (v:Throwable) {
	    Console.OUT.println("Exception at " + here);
	    v.printStackTrace();
	} catch (v:Error) {
	    Console.OUT.println("Exception at " + here);
	    v.printStackTrace();
	}
    }
    
    /** Called only for the root node. Processes all the children of 
	the root node and then proceeds to divide these children up 
	evenly amongst all the places. This is the bootstrap mechanism
	for distributed UTS.
    */
    def main (st:PLH, 
	      rootNode:TreeNode) {
	val P=Place.MAX_PLACES;
	event("Start main finish");
	counter.startLive();
	finish {
	    event("Launch main");
	    if (Constants.BINOMIAL==treeType) { 
		TreeExpander.processBinomialRoot (b0, rootNode, stack);
            } else {
		TreeExpander.geometric (a, b0, d, rootNode, stack);
            }
            ++counter.nodesCounter; // root node is never pushed on the stack.
	    
	    val lootSize = stack.size()/P;
	    for (var pi:Int=1 ; pi<P ; ++pi) {
		val time = gatherTimes ? System.nanoTime() : 0;
		val loot = stack.pop(lootSize);
		if (gatherTimes) counter.incTimePreppingSteal (System.nanoTime() - time);
		val pi_ = pi;
		async at(Place(pi_))
		    st().launch(st, true, loot, 0, 0);
		counter.incTxNodes(lootSize);
	    }
            active=true;
	    processStack(st);
            active=false;
	    event("Finish main");
	    counter.stopLive();
	} 
	event("End main finish");
    }
}


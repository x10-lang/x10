import x10.compiler.*;
import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Random;
import x10.compiler.Inline;

public class BC {
	val graph:Graph;
	public val N:Int;
	public val M:Int;
	public val verticesToWorkOn:Rail[Int];
	public val betweennessMap:Rail[Double];
	public var count:Long = 0;
	public var refTime:Long = 0;
	public var accTime:Double = 0;
	public var accYTime:Long = 0;
	public var toYield:Boolean = false; // by default, don't yield
	var yFs:Rail[Long] = new Rail[Long](10); // insert at most 10 yield points.
	public static NO_YIELD:String = "NO"; 
	
	// These are the per-vertex data structures.
	var predecessorMap:Rail[Int];
	val predecessorCount:Rail[Int];
	val distanceMap:Rail[Long];
	val sigmaMap:Rail[Long];
	public val regularQueue:FixedRailQueue[Int];
	val deltaMap:Rail[Double];
	
	var yieldTime:Long = 0L;
	
	/**
	 * A function to shuffle the vertices randomly to give better work dist.
	 */
	private def permuteVertices() {
		val prng = new Random(1);
		
		for(var i:Int=0n; i<N; i++) {
			val indexToPick = prng.nextInt(N-i);
			val v = verticesToWorkOn(i);
			verticesToWorkOn(i) = verticesToWorkOn(i+indexToPick);
			verticesToWorkOn(i+indexToPick) = v;
		}
	}
	
	/**
	 * Dump the betweenness map.
	 */
	public def printBetweennessMap() {
		for(var i:Int=0n; i<N; ++i) {
			if(betweennessMap(i) != 0.0) {
				Console.OUT.println("(" + i + ") -> " + betweennessMap(i));
			}
		}
	}
	
	/**
	 * substring helper function
	 */
	public static def sub(str:String, start:Int, end:Int) = str.substring(start, Math.min(end, str.length()));
	
	/**
	 * Dump the betweenness map.
	 * @param numDigit number of digits to print
	 */
	public final def printBetweennessMap(val numDigit:Int) {
		for(var i:Int=0n; i<N; ++i) {
			if(betweennessMap(i) != 0.0) {
				Console.OUT.println("(" + i + ") -> " + sub(""+betweennessMap(i), 0n, numDigit));
			}
		}
	}
	
	// Constructor
	public def this(rmat:Rmat, permute:Int) {
		this(rmat, permute, NO_YIELD);
	}
	
	public def this(rmat:Rmat, permute:Int, yf:String) {
		graph = rmat.generate();
		graph.compress();
		N = graph.numVertices();
		M = graph.numEdges();
		verticesToWorkOn = new Rail[Int](N, (i:Long)=>i as Int);
		if (permute > 0) permuteVertices();
		betweennessMap = new Rail[Double](N);
		
		predecessorMap = new Rail[Int](graph.numEdges());
		predecessorCount = new Rail[Int](N);
		distanceMap = new Rail[Long](N, Long.MAX_VALUE);
		sigmaMap = new Rail[Long](N);
		regularQueue = new FixedRailQueue[Int](N);
		deltaMap = new Rail[Double](N);
		if(!yf.equals(NO_YIELD)){
			this.toYield = true;
			val yfs:Rail[String] = yf.split(":");
			for(var idx:Long = 0L; idx < yfs.size; idx++){
				this.yFs(idx) = Long.parseLong(yfs(idx));
			}
		}
	}
	
	
	
	
	
	/**
	 * Helper function that processes a set of vertices --- i.e, calculates 
	 * the single souce shortest path for all destinations and updates the 
	 * betweenness for all the vertices based on this calculation.
	 */
	@Inline public def bfsShortestPath(vertexIndex:Int, yield:()=>void) {
		
		
		//var processingTime:Long = 0;
		var resetTime:Long = 0;
		var yieldIdx:Long = 0; // added on Jan 6, 2014
		yieldTime = 0;
		// Iterate over each of the vertices in my portion.
		
		val s:Int = verticesToWorkOn(vertexIndex);
		
		val refTime:Long = System.nanoTime();
		
		// Put the values for source vertex
		distanceMap(s) = 0L;
		sigmaMap(s) = 1L;
		regularQueue.push(s);
		yieldIdx = 0L;
		// Loop until there are no elements left in the priority queue
		while(!regularQueue.isEmpty()) {
			if(this.toYield){
				if(yieldIdx++ == yFs(0)){
					yieldIdx=0L;
					var beforeYieldTime:Long = System.nanoTime(); 
					yield();
					yieldTime += (System.nanoTime() - beforeYieldTime);
					
				} // added on Jan 6, 2014
			}
			count++;
			// Pop the node with the least distance
			val v = regularQueue.pop();
			
			// Get the start and the end points for the edge list for "v"
			val edgeStart:Int = graph.begin(v);
			val edgeEnd:Int = graph.end(v);
			
			// Iterate over all its neighbors
			for(var wIndex:Int=edgeStart; wIndex<edgeEnd; ++wIndex) {
				// Get the target of the current edge.
				val w:Int = graph.getAdjacentVertexFromIndex(wIndex);
				val distanceThroughV:Long = distanceMap(v) + 1L;
				
				// In BFS, the minimum distance will only be found once --- the 
				// first time that a node is discovered. So, add it to the queue.
				if(distanceMap(w) == Long.MAX_VALUE) {
					regularQueue.push(w);
					distanceMap(w) = distanceThroughV;
				}
				
				// If the distance through "v" for "w" from "s" was the same as its 
				// current distance, we found another shortest path. So, add 
				// "v" to predecessorMap of "w" and update other maps.
				if(distanceThroughV == distanceMap(w)) {
					sigmaMap(w) = sigmaMap(w) + sigmaMap(v);// XTENLANG-2027
					predecessorMap(graph.rev(w)+predecessorCount(w)++) = v;
				}
			}
		} // while priorityQueue not empty
		
		regularQueue.rewind();
		//yield(); // added on 12/13/2013
		// Return vertices in order of non-increasing distances from "s"
		yieldIdx=0L;
		while(!regularQueue.isEmpty()) {
			if(this.toYield){
				if(yieldIdx++ == this.yFs(1)){
					yieldIdx=0L;
					var beforeYieldTime:Long = System.nanoTime(); 
					yield();
					yieldTime += (System.nanoTime() - beforeYieldTime);
					
				} // added on Jan 6, 2014
			}
			
			val w = regularQueue.top();
			val rev = graph.rev(w);
			while(predecessorCount(w) > 0) {
				val v = predecessorMap(rev+--predecessorCount(w));
				deltaMap(v) += (sigmaMap(v) as Double/sigmaMap(w) as Double)*(1.0 + deltaMap(w));
			}
			
			// Accumulate updates locally 
			if(w != s) betweennessMap(w) += deltaMap(w); 
			distanceMap(w) = Long.MAX_VALUE;
			sigmaMap(w) = 0L;
			deltaMap(w) = 0.0;
			
		} // vertexStack not empty
		
		
		accTime += (System.nanoTime()-refTime - yieldTime)/1e9;
		accYTime += yieldTime;
		
	}
	
	
	
	
	
	/**
	 * Reads in all the options and calculate betweenness.
	 */
	public static def main(args:Rail[String]):void {
		val cmdLineParams = new OptionsParser(args, new Rail[Option](0L), [
		                                                                   Option("s", "", "Seed for the random number"),
		                                                                   Option("n", "", "Number of vertices = 2^n"),
		                                                                   Option("a", "", "Probability a"),
		                                                                   Option("b", "", "Probability b"),
		                                                                   Option("c", "", "Probability c"),
		                                                                   Option("d", "", "Probability d"),
		                                                                   Option("p", "", "Permutation"),
		                                                                   Option("v", "", "Verbose")]);
		
		val seed:Long = cmdLineParams("-s", 2);
		val n:Int = cmdLineParams("-n", 2n);
		val a:Double = cmdLineParams("-a", 0.55);
		val b:Double = cmdLineParams("-b", 0.1);
		val c:Double = cmdLineParams("-c", 0.1);
		val d:Double = cmdLineParams("-d", 0.25);
		val permute:Int = cmdLineParams("-p", 1n); // on by default
		val verbose:Int = cmdLineParams("-v", 0n); // off by default
		
		Console.OUT.println("Running BC with the following parameters:");
		Console.OUT.println("seed = " + seed);
		Console.OUT.println("N = " + (1<<n));
		Console.OUT.println("a = " + a);
		Console.OUT.println("b = " + b);
		Console.OUT.println("c = " + c);
		Console.OUT.println("d = " + d);
		Console.OUT.println("Seq ");
		
		var time:Long = System.nanoTime();
		val bc = new BC(Rmat(seed, n, a, b, c, d), permute);
		val setupTime = (System.nanoTime()-time)/1e9;
		
		time = System.nanoTime();
		for(var i:Int=0n; i<bc.N; ++i) { 
			bc.bfsShortestPath(i, ()=>{;});
		}
		//bc.bfsShortestPaths(0n,bc.N, ()=>{;});
		val procTime = (System.nanoTime()-time)/1e9;
		
		if(verbose > 0) {
			Console.OUT.println("[" + here.id + "]"
					+ " Time = " + bc.accTime
					+ " Count = " + bc.count);
		}
		
		if(verbose > 2) bc.printBetweennessMap(6n);
		
		Console.OUT.println("Seq N: " + bc.N + "  Setup: " + setupTime + "s  Processing: " + procTime + "s");
	}
}

import x10.util.Random;
import x10.util.HashMap;
import x10.util.ArrayList;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.lang.Math;
import x10.io.File;
import x10.io.Printer;
import x10.lang.Cell;
import x10.util.concurrent.AtomicLong;
import x10.util.Team;
import x10.lang.Lock;

public final class Brandes(N:Int) {
  static type AtomicType=LockedDouble;
  static val Meg = (1000*1000); // not Whitman, she is history

  val graph:AbstractCSRGraph;
  val debug:Int;
  val verticesToWorkOn=Rail.make[Int] (N, (i:Int)=>i);
  val betweennessMap=Rail.make[Double] (N, (Int)=> 0.0D);
  val betweennessMapLocks = Rail.make[Lock](N, (Int)=> new Lock());
  
  // Constructor
  public def this(g:AbstractCSRGraph, debug:Int) {
    property(g.numVertices());
    this.graph=g;
    this.debug=debug;
  }

  private def graph()=graph;

  // A comparator which orders the vertices by their distances.
  private static val makeNonIncreasingComparator = 
    (distanceMap:Rail[Long]) =>  (x:Int, y:Int) => {
      val dx = distanceMap(x);
      val dy = distanceMap(y);
      return (dx==dy) ? 0 : (dx<dy) ? +1 : -1;
  };

  /**
  * Helper function that processes one single vertex --- i.e, calculates 
  * the single souce shortest path for all destinations and updates the 
  * betweenness for all the vertices based on this calculation.
  * 
  * <p>Note that the vertex numbers are [startVertex, endVertex], not 
  * [startVertex, endVertex) as we are used to!
  */
  public def bfsShortestPaths (val startVertex:Int,
                               val endVertex:Int,
                               debug:Int) { 
    var allocTime:Long= System.nanoTime();
    // Per-thread structure --- initialize once.
    val myBetweennessMap = Rail.make[Double] (N, 0.0 as Double);

    // These are the per-vertex data structures.
    val vertexStack = new FixedRailStack[Int] (N);
    val predecessorMap= Rail.make[FixedRailStack[Int]](N, (i:Int)=> 
          new FixedRailStack[Int](graph.getInDegree(i)));
    val distanceMap = Rail.make[Long](N, Long.MAX_VALUE);
    val sigmaMap = Rail.make(N, 0 as Long);
    val regularQueue = new FixedRailQueue[Int] (N);
    val deltaMap = Rail.make[Double](N, 0.0 as Double);
    val processedVerticesStack = new FixedRailStack[Int](N);

    allocTime = (System.nanoTime() - allocTime);
    
    var processingTime:Long = 0;
    var resetTime:Long = 0;
    // Iterate over each of the vertices in my portion.
    for ([vertexIndex] in startVertex..endVertex) { 
      val s:Int = this.verticesToWorkOn(vertexIndex);
      
      // Reset the values of those vertices that were touched in the previous
      // iteration. This might save some computation.

      // 1. Clear the vertexStack and the priorityQueue --- O(1) operation.
      val resetCounter:Long = System.nanoTime();
      vertexStack.clear();
      regularQueue.clear();

      // 2. Pop off the processedVerticesStack and reset their values.
      while (!(processedVerticesStack.isEmpty())) {
        val processedVertex = processedVerticesStack.pop();
        predecessorMap(processedVertex).clear();
        distanceMap(processedVertex) = Long.MAX_VALUE;
        sigmaMap(processedVertex) = 0 as Long;
        deltaMap(processedVertex) = 0.0 as Double;
      }

      resetTime += (System.nanoTime() - resetCounter);
      
      val processingCounter:Long = System.nanoTime();
      // Put the values for source vertex
      distanceMap(s)=0 as Long;
      sigmaMap(s)=1 as Long;
      regularQueue.push (s);
     
     
      // Loop until there are no elements left in the priority queue
      while (!regularQueue.isEmpty()) {
        // Pop the node with the least distance
        val v = regularQueue.pop();
        vertexStack.push (v);
        processedVerticesStack.push (v);

        // Get the start and the end points for the edge list for "v"
        val edgeStart:Int = graph.begin(v);
        val edgeEnd:Int = graph.end(v);
        
        // Iterate over all its neighbors
        for (var wIndex:Int=edgeStart; wIndex<edgeEnd; ++wIndex) {
          // Get the target of the current edge and its weight.
          val w:Int = graph.getAdjacentVertexFromIndex(wIndex);
          val distanceThroughV:Long = distanceMap(v) + 1 as Long;

          // In BFS, the minimum distance will only be found once --- the 
          // first time that a node is discovered. So, add it to the queue.
          if (distanceMap(w)==Long.MAX_VALUE) {
            regularQueue.push (w);
            distanceMap(w) = distanceThroughV;
          }

          // If the distance through "v" for "w" from "s" was the same as its 
          // current distance, we found another shortest path. So, add 
          // "v" to predecessorMap of "w" and update other maps.
          if (distanceThroughV == distanceMap(w)) {
            sigmaMap(w) = sigmaMap(w) + sigmaMap(v);// XTENLANG-2027
            predecessorMap(w).push(v);
          }
        }
      } // while priorityQueue not empty
      
      // Return vertices in order of non-increasing distances from "s"
      while (!vertexStack.isEmpty()) {
        val w = vertexStack.pop ();
        while (!(predecessorMap(w).isEmpty())) {
          val v = predecessorMap(w).pop();
          deltaMap(v) += (sigmaMap(v) as Double/sigmaMap(w) as Double)*
          (1 + deltaMap(w));
        }
     
        // Accumulate updates locally 
        if (w != s) myBetweennessMap(w) += deltaMap(w); 
       
      } // vertexStack not empty
      processingTime  += (System.nanoTime() - processingCounter);
    } // All vertices from (startVertex, endVertex)

    // update shared state at the place once, atomically.
    var localMergeTime:Long = System.nanoTime();
    for (var i:Int=0; i < N; i++) {
      val result = myBetweennessMap(i);
      if (result != 0.0D) {
        val lock = betweennessMapLocks(i);
        lock.lock();
        betweennessMap(i) += result;
        lock.unlock();
      }
    } 
    localMergeTime = (System.nanoTime() - localMergeTime);
    
    if (debug > 0) {
      Console.OUT.println ("[" + here.id + ":" + Runtime.workerId() + "] "
          + " Alloc= " + allocTime/1e9
          + " Reset= " + resetTime/1e9
          + " Proc= " + processingTime/1e9
          + " Merge= " + localMergeTime/1e9
      );
    }
  }

  /**
  * Helper function that processes one single vertex --- i.e, calculates 
  * the single souce shortest path for all destinations and updates the 
  * betweenness for all the vertices based on this calculation.
  * 
  * <p>Note that the vertex numbers are [startVertex, endVertex], not 
  * [startVertex, endVertex) as we are used to!
  */
  public def dijkstraShortestPaths (val startVertex:Int,
                                    val endVertex:Int,
                                    debug:Int) { 
    var allocTime:Long= System.nanoTime();
    // Per-thread structure --- initialize once.
    val myBetweennessMap = Rail.make[Double] (N, 0.0 as Double);

    // These are the per-vertex data structures.
    val vertexStack = new FixedRailStack[Int] (N);
    val predecessorMap= Rail.make[FixedRailStack[Int]](N, (i:Int)=> 
          new FixedRailStack[Int](graph.getInDegree(i)));
    val distanceMap = Rail.make[Long](N, Long.MAX_VALUE);
    val sigmaMap = Rail.make(N, 0 as Long);
    val binaryHeapComparator = makeNonIncreasingComparator(distanceMap);
    val priorityQueue = new FixedBinaryHeap (binaryHeapComparator, N);
    val deltaMap = Rail.make[Double](N, 0.0 as Double);
    val processedVerticesStack = new FixedRailStack[Int](N);

    allocTime = (System.nanoTime() - allocTime);
    
    var processingTime:Long = 0;
    var resetTime:Long = 0;
    // Iterate over each of the vertices in my portion.
    for ([vertexIndex] in startVertex..endVertex) { 
      val s:Int = this.verticesToWorkOn(vertexIndex);
      
      // Reset the values of those vertices that were touched in the previous
      // iteration. This might save some computation.

      // 1. Clear the vertexStack and the priorityQueue --- O(1) operation.
      val resetCounter:Long = System.nanoTime();
      vertexStack.clear();
      priorityQueue.clear();

      // 2. Pop off the processedVerticesStack and reset their values.
      while (!(processedVerticesStack.isEmpty())) {
        val processedVertex = processedVerticesStack.pop();
        predecessorMap(processedVertex).clear();
        distanceMap(processedVertex) = Long.MAX_VALUE;
        sigmaMap(processedVertex) = 0 as Long;
        deltaMap(processedVertex) = 0.0 as Double;
      }

      resetTime += (System.nanoTime() - resetCounter);
      
      val processingCounter:Long = System.nanoTime();
      // Put the values for source vertex
      distanceMap(s)=0 as Long;
      sigmaMap(s)=1 as Long;
      priorityQueue.push (s);
     
     
      // Loop until there are no elements left in the priority queue
      while (!priorityQueue.isEmpty()) {
        // Pop the node with the least distance
        val v = priorityQueue.pop();
        vertexStack.push (v);
        processedVerticesStack.push (v);

        // Get the start and the end points for the edge list for "v"
        val edgeStart:Int = graph.begin(v);
        val edgeEnd:Int = graph.end(v);
        
        // Iterate over all its neighbors
        for (var wIndex:Int=edgeStart; wIndex<edgeEnd; ++wIndex) {
          // Get the target of the current edge and its weight.
          val w:Int = graph.getAdjacentVertexFromIndex(wIndex);
          val edgeWeight:Long = 
      (graph as AbstractWeightedCSRGraph).getEdgeWeightFromIndex(wIndex);
          val distanceThroughV = distanceMap(v) + edgeWeight;

          // Update the distance and push it in the priorityQueue
          // if a new low distance has been found.
          if (distanceThroughV < distanceMap(w)) {
            // Check if this is the first time "w" was found.
            val firstTimeRelaxation = 
              (distanceMap(w)==Long.MAX_VALUE)? true:false;

            // Update the distance map
            distanceMap(w) = distanceThroughV;

            // If this is the first time for "w", add it to the queue.
            if (firstTimeRelaxation) {
              priorityQueue.push (w); 
            } else { // Decrease the key and remove the previous predecessors
              priorityQueue.decreaseKey (w);
              while (!predecessorMap(w).isEmpty()) predecessorMap(w).pop();
            }
          }

          // If v relaxed the distance to w, we can update the sigmaMap and add
          // v to the predecessorMap of w.
          if (distanceThroughV == distanceMap(w)) {
            sigmaMap(w) = sigmaMap(w) + sigmaMap(v);// XTENLANG-2027
            predecessorMap(w).push(v);
          }
        }
      } // while priorityQueue not empty
      
      // Return vertices in order of non-increasing distances from "s"
      while (!vertexStack.isEmpty()) {
        val w = vertexStack.pop ();
        while (!(predecessorMap(w).isEmpty())) {
          val v = predecessorMap(w).pop();
          deltaMap(v) += (sigmaMap(v) as Double/sigmaMap(w) as Double)*
          (1 + deltaMap(w));
        }
     
        // Accumulate updates locally 
        if (w != s) myBetweennessMap(w) += deltaMap(w); 
       
      } // vertexStack not empty
      processingTime  += (System.nanoTime() - processingCounter);
    } // All vertices from (startVertex, endVertex)

    // update shared state at the place once, atomically.
    var localMergeTime:Long = System.nanoTime();
    for (var i:Int=0; i < N; i++) {
      val result = myBetweennessMap(i);
      if (result != 0.0D) {
        val lock = betweennessMapLocks(i);
        lock.lock();
        betweennessMap(i) += result;
        lock.unlock();
      }
    } 
    localMergeTime = (System.nanoTime() - localMergeTime);
    
    if (debug > 0) {
      Console.OUT.println ("[" + here.id + ":" + Runtime.workerId() + "] "
          + " Alloc= " + allocTime/1e9
          + " Reset= " + resetTime/1e9
          + " Proc= " + processingTime/1e9
          + " Merge= " + localMergeTime/1e9
      );
    }
  }
  
  /**
   * A function to shuffle the vertices randomly to give better work dist.
   */
  private def permuteVertices () {
    val prng = new Random();
    val maxIndex = N-1;
    val unshuffledVertices:Rail[Int] = Rail.make[Int] (N, (i:Int)=>i);
    for ([i] in 0..maxIndex) {
      val indexToPick = prng.nextInt (maxIndex-i);
      this.verticesToWorkOn(i) = unshuffledVertices(i+indexToPick);
      unshuffledVertices(i+indexToPick) = unshuffledVertices(i);
    }
  }

  /**
   * Place local version of crunchNumbers.
   */
  private def crunchNumbersLocally (permute:Boolean,
                                    weighted:Boolean,
                                    chunk:Int,
                                    vertexBeginIndex:Int,
                                    vertexEndIndex:Int,
                                    debug:Int) {

    // Permutate the vertices if asked for
    if (permute) permuteVertices ();
   
    // Evaluate after splitting up the tasks based on the scheduling policy
    // A "-1" indicates that the user wants to split evenly across all threads.
    val myTotalWorkLoad = (vertexEndIndex-vertexBeginIndex+1);
    val numChunks = (-1==chunk) ? Runtime.INIT_THREADS: chunk;
    assert numChunks > 0;
    val chunkSize = myTotalWorkLoad/numChunks;
    
    finish  {
      // spawn work 
      for ([i] in 0..(numChunks-1)) async {
        val startVertex = vertexBeginIndex + chunkSize*i;
        val endVertex = (i==(numChunks-1)) ? vertexEndIndex: 
                                           (startVertex+chunkSize-1);

        // We can lift this condition out so that its not checked everytime,
        // but considering how much work is involved in each BFS/DFS 
        // calculation, I don't think this will be a major time saver.
        if (weighted==true) {
          dijkstraShortestPaths (startVertex, endVertex, debug);
        } else {
          bfsShortestPaths (startVertex, endVertex, debug);
        }
      }
    }
    
    // Merge the results in this place with the results in other places.
    val globalMergeTime:Long = -System.nanoTime();
    // Merge the results globally, no locking necessary
    Team.WORLD.allreduce (here.id, // My ID.
        betweennessMap, // Source buffer.
        0, // Offset into the source buffer.
        betweennessMap, // Destination buffer.
        0, // Offset into the destination buffer.
        this.N, // Number of elements.
        Team.ADD); // Operation to be performed.
    if (debug > 0) {
      Console.OUT.println ("[" + here.id +  "] " 
          + " Global merge time= " + ((globalMergeTime+System.nanoTime())/1e9));
    }
  }
  
  /**
   * Dump the betweenness map.
   */
  private def printBetweennessMap (printer:Printer) {
    for ([i] in 0..(graph.numVertices()-1)) {
      if (betweennessMap(i) != 0.0 as Double) 
        printer.println ("(" + i + ") ->" + betweennessMap(i));
    }
  }

  /**
   * Calls betweeness, prints out the statistics and what not.
   */
  private static def crunchNumbers (graphMaker:()=>AbstractCSRGraph,
                                    printer:Printer, 
                                    permute:Boolean,
                                    weighted:Boolean,
                                    chunk:Int,
                                    debug:Int) {
    var time:Long = System.nanoTime();

    // Create a place local handle at each place.
    val brandesHandles = PlaceLocalHandle.make[Brandes] 
                 (Dist.makeUnique(), ()=> {
                   val graph = graphMaker();
                   graph.compressGraph();
                   new Brandes (graph, debug)
                 });
    val distTime = (System.nanoTime()-time)/1e9;
    time = System.nanoTime();
    
    val myGraph = brandesHandles().graph();
    val N= myGraph.numVertices();
    val M= myGraph.numEdges();
    printer.println ("Graph details: N=" + N + ", M=" + M);
    if (printer != Console.OUT) {
      Console.OUT.println ("Graph details: N=" + N + ", M=" + M);
    }
    
    // Determine the number of places.
    val numPlaces = Place.MAX_PLACES;

    // Get a rail of partitions
    val chunkSize = N/numPlaces;
    
    // Loop over all the places and crunch the numbers.
    finish {
      for ([place] in 0..(numPlaces-1)) 
        async at(Place(place)) 
          brandesHandles().crunchNumbersLocally (
                       permute, 
                       weighted,
                       chunk, 
                       place*chunkSize, 
                       (place==(numPlaces-1))? N-1: ((place+1)*chunkSize)-1, 
                       debug);
    } // finish
    
    time = System.nanoTime() - time;
    val procTime = time/1E9;
    val totalTime = distTime + procTime;
    val procPct = procTime*100.0/totalTime;
    printer.println ("Betweenness calculation took time=" + totalTime 
        + " s (proc: " + procPct  +  "%).");
    
    if (debug>1) {
      brandesHandles().printBetweennessMap(printer);
    }
  }
  
  /**
   * The big cahuna --- read in all the options and calculate betweenness.
   */
  public static def main (args:Array[String](1)):void {
    try {
      val cmdLineParams = new OptionsParser 
      (args, null,
          [Option("s", "", "Seed for the random number"),
           Option("n", "", "Number of vertices = 2^n"),
           Option("a", "", "Probability a"),
           Option("b", "", "Probability b"),
           Option("c", "", "Probability c"),
           Option("d", "", "Probability d"),
           Option("f", "", "Graph file name"),
           Option("t", "", "File type: 0: NET, 1:NWB, 2:GUOJING, 3:CSR"),
           Option("i", "", "Starting index of vertices"),
           Option("debug", "", "Debug"),
           Option("chunk", "", "Chunk size, defaults to 100"),
           Option("permute", "", "true, false"),
           Option("weighted", "", "true, false"),
           Option("dump", "", "true, false"),
           Option("o", "", "Output file name")]);
      
      val seed:Long = cmdLineParams ("-s", 2);
      val n:Int = cmdLineParams ("-n", 2);
      val a:Double = cmdLineParams ("-a", 0.55);
      val b:Double = cmdLineParams ("-b", 0.1);
      val c:Double = cmdLineParams ("-c", 0.1);
      val d:Double = cmdLineParams ("-d", 0.25);
      val fileName:String = cmdLineParams ("-f", "NOFILE");
      val fileType:Int = cmdLineParams ("-t", 0);
      val startIndex:Int = cmdLineParams ("-i", 0);
      val outFileName:String = cmdLineParams ("-o", "STDOUT");
      val debug:Int = cmdLineParams ("-debug", 0); // off by default
      val permute:Boolean = 1==cmdLineParams ("-permute", 0); // def: off
      val weighted:Boolean = 1==cmdLineParams ("-weighted", 0); //def: off
      val dumpBinary:Boolean = 1==cmdLineParams ("-dump", 0); //def: off
      val chunk:Int = cmdLineParams ("-chunk", -1); 

      val numPlaces = Place.MAX_PLACES;
      
      /* Set the printer appropriate to where the user wants output */
      val printer = (0 == outFileName.compareTo("STDOUT")) ? 
          Console.OUT : (new File(outFileName)).printer();
      
      printer.println ("Running SSCA2 with the following parameters:");
      val gm:()=>AbstractCSRGraph;
      if (0 == fileName.compareTo("NOFILE")) {
        printer.println ("seed = " + seed);
        printer.println ("N = " + Math.pow(2, n) as Int);
        printer.println ("a = " + a);
        printer.println ("b = " + b);
        printer.println ("c = " + c);
        printer.println ("d = " + d);
        gm = ()=>Rmat(seed, n,a,b,c,d).generate(weighted);
      } else {
        printer.println ("f = " + fileName);
        printer.println ("t = " + fileType);
        printer.println ("i = " + startIndex);

        if (0==fileType) {
          gm=()=>NetReader.readNetFile (fileName, startIndex, weighted);
        } else if (1==fileType) {
          gm=()=>NetReader.readNwbFile (fileName, startIndex, weighted);
        } else if (2==fileType) {
          gm=()=>NetReader.readGuojingFile (fileName, startIndex, weighted);
        } else if (3==fileType) {
          if (weighted==true) {
            printer.println ("Currently, the CSR format is supported only" +
                             " for unweighted graphs!");
            return;
          } else {
            gm=()=>new UnweightedGraph(fileName);
          }
        } else {
          printer.println ("The only file types supported are:");
          printer.println (".NET format --> -t 0 (default)");
          printer.println (".NWB format --> -t 1");
          printer.println ("GUOJING binary format --> -t 2");
          return;
        }
      }

      // If a binary dump has been requested, then dump the file out.
      if (dumpBinary) {
        val graph = gm(); 
        graph.compressGraph ();
        graph.writeBinaryToFile(outFileName);
        return;
      }
      
      printer.println ("Permuting: " + permute);
      printer.println ("Chunk size: " + chunk);
      printer.println ("" + Place.MAX_PLACES + " place" 
    		  + (Place.MAX_PLACES > 1 ? "s" : "")
    		  + " and " + 
          Runtime.INIT_THREADS + " worker" 
          + (Runtime.INIT_THREADS > 1 ? "s" : "") + "/place");
      
      crunchNumbers (gm, printer, permute, weighted, chunk, debug);
      
      
    } catch (e:Throwable) {
      e.printStackTrace(Console.ERR);
    }
  }
}

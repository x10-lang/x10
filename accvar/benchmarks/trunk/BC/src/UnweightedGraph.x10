import x10.util.ArrayList;

/**
 * A class that implements the unweighted graph interface.
 */
public final class UnweightedGraph extends AbstractUnweightedCSRGraph {
  private val adjacencyList:Rail[ArrayList[Int]];

  /**
   * Constructor: prepare all the data structures.
   */
  public def this (N:Int) { 
    super(N);
    this.adjacencyList = 
     Rail.make[ArrayList[Int]] (N, (Int)=> new ArrayList[Int]());
  }


  /** Constructor: construct from the CSR format file */
  public def this(csrFileName:String) {
    super(csrFileName);
    this.adjacencyList = Rail.make[ArrayList[Int]] (0);
  }

  /**
   * @Override
   * Create the compressed representation. To be called only after all the 
   * edges have been added to the vertex list. All we are doing here is 
   * iterating over all the edges over all the vertices and populating the 
   * offsetMap and the adjacencyMap.
   */
  public def compressGraph () { 
    // The graph may be compressed already --- don't do anything in this case.
    if (this.compressed) return;

    // Create as many elements as edges.
    this.adjacencyMap = Rail.make[Int](this.M);

    // Start copying over from the first vertex onwards.
    var currentOffset:Int = 0;
    for ([v] in 0..(this.N-1)) {
      // Put in the starting offset for this vertex.
      this.offsetMap(v) = currentOffset;
      
      // Iterate over all the edges.
      for (w in this.adjacencyList(v)) {
        this.adjacencyMap(currentOffset) = w;
        ++currentOffset;
      }
    }

    // assert that we have included every edge.
    assert (currentOffset==this.M);

    // set the offset of the sentinel
    this.offsetMap(N) = currentOffset;

    // set compressed to be true
    this.compressed = true;

    // de-allocate anything that we don't need
    for ([i] in 0..(this.adjacencyList.length-1)) {
      Runtime.deallocObject(this.adjacencyList(i));
    }
    Runtime.deallocObject (this.adjacencyList);
  }

  /**
   * @Override
   * Check if an edge exists between a pair of vertices.
   */
  public def existsEdge (v:Int, w:Int) {
    assert (this.compressed==false);
    return this.adjacencyList(v).contains(w); 
  }

  /**
   * @Override
   * Add an edge with a given weight. We do not check if the edge exists!
   * If an edge exists, its overwritten.
   */
  public def addEdge (v:Int, w:Int): void {
    assert (this.compressed==false);
    this.adjacencyList(v).add (w);
  }

  /**
   * @Override
   * Print out the graph as a string
   */
  public def toString (): String {
    var outString:String = "";

    for ([v] in 0..(N-1)) {
      val neighbors = this.adjacencyList (v);

      for (w in neighbors) {
        outString += "(" + v + ", " + w + ")" + "\n";
      }
    }
    return outString;
  }
}

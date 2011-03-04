import x10.io.File;
import x10.io.Writer;
import x10.io.Reader;

/**
 * An abstract class  that provides all the functionality of a graph. This is
 * further specialized to provide either weighted or unweighted graphs.
 */
public abstract class AbstractCSRGraph {
  protected val N:Int; // number of vertices
  protected var M:Int; // number of edges
  protected val inDegreeMap:Rail[Int]; // in-degree for each vertex
  protected var compressed:Boolean; // indicates compression

  /** 
   * Two gigantic rails that we are using to hold all the edges --- we are 
   * moving to this representation to save some object creation overhead 
   */
  /** The edgelist of a vertex is stored as offsetMap[i] to offsetmap[i+1] */
  protected val offsetMap:Rail[Int];

  /** This just contains a series of edges that are indexed by offsetMap */
  protected var adjacencyMap:Rail[Int];

  /**
   * Constructor
   * Read a graph in from file that has been previously written out in 
   * the CSR format.
   * 
   * The graph is written out as follows in Binary:
   * [N][M][offsetMap][AdjacencyMap]
   */
  public def this (fileName:String) {
    val inputFile:File = new File(fileName);
    val inputFileReader:Reader = inputFile.openRead();

    // First, write out M and N.
    this.N = inputFileReader.readInt ();
    this.M = inputFileReader.readInt ();

    // Second, create the datastructures that we need.
    this.inDegreeMap = Rail.make[Int] 
                        (this.N, (i:Int) => inputFileReader.readInt());

    // Third, create the offset map --- which has (N+1) entries. BEWARE!
    this.offsetMap = Rail.make[Int] 
                        (this.N+1, (i:Int) => inputFileReader.readInt());

    // Finally, read the adjacencyMap, which has M entries.
    this.adjacencyMap = Rail.make[Int] 
                        (this.M, (i:Int) => inputFileReader.readInt());

    // Set compressed to true
    this.compressed = true;
  }

  /** Constructor */
  public def this(N:Int) {
    this.N = N;
    this.M = 0;
    this.inDegreeMap = Rail.make[Int] (this.N, (Int) => 0);
    this.offsetMap = Rail.make[Int] ((this.N)+1);
    this.compressed = false;
  }

  /** Compress the datastructures in the graph */
  abstract public def compressGraph (): void;

  /** Get the adjacent node from index */
  public final def getAdjacentVertexFromIndex(wIndex:Int) {
    assert (this.compressed);
    return this.adjacencyMap(wIndex);
  }

  /** 
   * Give the position of the begining of my neighbors in the compressed
   * notation of the graph. This means that the compression must represent
   * the graph in the CSR format.
   */
  public final def begin (v:Int) {
    assert (this.compressed);
    assert (v < this.N);

    return this.offsetMap(v);
  }

  /**
   * Similar to above, but gives the location of one past the current 
   * vertex's neighbor list end. The iteration space is therefore
   * [begin, end).
   */
  public final def end (v:Int) {
    assert (this.compressed);
    assert (v < this.N);

    return this.offsetMap(v+1);
  }

  /** Check if an edge between the two vertices already exists */
  abstract public def existsEdge (u:Int, v:Int): Boolean;

  /** Print out the graph in string format */
  abstract public def toString (): String;

  /** Return the number of vertices in the graph */
  public final def numVertices () = this.N;

  /** Return the number of edges in the graph */
  public final def numEdges () = this.M;

  /** Add to the inDegree of a vertex */
  public final def incrementInDegree (v:Int) {
    this.inDegreeMap(v)+=1;
    this.M+=1;
  }

  /** Get a vertex's inDegree */
  public final def getInDegree (v:Int) = this.inDegreeMap(v);

  /**
   * Write the graph out to file so that its easy to read the next time 
   * around --- i.e., we can directly read it into CSR format.
   * 
   * The graph is written out as follows in Binary:
   * [N][M][offsetMap][AdjacencyMap]
   */
  public def writeBinaryToFile(fileName:String) {
    assert (this.compressed);

    val outputFile:File = new File(fileName);
    val outputFileWriter:Writer = outputFile.openWrite();

    // First, write out M and N.
    outputFileWriter.writeInt (this.N);
    outputFileWriter.writeInt (this.M);

    // Second, write the in-degree map.
    for ([i] in 0..(this.N-1)) outputFileWriter.writeInt(this.inDegreeMap(i));

    // Third, write the offset map --- which has (N+1) entries. BEWARE!
    for ([i] in 0..(this.N)) outputFileWriter.writeInt(this.offsetMap(i));

    // Finally, write the adjacencyMap, which has M entries.
    for ([i] in 0..(this.M-1)) outputFileWriter.writeInt(this.adjacencyMap(i));
  }
}

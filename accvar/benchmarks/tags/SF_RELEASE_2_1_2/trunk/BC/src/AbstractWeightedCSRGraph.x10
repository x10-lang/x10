/**
 * An abstract class for weighted graphs. These do allow adding an edge 
 * with a weight or querying an edge for its weight.
 */
public abstract class AbstractWeightedCSRGraph extends AbstractCSRGraph {
  /** Constructor */
  public def this(N:Int) = super(N);

  /** Add an edge */
  abstract public def addEdge (u:Int, v:Int, d:Long): void;

  /** Return the edge weight */
  abstract public def getEdgeWeight (v:Int, w:Int): Long;

  /** Retrieve the edge weight from the compressed notation */
  abstract public def getEdgeWeightFromIndex(wIndex:Int): Long;
}

/**
 * An abstract class for unweighted graphs. These do not allow adding an edge 
 * with a weight or querying an edge for its weight.
 */
public abstract class AbstractUnweightedCSRGraph extends AbstractCSRGraph {
  /** Constructor */
  public def this(N:Int) = super(N);

  /** Constructor */
  public def this(csrFileName:String) = super(csrFileName);

  /** Add an edge */
  abstract public def addEdge (u:Int, v:Int): void;
}

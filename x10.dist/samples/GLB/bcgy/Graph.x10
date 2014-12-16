import x10.compiler.*;
import x10.util.ArrayList;

/**
 * An class that provides all the functionality of an unweighted graph.
 */
public final class Graph {
    private val N:Int; // number of vertices
    private var M:Int; // number of edges
    private val inDegreeMap:Rail[Int]; // in-degree for each vertex

    /** The edgelist of a vertex is stored as offsetMap[i] to offsetmap[i+1] */
    private val offsetMap:Rail[Int];

    private val reverseOffsetMap:Rail[Int];

    /** This just contains a series of edges that are indexed by offsetMap */
    private var adjacencyMap:Rail[Int];

    private var adjacencyList:Rail[ArrayList[Int]];

    /** Constructor */
    public def this(N:Int) {
        this.N = N;
        this.M = 0n;
        this.inDegreeMap = new Rail[Int](N);
        this.offsetMap = new Rail[Int](N+1n);
        this.reverseOffsetMap = new Rail[Int](N);
        this.adjacencyList = new Rail[ArrayList[Int]](N, (Long)=>new ArrayList[Int]());
        this.adjacencyMap = null;
    }

    /** Get the adjacent node from index */
    @Inline public def getAdjacentVertexFromIndex(wIndex:Int) {
        assert(adjacencyMap != null);
        return adjacencyMap(wIndex);
    }

    /** 
     * Give the position of the begining of my neighbors in the compressed
     * notation of the graph. This means that the compression must represent
     * the graph in the CSR format.
     */
    @Inline public def begin(v:Int) {
        assert(adjacencyMap != null);
        assert(v < N);
        return offsetMap(v);
    }

    /**
     * Similar to above, but gives the location of one past the current 
     * vertex's neighbor list end. The iteration space is therefore
     * [begin, end).
     */
    @Inline public def end(v:Int) {
        assert(adjacencyMap != null);
        assert(v < N);
        return offsetMap(v+1);
    }

    @Inline public def rev(v:Int) {
        assert(adjacencyMap != null);
        assert(v < N);
        return reverseOffsetMap(v);
    }

    /** Return the number of vertices in the graph */
    public def numVertices() = N;

    /** Return the number of edges in the graph */
    public def numEdges() = M;

    /** Get a vertex's inDegree */
    public def getInDegree(v:Int) = inDegreeMap(v);

    /**
     * Create the compressed representation. To be called only after all the 
     * edges have been added to the vertex list. All we are doing here is 
     * iterating over all the edges over all the vertices and populating the 
     * offsetMap and the adjacencyMap.
     */
    public def compress() { 
        // The graph may be compressed already --- don't do anything in this case.
        if(adjacencyMap != null) return;

        // Create as many elements as edges.
        adjacencyMap = new Rail[Int](M);

        // Start copying over from the first vertex onwards.
        var currentOffset:Int = 0n;
        for(var v:Int=0n; v<N; ++v) {
            // Put in the starting offset for this vertex.
            offsetMap(v) = currentOffset;

            // Iterate over all the edges.
            val list = adjacencyList(v);
            for(var i:Int=0n; i<list.size(); ++i) {
                adjacencyMap(currentOffset++) = list(i);
            }
        }

        var offset:Int = 0n;
        for(var v:Int=0n; v<N; ++v) {
            reverseOffsetMap(v) = offset;
            offset += inDegreeMap(v);
        }

        // assert that we have included every edge.
        assert(currentOffset == M);
        assert(offset == M);

        // set the offset of the sentinel
        offsetMap(N) = currentOffset;

        adjacencyList = null;
    }

    /**
     * Add an edge. We do not check if the edge exists!
     * If an edge exists, its overwritten.
     */
    public def addEdge(v:Int, w:Int): void {
        assert(adjacencyMap == null);
        adjacencyList(v).add(w);
        inDegreeMap(w) += 1n;
        M += 1;
    }

    /**
     * Print out the graph as a string
     */
    public def toString():String {
        var outString:String = "";

        for(var v:Int=0n; v<N; ++v) {
            for(w in adjacencyList(v)) {
                outString += "(" + v + ", " + w + ")" + "\n";
            }
        }
        return outString;
    }
}

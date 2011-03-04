import x10.util.Random;
import x10.util.HashMap;
import x10.util.ArrayList;
import x10.lang.Math;

/**
 * A class that emulates the recursive-matrix graph from the paper:
 * "R-MAT: A Recursive Model for Graph Mining" by Chakrabarti et al..
 * 
 * This code is based on the MATLAB sample code that was given along 
 * with the SSCA2 benchmarks. For working MATLAB code, please refer to
 * "rmat.m" file in the current directory.
 * 
 * The R-MAT generator takes in 6 parameters as input (via the constructor).
 * The values passed to these arguments ultimately decide the shape and the 
 * properties of the graph. For a more detailed description of the parameters
 * and their influence, please refer to Chakrabarti et al.
 */
public final struct Rmat {
	private val seed:Long; // seed to the random number generator
	private val n:Int; // the log of the number of verties. I.e., N = 2^n
	private val N:Int; // the number of vertices.
	private val a:Double; // The next 4 parameters determine the shape of 
	private val b:Double; // the graph. A detailed description of the 
	private val c:Double; // parameters is out of scope here. Briefly,
	private val d:Double; // (a+b+c+d == 1) and typically a>=b, a>= c, a>=d.
	
	public def this (seed:Long,
			n:Int,
			a:Double,
			b:Double,
			c:Double,
			d:Double) {
		this.seed = seed;
		this.n = n;
		this.N = Math.pow (2, n) as Int;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	/**
	 * A function that mimics the MATLAB function "zeros".
	 */
	private final def zeros (numElements:Int):Rail[Int] =
		Rail.make[Int] (numElements, (Int)=>0);
	
	/**
	 *  A function that mimics the MATLAB function rand (M,1). It generates a 
	 *  vector of M random numbers. Here, numElements is M!
	 */
	private final def rand (rng:Random, numElements:Int) =
		Rail.make[Double] (numElements, (Int)=>rng.nextDouble());
	
	/**
	 * A function that mimics the use of > operator in MATLAB when the LHS is 
	 * a vector and the RHS either a scalar value or a vector. Basically, the 
	 * result of "V > a", where "V" is a vector and "a" is a scalar is an 
	 * integer-valued vector where there is a 1 if "V[i] > a" and 0 otherwise.
	 */
	private final def greaterThan (lhs:Rail[Double], rhs:Double) =
		Rail.make[Int] (lhs.length(), (i:Int)=>(lhs(i) > rhs) ? 1 : 0);
	
	/**
	 * The same function as above, only with a element-wise comparison in the RHS
	 * instead of a comparison with a scalar value. So, there is a 1 in the 
	 * resultant vector if "LHS[i] > RHS[i]", and 0 otherwise.
	 */
	private final def greaterThan (lhs:Rail[Double], rhs:Rail[Double]) =
		Rail.make[Int] (lhs.length(), 
				(i:Int)=>(lhs(i) > rhs(i)) ? 1 : 0);
	
	/**
	 * Multiple a vector with a scalar. There is, however, one catch. When the 
	 * flip bit is turned on, the vector (LHS) is manipulated such that its either 
	 * always 0 or 1. If V[i] > 0 and flip==true, then LHS(i) == 0, and 1 otherwise.
	 * In other words, we flip V[i].
	 */
	private final def multiply (lhs:Rail[Int], 
			multiplier:Double, 
			flip:Boolean) =
				Rail.make[Double] 
				          (lhs.length(), (i:Int)=> { 
				        	  val lhsMultiplier = flip ? ((lhs(i) > 0) ? 0 : 1) : lhs(i);
				        	  multiplier*(lhsMultiplier as Double)});
	
	/**
	 * A straightforward vector-scalar multiplication
	 */
	private final def multiply (lhs:Rail[Int], 
			multiplier:Int) =
				Rail.make[Int] (lhs.length(), (i:Int)=>lhs(i)*multiplier);
	
	/**
	 * A straightforward addition of two vectors.
	 */
	private final def add (lhs:Rail[Double], rhs:Rail[Double]) = 
		Rail.make[Double] (lhs.length(), (i:Int)=>lhs(i)+rhs(i));
	
	/**
	 * Same as above, but with a different type
	 */
	private final def add (lhs:Rail[Int], rhs:Rail[Int]) =
				Rail.make[Int] (lhs.length(), (i:Int)=>lhs(i)+rhs(i));
	
	
	/**
	 * This function mimics the behavior of the MATLAB function sparse(i,j,s).
	 * Basically, we are given two vectors. The LHS vector stands for the row 
	 * indices, and is hence called "row". Similarly, the RHS vector contains 
	 * the column indices. By default, the edge weight of an edge is considered
	 * to be 1. However, if the (row,col) values repeat, you add 1 to the edge 
	 * weight --- this is a way to deal with duplicate edges.
	 */
	private final def sparse (row:Rail[Int], 
                            col:Rail[Int], 
                            weighted:Boolean): AbstractCSRGraph {
    /* Declare the adjacency graph */
    val adjacencyGraph:AbstractCSRGraph = weighted? 
                      new WeightedGraph(this.N): new UnweightedGraph(this.N);
		val numElements = row.length(); 
		
		for (var i:Int=0; i<numElements; ++i) {
			val v = row(i);
			val w = col(i);
			
      if (adjacencyGraph instanceof AbstractWeightedCSRGraph) {
				// Add an edge from (v,w). If the edge exists, increase its weight.
				// Note that Int.MIN_VALUE being returned from getEdgeWeight() 
				// implies that there was no edge existing between (v,w) apriori.

        val d = 
        (adjacencyGraph as AbstractWeightedCSRGraph).getEdgeWeight (v,w);
				if (Long.MAX_VALUE == d) {
        (adjacencyGraph as AbstractWeightedCSRGraph).addEdge 
                                (v, w, 1 as Long);
          adjacencyGraph.incrementInDegree (w);
        } else {
        (adjacencyGraph as AbstractWeightedCSRGraph).addEdge 
                                (v, w, d+1 as Long);
        }
      } else if(adjacencyGraph instanceof AbstractUnweightedCSRGraph){
        // Simply add the edge. This is an idempotent operation.
        (adjacencyGraph as AbstractUnweightedCSRGraph).addEdge (v,w);
        adjacencyGraph.incrementInDegree (w);
      }
		}
		
		return adjacencyGraph;
	}
	
	/**
	 * TODO: Explain the code --- too late in the night now. Sleepy!
	 */
	public def generate (weighted:Boolean) {
		// Initialize M, and rng
		val M = 8*this.N;
		val rng = new Random(this.seed);
		
		// Create index arrays
		var ii:Rail[Int] = this.zeros(M);
		var jj:Rail[Int] = this.zeros(M);
		
		// Loop over each order of bit
		val ab = a+b;
		val cNorm = c/(c+d);
		val aNorm = a/(a+b);
		
		for (var ib:Int=0; ib<this.n; ++ib) {
			val iiBit = this.greaterThan (this.rand (rng, M), ab);
			val jjBitComparator = this.add (this.multiply (iiBit, cNorm, false),
					this.multiply (iiBit, aNorm, true));
			val jjBit = this.greaterThan (this.rand (rng, M), jjBitComparator);
			val exponent = Math.pow (2, ib) as Int;
			ii = this.add (ii, this.multiply (iiBit, exponent));
			jj = this.add (jj, this.multiply (jjBit, exponent));
		}
		
		return this.sparse(ii, jj, weighted);
	}
}

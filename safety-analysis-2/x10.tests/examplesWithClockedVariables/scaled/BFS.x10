/* 

BFS algorithm

Borrowed from Guy Blelloch's Cilk applications
Ported to X10 by Nalini Vasudevan


*/

import x10.util.HashSet;
import x10.util.Random;


class Edge {
  var first: int;
  var second: int;
  public def this (f: int, s: int) {
  	first = f;
  	second = s;
  }
  public def this () {
  }
};


class EdgeArray {
  var E: Rail[Edge]!;
  var numRows: int;
  var numCols: int;
  var nonZeros: int;

  public def this(EE: Rail[Edge]!, r: int, c: int, nz: int) {
  	E = EE;
  	numRows = r;
  	numCols = c;
  	nonZeros = nz;
  }

};



class Pair {
 public var first: int;
 public var second: int;
 
 public def this (f: int, s: int) {
 	first = f;
 	second = s;
 }

};

class seq {


  public var S: Edge;
  public var sz: int;
  public def this(seq: Edge, size: int) {
  		S = seq;
  		sz = size;
  }

}

class Vertex {
  var Neighbors: Rail[int]!;
  var degree: int = 0;

  public def this(N: Rail[int]!, d: int) {
  	Neighbors = N;
  	degree = d;
  }
  
  public def this () {
  }

};



class Graph {
  var V: Rail[Vertex]!;
  var n: int;
  var m: int;
  
  public def this(VV: Rail[Vertex]!, nn: int, mm: int) {
   V = VV;
   n = nn;
   m = mm;
 }
 
 public def print() {
  Console.OUT.println( "vertices = " + n );
  var i: int = 0;
  var j: int = 0;
  for (i=0; i < n; i++) {
     Console.OUT.println( "Id = " + i);
     val vi = V(i) as Vertex!;
     Console.OUT.print( " Deg = " + vi.degree + " neighbors = ");
     val neighbors = vi.Neighbors as Rail[Int]!;
     for (j =0; j < vi.degree; j++) 
        Console.OUT.print (" " + neighbors(j) + " ");
        
      Console.OUT.println(" ");
  }
}
 
 
};



class GraphUtil {

public static def hash(aa: long): long
{
  var a: long = aa;
   a = (a+0x7ed55d16) + (a<<12);
   a = (a^0xc761c23c) ^ (a>>19);
   a = (a+0x165667b1) + (a<<5);
   a = (a+0xd3a2646c) ^ (a<<9);
   a = (a+0xfd7046c5) + (a<<3);
   a = (a^0xb55a4f09) ^ (a>>16);
   return a;
}

public static def makeDimensionalGraph(dim: int, degree:int, numRows:int): EdgeArray {
  val halfDegree = degree/2;
  val nonZeros = numRows*halfDegree;
  val E = Rail.make[Edge](nonZeros, (int) => new Edge()) as Rail[Edge]!;

  //val RANDOM_SEED = 10101010;
  //var r: Random! = new Random(RANDOM_SEED);
  var r: Random! = new Random();
  
  var k: int = 0;
  for (k=0; k < nonZeros; k++) {
    val i = k;
    var pow: int = dim+2;
    var h: long = k as Long;
	val j = Math.abs(r.nextInt()) % (degree);
	
	
    /* do {
      while ((((h = hash(h)) % 1000003L) < 501L)) pow += dim;
      j = (((i as Long) + ((h = hash(h)) % ((1 << pow) as Long))) as int) % numRows;
      //Console.OUT.println(j+ " " + i);
    } while (j == i); */ 
 
    val Ek = E(k) as Edge!;
    Ek.first = i;  Ek.second = j;
  }
   
  return new EdgeArray(E,numRows,numRows,nonZeros);
}

public static def graphFromEdges(EAA: EdgeArray, makeSymmetric: boolean) : Graph {
  val EA = EAA as EdgeArray!;
/*  val E = new Seq(EA.E,EA.nonZeros);
  if (makeSymmetric) {
    val F = E.map<edge>(flip);
    E = E.append(F);
    F.del();
  } 
  //startTime();
  seq<seq<edge> > r=radixSort::collect(E,EA.numRows,firstF());

  seq<neighbors> EE = r.map<neighbors>(remDups);

  int edgecount = (EE.map<int>(edgeCount_FM)).reduce(utils::addF<int>());*/
  val n = (EA.nonZeros *2 / EA.numRows) + 1;
  Console.OUT.println("n = " + n);

  /*cout << "Edges: " << edgecount << " Vertices: " << n << endl; */
  val EE = EA.E as Rail[Edge]!;

  val v = Rail.make[Vertex](n, (int) => new Vertex());
	
 var i: int = 0;  
  
  for (i=0; i < n; i++) {
    val edge = EE(i) as Edge!;
    val vertex1 = v(edge.first) as Vertex!;
    vertex1.degree = vertex1.degree + 1; 
    val vertex2 = v(edge.second) as Vertex!;
    vertex2.degree = vertex2.degree + 1; 
  }
  
  
  for (i=0; i < n; i++) {
	val vi = v(i) as Vertex!;
    val nn = Rail.make[int](vi.degree);
    vi.Neighbors = nn;
    /*for (int j=0; j < v(i).degree; j++) {
      v(i).Neighbors(j) = EE(i).second(j);
    }*/
  } 

  val neighborCounts = Rail.make[int](n, (Int) => 0) as Rail[int]!;

  for (i=0; i < n; i++) {
    val edge = EE(i) as Edge!;
    val vertex1 = edge.first;
    val vertex2 = edge.second;
    val nc1 = neighborCounts(vertex1);
    val vvertex1 = v(vertex1) as Vertex!;
    vvertex1.Neighbors(nc1) = vertex2;
    neighborCounts(vertex1) ++;
    
   
    val nc2 = neighborCounts(vertex2);
    val vvertex2= v(vertex2) as Vertex!;
    // Console.OUT.println(vvertex2.Neighbors);
    vvertex2.Neighbors(nc2) = vertex1;
    neighborCounts(vertex2) ++;
     
  }

  return new Graph(v,n,EE.length); 

}

public static def convertGraph(GGG: Graph): GraphB {
 val GG = GGG as Graph!;
  val n = GG.n;
  val G = GG.V as Rail[Vertex]!;
  val GN = Rail.make[VertexB] (n+1, (int) => new VertexB ());
  var i: int = 0;
  for (i=0; i < n; i++) {
    val GNi = GN(i) as VertexB!;
    val Gi = G(i) as Vertex!;
    GNi.degree = Gi.degree;
    //val nn = Rail.make[int](2*Gi.degree);
    GNi.Neighbors = Rail.make[int](Gi.degree);
    GNi.Children = Rail.make[int](Gi.degree);
    GNi.cDegree = 0;
    var j: int = 0;
    for (j=0; j < GNi.degree; j++) {
      GNi.Neighbors(j) = Gi.Neighbors(j);
    }
  }

  return new GraphB(GN,n,GG.m);
}


public static def graphRandomWithDimension(dimension: int, edgesPerVertex: int, n: int): Graph {
  val EA = makeDimensionalGraph(dimension, edgesPerVertex, n);
  val G = graphFromEdges(EA, true);
  return G;
}


}

class VertexB {
  public var Neighbors: Rail[int]!;
  public var Children: Rail[int]!;
  public var degree: int;
  public var level: int;
  public var pId: int;
  public var cDegree: int;

};

class GraphB {
  var V: Rail[VertexB]!;
  var n: int;
  var m: int;
  public def this(VV: Rail[VertexB]!, nn: int, mm: int) {
  	 V = VV;
  	 n = nn;
  	 m = mm; 
  }

};




class BFSUtil {


// **************************************************************
//    BFS
// **************************************************************




public static def BFS(start: int, GAA: GraphB): Pair {
 val GA = GAA as GraphB!;
  val numVertices = GA.n;
  val numEdges = GA.m;
  val G = GA.V;
  val Counts = Rail.make[int](numVertices);

  //cilk_for
  var i: int = 0;
  for(i = 0; i < numVertices; i++) {
  	  val Gi = G(i) as VertexB!; 
      Gi.pId = 32628; Gi.level=-1;
  }
  var Frontier: Rail[int]! = Rail.make[int](numEdges);
  var FrontierNext: Rail[int]! = Rail.make[int](numEdges);

   Frontier(0) = start; 
   var frontierSize: int = 1;
   val Gstart = G(start) as VertexB!;
   Gstart.pId = 0;
   Gstart.level = 0;
   var round: int = 0;
   var totalVisited: int = 0;
   Console.OUT.println("BFS");
   Console.OUT.println ("Level 0: 0");
  while (frontierSize > 0) {
   
    val Offsets = new Array[int](0..numEdges-1, (Point)=>0);
    totalVisited += frontierSize;
    round++;
    Console.OUT.print("Level " + round + ": ");
    // For each vertex in the frontier try to "hook" unvisited neighbors.
    val Frontierr = Frontier;
    val FrontierNextt = FrontierNext;
    //Console.OUT.println( "Frontiersize " + frontierSize);
    finish for((ii) in 0..frontierSize-1) async {
      	var k: int = 0;
      	val v = Frontierr(ii);
      	var j: int = 0;
      	val Gv = G(v) as VertexB!;
      	for (j=0; j < Gv.degree; j++) {
        	val ngh = Gv.Neighbors(j);
        	val Gngh = G(ngh) as VertexB!;
    
			if (Gngh.level == -1)
		  		atomic {
		  			if (Gngh.pId > v) {
	    					Gngh.pId = v;
	      					Gv.Children(k) = ngh;
	      					k++;
	      					Offsets(ii)++;
	      			}
	      		}
	      }		
		 Counts(ii) = Gv.cDegree = k;
		 //Console.OUT.println("Counts(0)" + Counts(0));
      	
      }
      
 //Console.OUT.println("Counts(0)" + Counts(0));

    // Find offsets to write the next frontier for each v in this frontier
    //FIXME val nr = scan(Counts,Offsets,frontierSize,addF(),0);
    
    // Move hooked neighbors to next frontier.
    var off: int = 0;   
    finish for((iii)  in  0..frontierSize-1) async {
		val v = Frontierr(iii);
		val o = iii == 0 ? 0 : Offsets(iii - 1);
		var k : int = 0;
		var j: int = 0;
		val Gv = G(v) as VertexB!;
		
	
		for ( j=0; j < Counts(iii); j++) {
			 val children = Gv.Children;
	 		 val ngh = children(j);
	 		 val Gngh  = G(ngh) as VertexB!;
	 		//Console.OUT.println(Gngh.level + " " + Gngh.pId);
	  		if (Gngh.pId == v) {
	    		k++;
	    		Console.OUT.print(ngh + " ");
	    		val Frontierriii = Frontierr(iii); 
	    		val GFrontieri = G(Frontierriii) as VertexB!;
	    		val childrn = GFrontieri.Children;
	    		FrontierNextt(o+j) = childrn(j);
	    		Gngh.level = Gngh.level+1;
	  		} 
	  		//else FrontierNext(o+j) = -1;
		}
		
	
		
		Gv.cDegree = k;
      }

	frontierSize = Offsets.reduce(Int.+, 0);
	
	//Console.OUT.println("frontierSize=" + frontierSize);
	off = 0;
	val tmp = Frontier;
	Frontier = FrontierNext;
	FrontierNext = tmp;
	Console.OUT.println(" ");
    // Filter out the empty slots (marked with -1)
   // FIXME frontierSize = filter(FrontierNext,Frontier,nr,nonNegF());
  }

  return new Pair(totalVisited,round);
}

}

class BFSOrig {

public static def testBFS(GG: Graph) : void {
  val G = GG as Graph!;
  Console.OUT.println("Graph size: n = " + G.n + " m = " + G.m);
  val GB = GraphUtil.convertGraph(G);
  val r = BFSUtil.BFS(0,GB) as Pair!;
  Console.OUT.println("visited = " + r.first + " rounds = " + r.second);


}

 public static  def main(s: Rail[String]) {

  	val n = 10;
  	val m = 10;
  	val dimension = 3;
   
    val G = GraphUtil.graphRandomWithDimension(dimension, m, n) as Graph!;
	G.print();
   
    testBFS(G);

 

  }
}

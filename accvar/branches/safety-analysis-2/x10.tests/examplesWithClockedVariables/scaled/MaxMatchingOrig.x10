/*
 Maximal Matching
 
 Taken from Guy Blelloch's website
 Ported to X10 by Nalini Vasudevan

*/

import x10.util.Random;
import x10.util.HashSet;
import x10.util.GrowableRail;


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


class VertexB {
  public var Neighbors: Rail[int]!;
  public var Children: Rail[int]!;
  public var degree: int;
  public var level: int;
  public var pId: int;
  public var cDegree: int;

};


// **************************************************************
//    MAXIMAL MATCHING
// **************************************************************


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



// Holds state per vertex
class VertexD {
  var ngh: int; // the neighbor in the matching, initially -1
  var coin: int; // coin flipped for the algorithm
  var flag: boolean; // 1 if matched or all neighbors are matched (all 1 at end)
  
  public def vertexD () {
  	ngh=-1; flag=true; coin = 0;
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

class GraphUtils {

public static def hash(aa: long): ulong
{
  var a: ulong = aa;
   a = (a+0x7ed55d16UL) + (a<<12);
   a = (a^0xc761c23cUL) ^ (a>>19);
   a = (a+0x165667b1UL) + (a<<5);
   a = (a+0xd3a2646cUL) ^ (a<<9);
   a = (a+0xfd7046c5UL) + (a<<3);
   a = (a^0xb55a4f09UL) ^ (a>>16);
   return a;
}

public static def makeDimensionalGraph(dim: int, degree:int, numRows:int): EdgeArray {
  val halfDegree = ((degree + 1)/2);
  val nonZeros = numRows*halfDegree;
  val E = Rail.make[Edge](nonZeros, (int) => new Edge()) as Rail[Edge]!;

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
    if (i == j) {
		k--; continue;
    }
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
  val n = (EA.nonZeros *2 / EA.numRows);
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

class notYetMatched_FF {

  var GD: Rail[VertexD]!;
  
  public def this (GGD: Rail[VertexD]) {
  	GD = GGD as Rail[VertexD]!; 
  }
  
  public def search (v: int): boolean {
    val GDv = GD (v) as VertexD!;
   
    if (GDv.coin % 2 == 1) {
      // head with all neighbors assigned   
      if (GDv.ngh == -2 ) GDv.flag = true;
	  
      // matched head
      else if (GDv.ngh != -1) {
        
        val GDngh = GD(GDv.ngh) as VertexD!; 
      	if (GDngh.ngh == v) 
      		GDv.flag = true; 

      // unmatched head -- reset to -1
        else GDv.ngh = -1; 
      }
    }
    return !GDv.flag;
  }
  
};


public class MaxMatchingOrig {
	public static def hashVertex(round: int, i: int): ulong {
  		return GraphUtils.hash(round + i);
}

// Returns 1 for vertices that are not yet matched.


public static def  maxMatchR(Remain: GrowableRail[int]!,  G: Rail[Vertex]!, GD: Rail[VertexD]!, round: int, maxRound: int): void {
   Console.OUT.println(" round = " + round);
  if (Remain.length() > 0) {  
    // avoiding infinite loops
 

    // Each vertex flips a coin and if it comes up heads (coin=1) then it tries
    // to link with a tail by writing its ID into a neighbors ngh field
    // Also if all neighbors are already matched then set ngh field to -2
    // The round number is used to generate different coin flips on each try.
      finish for ((i) in 0 ..Remain.length()-1) async {
      val v = Remain(i);
      var found: int = 0;
      val hash = hashVertex(round,v);
      val GDv = GD(v) as VertexD!;
      GDv.coin = ((hash & 0xFFFFUL) as Long) as Int;

      if (GDv.coin % 2 == 1) {
		var j: int; 
		var ngh: int = 0; 
		var fl: int = 0;
		val Gv = G(v) as Vertex!;
		var jj : int = ((hash % (Gv.degree as ULong)) as Long) as Int;
		for (j=0; j < Gv.degree; j++, jj++) {
	  		if (jj == Gv.degree) jj = 0;
	  		ngh = Gv.Neighbors(jj);
	  		Console.OUT.println ("d= " + Gv.degree + " v= " + v + " jj = " + jj + " flip= " + (hashVertex(round,ngh)& 1UL));;
	  	    	val GDngh = GD (ngh) as VertexD!;
	  		if (GDngh.flag || ngh == v) 
	  			fl++;
	  		else 
	  	 		if ((hashVertex(round,ngh) & 1UL) == 0UL) {
	  	 			found=1; break;
	  	 	}
	}
	if (found == 1) {
	  val GDngh = GD (ngh) as VertexD!; 
	  GDngh.ngh = -3; GDv.ngh = ngh;
	}
	else {
	  // all neighbors are matched -- can't match
	  if (fl == Gv.degree) GDv.ngh = -2;
	}
   }
 }

    finish for ((i) in 0..Remain.length()-1) async {
      val v = Remain(i);
      val GDv  = GD (v) as VertexD!;
	  val Gv = G(v) as Vertex!;
      if ((GDv.coin % 2 == 0) && (GDv.ngh==-3)) {
			GDv.flag = true;
			var jj: int = GDv.coin % Gv.degree;
	  		var j: int = 0;
	  		for (j=0; j < Gv.degree; j++, jj++) {
	  		if (jj == Gv.degree) jj = 0;
	  			val ngh = Gv.Neighbors(jj);
	  			val GDngh = GD(ngh) as VertexD!;
	  			if (GDngh.ngh == v) {
	  				GDv.ngh = ngh; break;
	  			}
		}
   	}
 }

    // Since many heads might write to the same tail, this filter checks if
    // you agree that you are each others neighbors and keeps any unmatched
    // vertices.

    
    val R = new GrowableRail[int]();
    val ss = new notYetMatched_FF(GD as Rail[VertexD]!);
    var i: int;
    for (i = 0; i < GD.length; i++) {
       val keep = ss.search (i);
       if (keep) 
       		R.add(i);
    
    }

    // recurse on unmatched vertices
    maxMatchR(R, G, GD, round+1, maxRound);
  } 
}

// Only needed because of a bug in Cilk++
/*struct initGD { 
  vertexD operator() (int i) {vertexD VD; VD.ngh=-1; VD.flag=0; return VD;}
};

struct copyGD { vindex operator() (vertexD v) {return v.ngh;}};
*/

// Finds a maximal matching of the graph
// Returns an array with one element per vertex which is either set
// to the matching vertex id, or to -2 if not match.
// For some reason putting a cilk_for in this code breaks
public static def maximalMatching(G: Graph!, seed: int) : Rail[int] {
   val n = G.n;
   val round = seed;
   val R = new GrowableRail[int]() as GrowableRail[int]!;
   val GD = Rail.make[VertexD](n, (int) => new VertexD()) as Rail[VertexD]!;
   var i: int = 0;
   for (i = 0; i < n; i++) {
  	 R.add(i);
  }

  //seq<int> Flags = seq<int>(n, utils::zeroF<int>());
  /* val Flags = Rail.make[int](n, (int) => 0);
  seq<vindex> R = seq<vindex>(n,utils::identityF<int>());
  seq<vertexD> GD = seq<vertexD>(n,initGD()); */
  // if it runs form more than 100 rounds, something is wrong
  maxMatchR(R,G.V as Rail[Vertex]!,GD,round,round+100); 
 // R.del();
  //vindex* Ngh = newA(vindex,n);
  //cilk_for (int j=0; j<n; j++) Ngh[j] = GD.S[j].ngh;
 // seq<vindex> Ngh = GD.map<vindex>(());
  //GD.del();
  //return Ngh.S;
   val ngh = Rail.make[int](GD.length, (i: int) => (GD(i) as VertexD!).ngh);
  return ngh;  
}  

// Checks for every vertex if locally maximally matched
public static def checkMaximalMatching(GG: Graph!, GD: Rail[Int]!): void {
  val n = GG.n;
  val G = GG.V;
  var i: int;
  for (i=0; i < n; i++) {
    val Gi = G(i) as Vertex!;
    if (GD(i) == -1) Console.OUT.println("Problem!");
    else if (GD(i) == -2) {
      var j: int;
      for (j=0; j < Gi.degree; j++) {
			val ngh = Gi.Neighbors(j);
			if (GD(ngh) < 0 && ngh != i) {
	  		Console.OUT.println ("unassigned edge: " + i + "," + ngh);
	
	   } 
     } 
   } else {
      val GDi = GD(i);
      if (GD(GDi) != i) {
			Console.OUT.println("misassigned vertex: " + i + "," + GD(i)
	     + "," + GD(GDi));
      }
    }
  }
 }
 
 public static  def main(s: Rail[String]) {
  val n = 10;
  val m = 10;
  val dimension = 2;

  val G = GraphUtils.graphRandomWithDimension(dimension, m, n) as Graph!;

  G.print();

  val GD = maximalMatching(G,0) as Rail[Int]!;
 
  checkMaximalMatching(G,GD);

  
  var i: int;
  var count: int = 0;
  Console.OUT.print ("Maximal Matching: ");
  for(i= 0; i < n; i++) {
  	if (GD(i) >= 0) { 
  		Console.OUT.print(i + "-" + GD(i) + "; "); 
  		count++;
  	 }
  }
  Console.OUT.println ("Set size = " + count/2);
}
 

}

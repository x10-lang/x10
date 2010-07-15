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


/*
// Holds state per vertex
class vertexD {
  ngh: int; // the neighbor in the matching, initially -1
  coin: int; // coin flipped for the algorithm
  flag: boolean; // 1 if matched or all neighbors are matched (all 1 at end)
};



// Only needed because of a bug in Cilk++

struct initGD { 
  vertexD operator() (int i) {vertexD VD; VD.ngh=-1; VD.flag=0; return VD;}
};

struct copyGD { vindex operator() (vertexD v) {return v.ngh;}};
*/





// **************************************************************
//    MAXIMAL INDEPENDENT SET
// **************************************************************

class stillSearching_FF {
  _G: Rail[Vertex]!;
  _Flags: Rail[int]!;
  
  public def this (G: Rail[Vertex]!, Flags: Rail[int]!) {
  	 _G = G;
  	 _Flags = Flags;
  }
  
  
  public def search(v: int): int {
    if (_Flags(v) == 1) return 0;
    // if any neighbor is selected then drop out and set flag=2
    var i: int = 0;
    val _Gv = _G(v) as Vertex!;
    for (i=0; i < _Gv.degree; i++) {
      val ngh = _Gv.Neighbors(i);
      if (_Flags(ngh) == 1) {_Flags(v) = 2; break;}
    }
    if (_Flags(v) == 0) return 1;
    else return 0;
  }
};



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
  val halfDegree = (degree + 1) / 2;
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
  Console.OUT.println("n =" + n);

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


public class MaxIndSetOrig {






public static def maxIndSetR(Remain: GrowableRail[int]!, G: Rail[Vertex]!, Flags: Rail[int]!, 
  round: int, maxRound: int) {
  Console.OUT.println(" round = " + round + " Remaining vertices = " + Remain.length());

  if (Remain.length() > 0) {
    // Checks if each vertex is the local maximum based on hash of vertex id
    var ii: int ;
    finish for (ii=0; ii < Remain.length(); ii++) {
     val i = ii;
     async {
      val v = Remain(i);
      val h = GraphUtil.hash(round + v);
      Flags(v) = 1;
      var j: int;
      val Gv = G(v) as Vertex!;
      for (j=0; j < Gv.degree; j++) {
	      val ngh = Gv.Neighbors(j);
	// there is a harmless race condition here since ngh->flag
	// could be 0 or 1 depending on order
		  if (Flags(ngh) < 2 && GraphUtil.hash(round + ngh) >= h) { 
	  			Flags(v) = 0; break;
	   }
      }
    }
    }
    //seq<vindex> R = Remain.filter(
    
    val R = new GrowableRail[int]();
    val ss = new stillSearching_FF(G,Flags);
    var i: int;
    for (i = 0; i < Flags.length; i++) {
       val keep = ss.search (i);
       if (keep == 1) 
       		R.add(i);
    
    }
    maxIndSetR(R, G, Flags,round+1, maxRound);
  } 
}




// Checks if valid maximal independent set
public static  def checkMaximalIndependentSet(G: Graph!, Flags: Rail[int]!): void {
  val n = G.n;
  val V = G.V;
  for ((i) in 0..n-1) {
    var nflag: int = 0;
    val Vi = V(i) as Vertex!;
    for ((j) in 0..Vi.degree-1) {
      val ngh = Vi.Neighbors(j);
      if (Flags(ngh) == 1) {
		if (Flags(i) == 1) 
	  		Console.OUT.println ("bad edge " + i + "," + ngh);
	
	    } else nflag += 1;
    }
    if ((Flags(i) != 1) && (nflag == Vi.Neighbors.length)) {
      	Console.OUT.println ("bad vertex " + i );
      
    }
  }
}


public static def maxIndependentSet(G: Graph!, seed: int) : Rail[int] {
  val n = G.n;
  val round = seed;
  //seq<vindex> Remain = seq<vindex>(n, utils::identityF<int>());
  val Remain = new GrowableRail[int]() as GrowableRail[int]!;
  var i: int = 0;
  for (i = 0; i < n; i++)
  	Remain.add(i);
  //seq<int> Flags = seq<int>(n, utils::zeroF<int>());
  val Flags = Rail.make[int](n, (int) => 0);
  maxIndSetR(Remain,G.V,Flags,round,round+100);
  checkMaximalIndependentSet(G,Flags);

  return Flags;
}


public static  def main(s: Rail[String]) {
  val n: int;
  val m: int;
  val dimension: int;
  n = 10;
  m = 10;
  dimension = 2;
  val G = GraphUtil.graphRandomWithDimension(dimension, m, n) as Graph!;
  G.print();

  // Matching

   val flags = maxIndependentSet(G,0) as Rail[int]!;


  checkMaximalIndependentSet(G,flags);
  Console.OUT.print("Set = ");
  var i: int = 0;
  var count: int = 0;
  for(i=0; i < n; i++)  {
  	if (flags(i) == 1) {
  		count++;
  		Console.OUT.print(i + " ");
  	} 
 }
  Console.OUT.println(" ");
  Console.OUT.println ("set size = " + count);

}

}

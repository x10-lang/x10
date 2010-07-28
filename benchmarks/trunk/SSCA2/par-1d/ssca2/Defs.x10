package ssca2;
import x10.util.*;
import x10.lang.PlaceLocalHandle;

public class Defs {
	
	public static val container: Defs! = new Defs();
	
	public static  def init (val SCALE: Types.INT_T, CUTSHORT: Boolean, val nthreads: Int) {
		val N = 1 << SCALE  as Types.LONG_T;
		val M = 8*N as Types.LONG_T;
		val MaxIntWeight = (1<<SCALE) as  Types.WEIGHT_T;
		val SubGraphPathLength = 3; 
		
		val K4Approx = CUTSHORT ? (SCALE < 5 ? SCALE: 5) : SCALE;
		
		
		container.globals = Consts (SCALE, N, M, MaxIntWeight, SubGraphPathLength, K4Approx, nthreads);
	} 
	
	public var globals: Consts;
	//public val sprng_stream: Rail[Rail[int]];
	static val A: Types.DOUBLE_T=0.55;
	static val ARRAY_INIT_SIZE = 6;
	static val B: Types.DOUBLE_T=0.1;
	static val C: Types.DOUBLE_T=B;
	static val D: Types.DOUBLE_T=0.25;
	
	public static struct graph {
		global val n: Types.LONG_T;
	global val m: Types.LONG_T;
	
	global val endV: Rail[Types.VERT_T];
	global val numEdges: Rail[Types.LONG_T];
	global val weight : Rail[Types.WEIGHT_T];
	
	public def this (n: Types.LONG_T, m: Types.LONG_T, endV: Rail[Types.VERT_T]!, numEdges: Rail[Types.LONG_T]!, weight:
		Rail[Types.WEIGHT_T]!) {
		this.n = n;
		this.m = m;
		this.endV = endV;
		this.numEdges = numEdges;
		this.weight = weight;
	}
	};
	
	
	public static class pGraphLocal {
		val n: Types.LONG_T;
	
	public  val numEdges: LinearArray[Types.LONG_T]!;
	
	public  val endV: GrowableRail[Types.VERT_T]!;
	public  val weight : GrowableRail[Types.WEIGHT_T]!;
	
	public  val vertices: Region(1);
	
	public def this (n: Types.LONG_T, dist: Dist) {
		this.n = n / dist.places().length;
		this.vertices = this.n*here.id..((here.id+1)*this.n-1);
		val vertices_ext = this.n*here.id..((here.id+1)*this.n);
		numEdges = new LinearArray[Types.LONG_T](vertices_ext); 
		
		endV = new GrowableRail[Types.VERT_T](0);
		weight = new GrowableRail[Types.WEIGHT_T](0);
	}
	
	
	};
	
	public static struct pGraph {
		
		global val N: Types.LONG_T; /* total n */
		global val M: Types.LONG_T; /* total m */
		
		val chunkSize: Types.LONG_T;
	val logChunkSize: Types.LONG_T;
	
	global val sections :  PlaceLocalHandle[pGraphLocal]; /* sections of graph on p */
	global val dist: Dist(1);
	
	public def this(val n: Types.LONG_T, val m: Types.LONG_T, dist: Dist(1)) {
		M = m;
		N = n;
		sections = PlaceLocalHandle.make[pGraphLocal](dist, ()=>new pGraphLocal(n, dist));
		this.dist = dist;
		this.chunkSize = N/dist.places().length();
		this.logChunkSize = Math.log2(chunkSize);
	}
	
	public global def restrict_here () : pGraphLocal! {
		return  sections();
	}
	
	public global def  owner(v: Types.VERT_T) {
		return Place(v/chunkSize);
	}
	public global def  owner_id(v: Types.VERT_T) {
		return v >> logChunkSize;
	}
	
	};
	
	
	public static struct edge {
		public global val startVertex: Types.VERT_T;
	public global val endVertex: Types.VERT_T;
	public global val w: Types.WEIGHT_T;
	public global val e: Types.LONG_T;
	
	public def this (startVertex: Types.VERT_T, endVertex: Types.VERT_T,  e:Types.LONG_T, w:Types.WEIGHT_T) {
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.w = w;
		this.e = e;
	}
	
	
	public global safe def toString():String {
		return "(" + startVertex + ", " + endVertex + ", " + w +  e + ")";
	}
	
	};
	
};

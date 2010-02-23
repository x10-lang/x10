package ssca2;

public class defs {
	
	public static val container: defs! = new defs();
	
	public static  def init (val SCALE: types.INT_T) {
		val N = 1 << SCALE  as types.LONG_T;
		val M = 8*N as types.LONG_T;
		val MaxIntWeight = (1<<SCALE) as  types.WEIGHT_T;
		val SubGraphPathLength = 3; 
		val K4Approx = SCALE < 10 ? SCALE: 10;
		container.globals = runtime_consts (SCALE, N, M, MaxIntWeight, SubGraphPathLength, K4Approx);
	} 
	
	public var globals: runtime_consts;
	//public val sprng_stream: Rail[Rail[int]];
	static val A: types.DOUBLE_T=0.55;
	static val ARRAY_INIT_SIZE = 6;
	static val B: types.DOUBLE_T=0.1;
	static val C: types.DOUBLE_T=B;
	static val D: types.DOUBLE_T=0.25;
	
	public static struct graphSDG {
		public val startVertex: Rail[types.VERT_T];
	public val endVertex: Rail[types.VERT_T];
	public val weight: Rail[types.WEIGHT_T];
	
	public val m: types.LONG_T;
	public val n : types.LONG_T;
	public def this (m: types.LONG_T, n : types.LONG_T, v0: Rail[types.VERT_T]!, v1: Rail[types.VERT_T]!, weight: Rail[types.WEIGHT_T]!) {
		startVertex = v0;
		endVertex = v1;
		this.weight = weight;
		this.m = m;
		this.n = n;
	}
	};
	
	public static struct graph {
		val n: types.LONG_T;
	val m: types.LONG_T;
	
	val endV: Rail[types.VERT_T];
	val numEdges: Rail[types.LONG_T];
	val weight : Rail[types.WEIGHT_T];
	
	public def this (n: types.LONG_T, m: types.LONG_T, endV: Rail[types.VERT_T]!, numEdges: Rail[types.LONG_T]!, weight: Rail[types.WEIGHT_T]!) {
		this.n = n;
		this.m = m;
		this.endV = endV;
		this.numEdges = numEdges;
		this.weight = weight;
	}
	};
	
	public static class edge {
		public startVertex: types.VERT_T;
	public endVertex: types.VERT_T;
	public w: types.WEIGHT_T;
	public e: types.LONG_T;
	
	public def this (startVertex: types.VERT_T, endVertex: types.VERT_T,  e:types.LONG_T, w:types.WEIGHT_T) {
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.w = w;
		this.e = e;
	}
	};
	
};

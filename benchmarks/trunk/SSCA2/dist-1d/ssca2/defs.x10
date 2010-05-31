package ssca2;
import x10.util.*;
import x10.lang.PlaceLocalHandle;

public class defs {
	public static val container: defs! = new defs();
	
	public static  def init (val SCALE: types.INT_T, CUTSHORT: Boolean) {
		val N = 1 << SCALE  as types.LONG_T;
		val M = 8*N as types.LONG_T;
		val MaxIntWeight = (1<<SCALE) as  types.WEIGHT_T;
		val SubGraphPathLength = 3; 

                val K4Approx = CUTSHORT ? (SCALE < 10 ? SCALE: 10) : SCALE;

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
        public global val startVertex: Rail[types.VERT_T];
        public global val endVertex: Rail[types.VERT_T];
        public global val weight: Rail[types.WEIGHT_T];

        public global val m: types.LONG_T;
        public global val n : types.LONG_T;
        public def this (m: types.LONG_T, n : types.LONG_T, v0: Rail[types.VERT_T]!, v1: Rail[types.VERT_T]!, weight: Rail[types.WEIGHT_T]!) {
                startVertex = v0;
                endVertex = v1;
                this.weight = weight;
                this.m = m;
                this.n = n;
        }
        };
	
	public static class graphSDG_dist {
        public global val etriplets: Rail[types.UVWTriplet]!;
	
	public global val m: types.LONG_T;
	public global val n : types.LONG_T;
	public def this (m: types.LONG_T, n : types.LONG_T, etriplets: Rail[types.UVWTriplet]!) {
                this.etriplets = etriplets;
		this.m = m;
		this.n = n;
	}
	};

 public static struct graph {
        global val n: types.LONG_T;
        global val m: types.LONG_T;

        global val endV: Rail[types.VERT_T];
        global val numEdges: Rail[types.LONG_T];
        global val weight : Rail[types.WEIGHT_T];

        public def this (n: types.LONG_T, m: types.LONG_T, endV: Rail[types.VERT_T]!, numEdges: Rail[types.LONG_T]!, weight:
Rail[types.WEIGHT_T]!) {
                this.n = n;
                this.m = m;
                this.endV = endV;
                this.numEdges = numEdges;
                this.weight = weight;
        }
        };

	
	public static class pGraphLocal {
	 val n: types.LONG_T;
	
	public  val numEdges: LinearArray[types.LONG_T]!;

	public  val endV: GrowableRail[types.VERT_T]!;
	public  val weight : GrowableRail[types.WEIGHT_T]!;

        public  val vertices: Region(1);
	
         public def this (n: types.LONG_T, dist: Dist) {
            this.n = n / dist.places().length;
            this.vertices = this.n*here.id..((here.id+1)*this.n-1);
            val vertices_ext = this.n*here.id..((here.id+1)*this.n);
            numEdges = new LinearArray[types.LONG_T](vertices_ext); 
               
            endV = new GrowableRail[types.VERT_T](0);
            weight = new GrowableRail[types.WEIGHT_T](0);
         }


	};

public static struct pGraph {

        global val N: types.LONG_T; /* total n */
        global val M: types.LONG_T; /* total m */
 
        val chunkSize: types.LONG_T;
        val logChunkSize: types.LONG_T;

        global val sections :  PlaceLocalHandle[pGraphLocal]; /* sections of graph on p */
        global val dist: Dist(1);

         public def this(val n: types.LONG_T, val m: types.LONG_T, dist: Dist(1)) {
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

        public global def  owner(v: types.VERT_T) {
            return Place(v/chunkSize);
       }
        public global def  owner_id(v: types.VERT_T) {
            return v >> logChunkSize;
       }

        };

	
	public static struct edge {
        public global val startVertex: types.VERT_T;
	public global val endVertex: types.VERT_T;
	public global val w: types.WEIGHT_T;
	public global val e: types.LONG_T;
	
	public def this (startVertex: types.VERT_T, endVertex: types.VERT_T,  e:types.LONG_T, w:types.WEIGHT_T) {
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

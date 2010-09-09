package graph;
/**
 * (c) IBM Corporation 2007
 * Author: Guojing Cong
 * Tong Wen
 * Vijay Saraswat
 * 
 * Iterative version of main loop. Uses the frame directly to represent tasks. Edges are
 * represented as references to vertices, not their indices.
 * 
 * 1/28/2008 vj (with Doug Lea)
 * Trying to improve performance by batching.
 */


public class DFS {
    final BooleanRef verificationResult = new BooleanRef();
    static final long NPS = (1000L * 1000 * 1000);
    static int BATCH_SIZE;
	
	public final class V  extends Vertex {
	//	public V next; // link for batching
		public V [] neighbors;
		public V parent;
		public V parent() { return parent;}
		public V(int i){super(i);}
	
		void makeRoot() {
			level=1;
			parent=this;
		}

		void initNeighbors(int k) { neighbors=new V[k];}

		void addEdge(Vertex v) { 
			int i=0;
			for (;neighbors[i] != null && i < neighbors.length; i++);
			neighbors[i]= (V) v;
		}

	    public void compute() {
		for (int i=0; i < neighbors.length; ++i) {
		    V v =neighbors[i];
		    if (v.tryColor()) {
			v.parent=this;
		    }
		}
	    }

	    public void reset() {
		level=0;
		parent=null;
	    }
	    String ij(int index) {
		return graphType=='T' ? "["+(index/20) + "," + (index % 20 ) + "]" : "" + index;
	    }
	    public String toString() {
		String s="[" + (neighbors.length==0? "]" : "" + ij(neighbors[0].index));
		for (int i=1; i < neighbors.length; i++) s += ","+ ij(neighbors[i].index);
		return "v(" + ij(index) +  ",parent=" + parent.index
		    + ",degree="+neighbors.length+ ",n=" + s+"])";
	    }
	}
	boolean[] reachesRoot;
	V[] G;
	int N, M;
	char graphType;
	
	static int[] Ns = new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
	public DFS (int n, int d, char graphType){
		this.graphType=graphType;
		N=(graphType=='T')?n*n:n;
		M=N*d;
		G = new V[N]; for(int i=0;i<N;i++) G[i]=new V(i);
		if (graphType=='E') GraphGenerator.randomEdgeGraph(M, G);
		else if (graphType=='T') GraphGenerator.torusGraph(n,G);
		else GraphGenerator.kGraph(G);
	}
	public static void main(String[] args) {
		Options O;
		try {
		 O = new Options("SpanFTROLev", args);
		} catch (Exception e) {
			return;
		}
		final Pool g = new Pool(O.P);
		if (O.N >= 0) Ns = new int[] {O.N};
		BATCH_SIZE=O.BATCH_SIZE;
		assert (O.graphType=='T' || O.graphType=='K' ||O.graphType=='E');
		long sc = 0, sa = 0;
		for (int i=Ns.length-1; i >= 0; i--) {
			System.gc();
			long creationTime = -System.nanoTime();
			DFS graph = new DFS(Ns[i],O.D,O.graphType);
			creationTime += System.nanoTime();
			System.out.printf("Creation time:%5.4f s",((double) creationTime)/NPS);
			System.out.println();
			if (O.graphOnly) return;
			
			graph.reachesRoot = new boolean[graph.G.length];
			
			for (int k=0; k < 20; ++k) {
				final V root = graph.G[1];
				root.makeRoot();
				long t = -System.nanoTime();
				root.compute();
				t += System.nanoTime();
				double secs = ((double) t)/NPS, Meps = graph.M/(1000*1000*secs);
				System.out.printf("M=%d t=%5.4f s %5.2f MegaEdges/s", 
						  graph.M, secs, Meps);
				System.out.println();
				sc=nsc; sa = nsa;
				if (O.verification)
					Verifier.verify(g, graph.G, graph.reachesRoot, graph.verificationResult);
				for (V v : graph.G) v.reset();
			}
			System.out.printf("Completed iterations for N=%d", graph.N);
			System.out.println();
		}   
		g.shutdown(); 
	}
}


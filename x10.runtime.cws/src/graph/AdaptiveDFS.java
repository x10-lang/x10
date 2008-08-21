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
 * 2/5/2008
 * Replaced batching by Adaptive pushing formulated by Doug Lea.
 */


import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;

public class AdaptiveDFS {
	final BooleanRef verificationResult = new BooleanRef();
	static final long NPS = (1000L * 1000 * 1000);
	static int BATCH_SIZE;
	
	public final class V  extends Vertex {
		public V next; // link for batching
		public V [] neighbors;
		public V parent;
		public V parent() { return parent;}
		public V(int i){super(i);}
	
		
		@Override
		void makeRoot() {
			level=1;
			parent=this;
		}
		@Override
		void initNeighbors(int k) { neighbors=new V[k];}
		@Override
		void addEdge(Vertex v) { 
			int i=0;
			for (;neighbors[i] != null && i < neighbors.length; i++);
			neighbors[i]= (V) v;
		}
		/**
         * Traverse the list ("oldList") embedded across .next fields,
         * starting at this node, placing newly discovered neighboring
         * nodes in newList. If the oldList becomes exhausted, swap in
         * newList and continue. Otherwise, whenever the length of
         * newList exceeds current number of tasks in work-stealing
         * queue, push list onto queue.
         * @author dl 2/16/08
         */ 
		
		static final int LOG_MAX_BATCH_SIZE = 7;
		@Override
		public void compute(Worker w) throws StealAbort {
			w.popFrame();
			int batchSize = 0; // computed lazily
			V newList = null;
			int newLength = 0;
			V oldList = this;
			V par = parent;
			do {
				V v = oldList;
				V[] edges = v.neighbors;
				oldList = v.next;
				int nedges = edges.length;
				for (int k = 0; k < nedges; ++k) {
					V e = edges[k];
					if (e != null && e.level == 0 &&
							UPDATER.compareAndSet(e,0,1)) {
						e.parent = par;
						e.next = newList;
						newList = e;
						if (batchSize == 0) {
							int s = w.getLocalQueueSize();
							batchSize = ((s < 1)? 1 :
								((s >= LOG_MAX_BATCH_SIZE)?
										(1 << LOG_MAX_BATCH_SIZE) :
											(1 << s)));
						}
						if (++newLength >= batchSize) {
							newLength = 0;
							batchSize = 0;
							if (oldList == null)
								oldList = newList;
							else
								w.pushFrame(newList);
							newList = null;
						}
					}
				}
				if (oldList == null) {
					oldList = newList;
					newList = null;
					newLength = 0;
				}
			} while (oldList != null);
		}



		@Override
		public void reset() {
			level=0;
			parent=null;
			next=null;
		}
		String ij(int index) {
			return graphType=='T' ? "["+(index/20) + "," + (index % 20 ) + "]" : "" + index;
		}
		public String toString() {
			String s="[" + (neighbors.length==0? "]" : "" + ij(neighbors[0].index));
			for (int i=1; i < neighbors.length; i++) s += ","+ ij(neighbors[i].index);
			return "v(" + ij(index) +  ",parent=" 
			+ (parent==null? "null" : "" + parent.index)
			+ ",degree="+neighbors.length+ ",n=" + s+"])";
		}
	}
	boolean[] reachesRoot;
	V[] G;
	int N, M;
	char graphType;
	
	static int[] Ns = new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
	public AdaptiveDFS (int n, int d, char graphType){
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
			AdaptiveDFS graph = new AdaptiveDFS(Ns[i],O.D,O.graphType);
			creationTime += System.nanoTime();
			System.out.printf("Creation time:%5.4f s",((double) creationTime)/NPS);
			System.out.println();
			if (O.graphOnly) return;
			
			graph.reachesRoot = new boolean[graph.G.length];
			
			for (int k=0; k < 20; ++k) {
				final V root = graph.G[1];
				root.makeRoot();
				GloballyQuiescentVoidJob job = new GloballyQuiescentVoidJob(g, graph.G[1]);
				long t = -System.nanoTime();
				g.invoke(job);
				t += System.nanoTime();
				double secs = ((double) t)/NPS, Meps = graph.M/(1000*1000*secs);
				long nsc = g.getStealCount(), nsa = g.getStealAttempts();
				System.out.printf("M=%d t=%5.4f s %5.2f MegaEdges/s sc=%d sa=%d", 
						graph.M, secs, Meps, nsc-sc, nsa-sa);
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


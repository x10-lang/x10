package graph;
/**
 * (c) IBM Corporation 2007
 * Author: Vijay Saraswat
 * 
 * 
 * A purely sequential breadth-first search, with no parallellism associated overhead. 
 * Intended to be used as the basis for speedup numbers.
 */


import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;


public class BFSRef {
	public static int BATCH_SIZE=32;
	public final class V  extends Vertex {
		public V [] neighbors;
		public V next; // link for batching
		public V(int i){super(i);}
		@Override
		void makeRoot() {
			level=1;
			parent=index;
		}
		@Override
		public void reset() {
			level=0;
			parent=index;
			next=null;
		}
		@Override
		void initNeighbors(int k) { neighbors=new V[k];}
		@Override
		void addEdge(Vertex v) { 
			int i=0;
			for (;neighbors[i] != null && i < neighbors.length; i++);
			neighbors[i]= (V) v;
		}
		public void compute(Worker w)  throws StealAbort {
			w.popAndReturnFrame();
			V node=this; 
			final int BS=BATCH_SIZE, pid = w.index;
			int nb=0;
			for (;;) {
				for (V v : node.neighbors) {
					if (v.tryLabeling()) {
						v.parent=node.index;
						v.next = batch[pid];
						batch[pid] = v;
						if (++nb >= BS) {
							w.pushFrameNext(batch[pid]);
							batch[pid]=null;
							nb=0;
						}
					}
				}	
				V nxt = node.next;
				if (nxt == null) break;
				node.next=null;
				node=nxt;
			}
		}
		@Override
		public String toString() {
			String s="[" + (neighbors.length==0? "]" : "" + neighbors[0].index);
			for (int i=1; i < neighbors.length; i++) s += ","+neighbors[i].index;
			return "v(" + index + ",degree="+neighbors.length+ ",n=" + s+"])";
		}
	}
	V[] G;
	static int[] Ns = new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
	int N, M;
	public BFSRef (int n, int m, char graphType){
		N=n;
		M=m;
		if (graphType=='T') N=n*n;
		G = new V[N]; for(int i=0;i<N;i++) G[i]=new V(i);
		if (graphType=='E') {
			GraphGenerator.randomEdgeGraph(M, G);
		} else if (graphType=='T') { GraphGenerator.torusGraph(n,G);
		} else if (graphType=='K') { GraphGenerator.kGraph(G);}
	}
	boolean[] reachesRoot;
	static boolean reporting = false;
	static final long NPS = (1000L * 1000 * 1000);
	static boolean graphOnly =false, verification=true;
	public static void main(String[] args) {
		Options O;
		try {
		 O = new Options("BFSRef", args);
		} catch (Exception e) {
			return;
		}
		Pool g = new Pool(O.P);
		if (O.N >= 0) Ns = new int[] {O.N};
		for (int i=Ns.length-1; i >= 0; i--) {
			
			final int N =Ns[i], NN= (O.graphType=='T'?N:1)*N, M = O.D*N;
			System.gc();
			BFSRef graph = new BFSRef(N,M, O.graphType);
			graph.reachesRoot=new boolean[NN];
			if (graphOnly) return;
		     graph.batch = new V[O.P];
			//System.out.printf("N:%8d ", N);
			for (int k=0; k < 20; ++k) {
				final V root = graph.G[1];
				root.makeRoot();
				final BFSRef GGraph = graph;
				GloballyQuiescentVoidJob job = new GloballyQuiescentVoidJob(g, root) {
					@Override 
					protected void onCheckIn(Worker w) {
						// each time the worker checks in, it empties the batch.
						final int pid = w.index;
						V node = GGraph.batch[pid];
						if (node != null) {
							w.pushFrameNext(node);
							GGraph.batch[pid]=null;
						}
					}
				};
				long t = -System.nanoTime();
				g.invoke(job);
				t += System.nanoTime();
				double secs = ((double) t)/NPS, Meps = graph.M/(1000*1000*secs);
				System.out.printf("M=%d t=%5.4f s %5.2f MegaEdges/s", graph.M, secs, Meps);
				System.out.println();
				if (verification)
					Verifier.verify(g, graph.G, graph.reachesRoot, graph.verificationResult);
				for (V v : graph.G) v.reset();
			}
			System.out.printf("Completed iterations for N=%d", N);
			System.out.println();
		}   
		
	}
	
	V[] batch;
	final BooleanRef verificationResult = new BooleanRef();
	
	
}


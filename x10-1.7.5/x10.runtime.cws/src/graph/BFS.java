package graph;
/**
 * (c) IBM Corporation 2007
 * Author: Vijay Saraswat
 * 
 * 
 * A purely sequential breadth-first search, with no parallellism associated overhead. 
 * Intended to be used as the basis for speedup numbers.
 */


import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;


public class BFS {
	public static int BATCH_SIZE=32;

	public final class V  extends Vertex {
		public V [] neighbors;
		//public V next; // link for batching
		public V parent;
		public V parent() { return this.parent;}
		public V(int i){super(i);}
		@Override
		void makeRoot() {
			level=1;
			parent=this;
		}
		@Override
		public void reset() {
			level=0;
			parent=null;
			//next=null;
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
			for (V v : neighbors) {
				if (v.tryColor()) {
					v.parent=this;
					w.pushFrameNext(v);

				}
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
	public BFS (int n, int d, char graphType){
		N=(graphType=='T')?n*n:n;
		M=N*d;
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
		 O = new Options("BFS", args);
		} catch (Exception e) {
			return;
		}
		Pool g = new Pool(O.P);
		if (O.N >= 0) Ns = new int[] {O.N};
		BATCH_SIZE=O.BATCH_SIZE;
		for (int i=Ns.length-1; i >= 0; i--) {
			System.gc();
			long creationTime = -System.nanoTime();
			BFS graph = new BFS(Ns[i],O.D, O.graphType);
			creationTime += System.nanoTime();
			System.out.printf("Creation time:%5.4f s",((double) creationTime)/NPS);
			System.out.println();
			if (O.graphOnly) return;
			graph.reachesRoot=new boolean[graph.G.length];
		     graph.batch = new V[O.P];
			//System.out.printf("N:%8d ", N);
		    long sc = 0, sa = 0;
			for (int k=0; k < 20; ++k) {
				final V root = graph.G[1];
				root.makeRoot();
				final BFS GGraph = graph;
				GloballyQuiescentVoidJob job = new GloballyQuiescentVoidJob(g, root);
				long t = -System.nanoTime();
				g.invoke(job);
				t += System.nanoTime();
				long nsc  = g.getStealCount(), nsa = g.getStealAttempts();
				double secs = ((double) t)/NPS, Meps = graph.M/(1000*1000*secs);
				System.out.printf("M=%d t=%5.4f s %5.2f MegaEdges/s sc=%d sa=%d", graph.M, 
						secs, Meps, nsc-sc, nsa-sa);
				sa = nsa; sc = nsc;
				System.out.println();
				if (verification)
					Verifier.verify(g, graph.G, graph.reachesRoot, graph.verificationResult);
				for (V v : graph.G) v.reset();
			}
			System.out.printf("Completed iterations for N=%d", graph.N);
			System.out.println();
		}   
		
	}
	
	V[] batch;
	final BooleanRef verificationResult = new BooleanRef();
	
	
}


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


import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;

public class PerfBound {
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
		@Override void reset() {}
		@Override
		void initNeighbors(int k) { neighbors=new V[k];}
		@Override
		void addEdge(Vertex v) { 
			int i=0;
			for (;neighbors[i] != null && i < neighbors.length; i++);
			neighbors[i]= (V) v;
		}
		@Override
		public void compute(Worker w) throws StealAbort {
		}

	}
	V[] G;
	int N, M;
	char graphType;
	
	static int[] Ns = new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
	public PerfBound (int n, int d, char graphType){
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
		 O = new Options("PerfBound", args);
		} catch (Exception e) {
			return;
		}
		Thread[] threads = new Thread[O.P];
		final int size = O.N/O.P;
		for (int i=0; i<threads.length; ++i) {
			final int ii=i;
			threads[i] = new Thread(new Runnable() {
				public void run() {
						final int low = ii*size, high = (ii+1)*size-1;
						for (int j=low; j <= high; j++) {
							
						}
					
				}
			});
		}
		
		for (int i=Ns.length-1; i >= 0; i--) {
			System.gc();
			long creationTime = -System.nanoTime();
			PerfBound graph = new PerfBound(Ns[i],O.D,O.graphType);
			creationTime += System.nanoTime();
			System.out.printf("Creation time:%5.4f s",((double) creationTime)/NPS);
			System.out.println();
			if (O.graphOnly) return;
			
			for (int k=0; k < 20; ++k) {
				long t = -System.nanoTime();
				for (Thread th : threads) th.start();
				for (Thread th: threads)
					try {
						th.join();
					} catch (InterruptedException e) {
						System.out.println("Gah ... " + th + " interrupted.");
					}
				t += System.nanoTime();
				
				double secs = ((double) t)/NPS, Meps = graph.M/(1000*1000*secs);
				System.out.printf("M=%d t=%5.4f s %5.2f MegaEdges/s", graph.M, secs, Meps);
				System.out.println();
			}
			System.out.printf("Completed iterations for N=%d", graph.N);
			System.out.println();
		}   
		
	}
}


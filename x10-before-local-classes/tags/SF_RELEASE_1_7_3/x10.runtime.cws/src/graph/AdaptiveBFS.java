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


public class AdaptiveBFS {
	public static int BATCH_SIZE=32000;
	public static final int MAX_BATCH_SIZE = 1<<9, MIN_BATCH_SIZE=4;
	public final class V  extends Vertex {
		public V [] neighbors;
		public V next; // link for batching
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
		
		@Override
        public void compute(Worker ww) throws StealAbort {
			MyWorker w = (MyWorker) ww;
		            w.popAndReturnFrame();
		            V newList = w.batch;
		            int newLength = w.batchSize;
		            V oldList = this;
		            V par = parent;
		            if (false)
		            	System.out.println(w + " visiting " + this);
		            do {
		                V v = oldList;
		                V[] edges = v.neighbors;
		                oldList = v.next;
		                int nedges = edges.length;
		                for (int k = 0; k < nedges; ++k) {
		                    V e = edges[k];
		                    if (e != null && e.level == 0 &&
		                        UPDATER.compareAndSet(e,0,1)) {
		                    	//System.out.println("Linking in " + e);
		                        e.parent = par;
		                        e.next = newList;
		                        newList = e;
		                        if (++newLength >= w.maxBatchSize) {
		                        	if (false) {
		                        		System.out.print(w + " pushing [" );
		                        		for (V n=newList; n!=null; n=n.next) {
		                        			System.out.print(n.index + " ");
		                        		}
		                        		System.out.println("]" + newLength);
		                        	}
		                        	w.addNextPhaseSize(newLength);
		                        	newLength=0;
		                        	w.pushFrameNext(newList);
		                        	newList = null;
		                        }
		                    }
		                }
		               
		            } while (oldList != null);
		            // write back current unfinished list into worker.
		            w.batchSize=newLength;
		            w.batch=newList;
		           if (false) System.out.println(w + " done visiting " + this.index);
		        }
        
/*
		public void compute(Worker w)  throws StealAbort {
			w.popAndReturnFrame();
			V node=this; 
			final int BS=BATCH_SIZE, pid = w.index;
			int nb=0;
			for (;;) {
				for (V v : node.neighbors) {
					if (v.tryColor()) {
						v.parent=node;
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
		}*/
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
	public AdaptiveBFS (int n, int d, char graphType){
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
	public  static final class MyWorker extends Worker {
		MyWorker(Pool p, int index) {
			super(p, index);
		}
	//	public String toString() { return "Worker " + index;}
		@Override public void advancePhase(int bNum, int lastSize) {
			super.advancePhase(bNum, lastSize);
			  int s =  (int) (lastPhaseSize()/(P*MULTIPLIER));
	            maxBatchSize = ((s < 1)? MIN_BATCH_SIZE :((s >= MAX_BATCH_SIZE)? 
	            			(MAX_BATCH_SIZE) :
	            				(s)));
	            int phase = phaseNum();
	           if (false && index==0 && phase % 100==0) 
	        	   System.out.println("In phase " + this.phaseNum() 
	        			   + " maxBatchSize=" + maxBatchSize);
	            		
		}
		
		V batch;
		int batchSize;
		int maxBatchSize;
	}
	public static Pool.WorkerMaker workerMaker = new Pool.WorkerMaker() {
		public Worker makeWorker(Pool p, int i) {
			return new MyWorker(p, i);
		}
		
	};
	static int P; static float MULTIPLIER;
	public static void main(String[] args) {
		Options O;
		try {
		 O = new Options("AdaptiveBFS", args);
		} catch (Exception e) {
			return;
		}
		P=O.P; MULTIPLIER=O.MULTIPLIER;
		Pool g = new Pool(P, workerMaker);
		if (O.N >= 0) Ns = new int[] {O.N};
		BATCH_SIZE=O.BATCH_SIZE;
		for (int i=Ns.length-1; i >= 0; i--) {
			System.gc();
			long creationTime = -System.nanoTime();
			AdaptiveBFS graph = new AdaptiveBFS(Ns[i],O.D, O.graphType);
			creationTime += System.nanoTime();
			System.out.printf("Creation time:%5.4f s",((double) creationTime)/NPS);
			System.out.println();
			if (O.graphOnly) return;
			graph.reachesRoot=new boolean[graph.G.length];
			//System.out.printf("N:%8d ", N);
		    long sc = 0, sa = 0;
			for (int k=0; k < 20; ++k) {
				final V root = graph.G[1];
				root.makeRoot();
			
				GloballyQuiescentVoidJob job = new GloballyQuiescentVoidJob(g, root) {
					@Override 
					protected void onCheckIn(Worker ww) {
						MyWorker w = (MyWorker) ww;
						if (false) 
							System.out.println(w + " onCheckIn checkIn=" + w.checkedIn() + 
									" nextPhaseHasWork= " + w.nextPhaseHasWork() + 
									
									" batch=" + w.batch + " phase=" + w.phaseNum());
						// each time the worker checks in, it empties the batch.
						if (w.batch !=null) {
							w.addNextPhaseSize(w.batchSize);
							w.pushFrameNext(w.batch);
							w.batch=null;
							w.count=0;
						}
					}
				};
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
	
	final BooleanRef verificationResult = new BooleanRef();
	
	
}


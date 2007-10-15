



/**
 * (c) IBM Corporation 2007
 * Author: Guojing Cong
 * Tong Wen
 * Vijay Saraswat
 * 
 * Iterative version of main loop. Uses the frame directly to represent tasks. Edges are
 * represented as references to vertices, not their indices.
 */

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import x10.runtime.cws.Frame;
import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;


public class SpanFTRO {
	
	public class V  extends Frame {
		public final int index;
		public volatile V parent;
		public V [] neighbors;
		public V(int i){index=i;}
		volatile int PC=0;
		@Override
		public void compute(Worker w) throws StealAbort {
			// An improperly nested frame is one which is not
			// guaranteed to be at the bottom of the dequeue when its
			// compute method terminates. For instance, such a frame
			// may have pushed other frames onto the dequeue without
			// transferring control to them.
			
			// Improperly nested frames may be executed a second time
			// either becuase they are stolen or because the worker
			// has just executed them. We use this technique to pop
			// the frame off the dequeue.
			if (PC==1) {
				w.popFrame();
				return;
			}
			PC=1;
			V node = this;
			for (;;) {
				V lastV = null;
				for (int k=0; k < node.neighbors.length; k++) {
					final V v = node.neighbors[k];
					if (UPDATER.get(v)==null && UPDATER.compareAndSet(v,null,node)) {
						if (lastV != null) {
							// async lastV.compute();
							w.pushFrame(lastV);
						}
						lastV =v;
					}
				}
				if ((node=lastV)==null) break;
			}
			// cannot call w.popFrame() because this frame may not be
			// the current frame on the dequeue, since the task is not
			// properly nested.
		}
		public boolean verify(V root, boolean[] reachesRoot, int N) {
			V p = parent;
			int count=0;
			try { 
				while (! (p==null || reachesRoot[p.index] || p ==this || p == root || count == N)) {
					p = p.parent;
					count++;
				}
				reachesRoot[index]=(count < N && p != null && ( p==root || reachesRoot[p.index]));
				return reachesRoot[index];
			} finally {
				if (reporting) {
					if (count > N-10) {
						System.out.println(Thread.currentThread() + " finds possibly bad guy " + this +
								"count=" + count + "p=" + p );
					}
					if (! reachesRoot[index])
						System.out.println(Thread.currentThread() + " finds bad guy " + this +
								"count=" + count + "p=" + p );
				}
			}
		}
		public void reset() {
			PC=0;
			UPDATER.set(this,null);

		}
		@Override
		public String toString() {
			String s="[" + (neighbors.length==0? "]" : "" + neighbors[0].index);
			for (int i=1; i < neighbors.length; i++) s += ","+neighbors[i].index;
			return "v(" + index +  ",parent=" + (parent==null? "null" :parent.index + "")
			+ ",degree="+neighbors.length+ ",n=" + s+"])";
		}
	}
	
	class E{
		public int v1,v2;
		public boolean in_tree;
		public E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
	}
	
	int m;
	V[] G;
	E[] El;
	E[] El1;
	final static AtomicReferenceFieldUpdater<V,V> UPDATER 
	= AtomicReferenceFieldUpdater.newUpdater(V.class, V.class, "parent");
	
	//AtomicIntegerArray visitCount ;
	int ncomps=0;
	
	static int[] Ns = new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
	int N, M;
	
	int randSeed = 17673573; 
	int rand32() { return randSeed = 1664525 * randSeed + 1013904223;}
	public SpanFTRO (int n, int m){
		N=n;
		M=m;
	
		
		/*constructing edges*/
		El = new E [M];
		G = new V[N];
		for(int i=0;i<N;i++) {
			G[i]=new V(i);
			
		}
		
		for (int i=0; i <M; i++) El[i] = new E(Math.abs(rand32())%N, Math.abs(rand32())%N);
		
		int[] D = new int [N];
		/* D[i] is the degree of vertex i (duplicate edges are counted).*/
		for(int i=0;i<M;i++){
			D[El[i].v1]++;
			D[El[i].v2]++;
		}
		
		int[][] NB = new int[N][];/*NB[i][j] stores the jth neighbor of vertex i*/
		// leave room for making connected graph by +2
		for(int i=0;i<N;i++) NB[i]=new int [D[i]+2]; 
		
		/*Now D[i] is the index for storing the neighbors of vertex i
		 into NB[i] NB[i][D[i]] is the current neighbor*/
		for(int i=0;i<N;i++) D[i]=0;
		
		m=0;
		for(int i=0;i<M;i++) {
			boolean r=false;;  
			/* filtering out repeated edges*/
			for(int j=0;j<D[El[i].v1] && !r ;j++){ 
				if(El[i].v2==NB[El[i].v1][j]) r=true;
			}
			if(r){
				El[i].v1=El[i].v2=-1; /*mark as repeat*/
			} else {
				m++;
				NB[El[i].v1][D[El[i].v1]]=El[i].v2;
				NB[El[i].v2][D[El[i].v2]]=El[i].v1;
				D[El[i].v1]++;
				D[El[i].v2]++;
			}
		}  
		
		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		//visitCount = new AtomicIntegerArray(N);
		int[] stack = new int [N]; 
		int[] connected_comps  = new int [N], color=new int[N];
		
		int top=-1;
		ncomps=0;
		for(int i=0;i<N ;i++) {
			if (color[i]==1) continue;
			connected_comps[ncomps++]=i;
			stack[++top]=i;
			color[i]=1;
			while(top!=-1) {
				int v = stack[top];
				top--;
				
				for(int j=0;j<D[v];j++) {
					final int mm = NB[v][j];
					if(color[mm]==0){
						top++;
						stack[top]=mm;
						color[mm]=1;
					}
				}
			}
		}
		
		//System.out.println("ncomps="+ncomps);
		El1 = new E [m+ncomps-1]; 
		
		
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(El[i].v1!=-1) El1[j++]=El[i]; 
		
		//if(j!=m) System.out.println("Remove duplicates failed");
		//else System.out.println("Remove duplicates succeeded,j=m="+j);
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			NB[connected_comps[i]][D[connected_comps[i]]++]=connected_comps[i+1];
			NB[connected_comps[i+1]][D[connected_comps[i+1]]++]=connected_comps[i];
			El1[i+m]=new E (connected_comps[i], connected_comps[i+1]);
		}
		
		//visited = new boolean[N];
		reachesRoot = new boolean[N];
		for(int i=0;i<N;i++) {
			UPDATER.set(G[i],null);
			color[i]=0;
			G[i].neighbors=new V [D[i]];
			for(j=0;j<D[i];j++) {
				G[i].neighbors[j]=G[NB[i][j]];
				
			}
			if (graphOnly)
				System.out.println("G[" + i + "]=" + G[i]);
		}     
	}
	boolean[] reachesRoot;
	public boolean verifyTraverse(V root) {
		for (int i=0; i < reachesRoot.length; i++) reachesRoot[i]=false;
		int i=0;
		boolean result = false;
		try {
			for (;i<N && G[i].verify(root, reachesRoot, N);i++) ;
			return result = (i==N);
		} finally {
			if (reporting && ! result)
				System.out.println(Thread.currentThread() + " fails at " + G[i]);
		}
	}
	/*
	boolean verifyTraverse(V root) {
		V[] X = new V [N];
		for (int i=0;i<N;i++) {
			//System.out.println( G[i] + ".parent=" + G[i].parent);
			if (G[i].parent==G[i] && G[i] !=root) 
				System.out.println("Questionable guy " + G[i] + ".parent=" + G[i].parent);
		}
		for(int i=0;i<N;i++) X[i]=G[i].parent;
		V temp;
		for(int i=0;i<N;i++) {
			while(X[i]!=null && X[i] !=(temp=X[X[i].index])) X[i]=temp;
			if (X[i] != root) {
				System.out.println("X[" + i + "]=" + X[i]);
				return false;
			}
		}
		return true;
	}*/

	static boolean reporting = true;
	static final long NPS = (1000L * 1000 * 1000);
	static boolean graphOnly =false;
	public static void main(String[] args) {
		int procs;
		int num=-1;
		int D=4;
		try {
			procs = Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
			if (args.length > 1) {
				num = Integer.parseInt(args[1]);
				System.out.println("N=" + num);
			}
			if (args.length > 2) {
				D = Integer.parseInt(args[2]);
				System.out.println("D=" + D);
			}
			if (args.length > 3) {
				boolean b = Boolean.parseBoolean(args[3]);
				reporting=b;
			}
			if (args.length > 4) {
				boolean b = Boolean.parseBoolean(args[4]);
				graphOnly=b;
			}
		}
		catch (Exception e) {
			System.out.println("Usage: java SpanFTRO <threads> [<N> [<Degree> [[false|true] [false|true]]]]");
			return;
		}
		//System.out.print("Creating pool...");
		Pool g = new Pool(procs);
		//System.out.print("done.");
		if (num >= 0) {
			Ns = new int[] {num};
		}
		for (int i=Ns.length-1; i >= 0; i--) {
			
			final int N = Ns[i], M = D*N;
			// ensure the sole reference to the previous graph is nulled before gc.
			
			System.gc();
			long creationTime = -System.nanoTime();
			SpanFTRO graph = new SpanFTRO(N,M);
			creationTime += System.nanoTime();
			System.out.printf("Creation time:%5.4f s",((double) creationTime)/NPS);
			System.out.println();
			if (graphOnly) return;
		
			//System.out.printf("N:%8d ", N);
			for (int k=0; k < 9; ++k) {
				UPDATER.set(graph.G[1], graph.G[1]);
				GloballyQuiescentVoidJob job = new GloballyQuiescentVoidJob(g, graph.G[1]);
				//System.out.println(Thread.currentThread() + " Starting timed section...");
				long s = System.nanoTime();
				g.invoke(job);
				long t = System.nanoTime() - s;
				//System.out.println(Thread.currentThread() + " Done with timed section...");
				double secs = ((double) t)/NPS;
				double Meps = 1000*(M/(double)t);
				System.out.printf("N=%d t=%5.4f s %5.4f MegaEdges/s", N, secs, Meps);
				long verificationTime = - System.nanoTime();
				if (! graph.verifyTraverse(graph.G[1]))
					System.out.println("false");
				else {
					verificationTime += System.nanoTime();
					double vSecs = ((double) verificationTime)/NPS;
					System.out.printf(" (verification %5.4f s)", vSecs );
					System.out.println();
				}
				graph.clearColor();
				
			}
			System.out.printf("Completed iterations for N=%d", N);
			System.out.println();
		}   
		g.shutdown(); 
	}
	void clearColor() {
		for (V v : G) v.reset();
		
	}
}


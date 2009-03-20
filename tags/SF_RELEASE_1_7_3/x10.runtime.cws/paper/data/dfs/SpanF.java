/**
 * (c) IBM Corporation 2007
 * Author: Guojing Cong
 * Tong Wen
 * Vijay Saraswat
 * 
 * Iterative version of main loop. Uses the frame directly to represent tasks. 
 * Edges are represented as references to vertices, not their indices.
 * Manual tail-recursion optimization is not implemented.
 */

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import x10.runtime.cws.Frame;
import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;


public class SpanF {
	
	public class V  extends Frame {
		final int index;
		V parent;
		int degree;
		V [] neighbors;
		volatile int color;
		V(int i){index=i;}
		
		@Override
		public void compute(Worker w) throws StealAbort {
			V node = this;
			for (int k=0; k < node.degree; k++) {
				final V v = node.neighbors[k];
				if (v.color==0 && UPDATER.compareAndSet(v,0,1)) {
					v.parent=node;
					// async lastV.compute();
					w.pushFrame(v); 
				}
			}
		}
		@Override
		public String toString() {
			String s="[" + (neighbors.length==0? "]" : "" + neighbors[0].index);
			for (int i=1; i < neighbors.length; i++) s += ","+neighbors[i].index;
			return "v(" + index + ", color=" + color + ",parent=" + parent.index + ",degree="+degree+ ",n=" + s+"])";
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
	final static AtomicIntegerFieldUpdater<V> UPDATER 
	= AtomicIntegerFieldUpdater.newUpdater(V.class, "color");
	
	//AtomicIntegerArray visitCount ;
	int ncomps=0;
	
	static int[] Ns = new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
	int N, M;
	
	int randSeed = 17673573; 
	int rand32() { return randSeed = 1664525 * randSeed + 1013904223;}
	public SpanF (int n, int m){
		N=n;
		M=m;
		/*constructing edges*/
		El = new E [M];
		G = new V[N];
		for(int i=0;i<N;i++) G[i]=new V(i);
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
		int[] connected_comps  = new int [N]; 
		
		int top=-1;
		ncomps=0;
		for(int i=0;i<N ;i++) {
			if (G[i].color==1) continue;
			connected_comps[ncomps++]=i;
			stack[++top]=i;
			G[i].color=1;
			while(top!=-1) {
				int v = stack[top];
				top--;
				
				for(int j=0;j<D[v];j++) {
					final int mm = NB[v][j];
					if(G[mm].color==0){
						top++;
						stack[top]=mm;
						G[mm].color=1;
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
		
		for(int i=0;i<N;i++) {
			G[i].degree=D[i];
			G[i].parent = G[i];
			G[i].color=0;
			G[i].neighbors=new V [D[i]];
			for(j=0;j<D[i];j++) {
				G[i].neighbors[j]=G[NB[i][j]];
				
			}
			if (graphOnly)
				System.out.println("G[" + i + "]=" + G[i]);
		}     
	}
	
	boolean verifyTraverse(V root) {
		V[] X = new V [N];
		for (int i=0;i<N;i++) {
			if (G[i].parent==G[i] && G[i] !=root) 
				System.out.println("Questionable guy " + i + " index=" + G[i]);
		}
		for(int i=0;i<N;i++) X[i]=G[i].parent;
		V temp;
		for(int i=0;i<N;i++) {
			while(X[i]!=(temp=X[X[i].index])) X[i]=temp;
			if (X[i] != root) return false;
		}
		return true;
	}
	static SpanF graph;
	static boolean reporting = false;
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
			System.out.println("Usage: java SpanF <threads> [<N> [<Degree> [[false|true] [false|true]]]]");
			return;
		}
		Pool g = new Pool(procs);
		if (num >= 0) {
			Ns = new int[] {num};
		}
		for (int i=Ns.length-1; i >= 0; i--) {
			
			int N = Ns[i], M = D*N;
			// ensure the sole reference to the previous graph is nulled before gc.
			graph = null;
			System.gc();
			graph = new SpanF(N,M);
			if (graphOnly) return;
		
			//System.out.printf("N:%8d ", N);
			for (int k=0; k < 9; ++k) {
				graph.G[1].color=1;
				GloballyQuiescentVoidJob job = new GloballyQuiescentVoidJob(g, graph.G[1]);
				long s = System.nanoTime();
				g.invoke(job);
				long t = System.nanoTime() - s;
				double secs = ((double) t)/NPS;
				System.out.printf("N=%d t=%5.3f", N, secs);
				System.out.println();
				if (! graph.verifyTraverse(graph.G[1]))
					System.out.printf("%b ", false);
				graph.clearColor();
				
			}
			System.out.printf("Completed iterations for N=%d", N);
			System.out.println();
		}   
		g.shutdown(); 
	}
	void clearColor() {
		for (int i = 0; i < N; ++i) {
			graph.G[i].color=0;
		}
	}
}


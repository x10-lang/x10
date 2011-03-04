

/**
 * (c) IBM Corporation 2007
 * Author: Guojing Cong
 * Tong Wen
 * Vijay Saraswat
 * 
 * Iterative version of main loop.
 */

import java.util.concurrent.atomic.AtomicIntegerArray;

import x10.runtime.cws.Closure;
import x10.runtime.cws.Frame;
import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentJob;

public class SpanCI {
	
	class V {
		public int parent;
		public int degree;
		public int [] neighbors;
		public V(){}
		public String toString() { 
			String s="[" + (neighbors.length==0? "]" : "" + neighbors[0]);
			for (int i=1; i < neighbors.length; i++) s += ","+neighbors[i];
			return "v(parent=" + parent + ",degree="+degree+ ",n=" + s+"])";}
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
	AtomicIntegerArray color;
	int ncomps=0;
	
	static int[] Ns = new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
	int N, M;
	
	public SpanCI (int n, int m){
		N=n;
		M=m;
		
		/*constructing edges*/
		El = new E [M];
		double[] seeds = new double[] {
				0.21699440277541715, 0.9099040926714322, 0.5586793832519766,
				0.15656203486110076, 0.3716929310972751, 0.6327328452004045,
				0.9854204833301402, 0.8671652950975213, 0.1079976151083556,
				0.5993517714916581
		};
		El = new E [M];
		int r0 = 0;
		int r1 = 1;
		for(int i=0;i<M;i++){
			El[i]=new 
			E ((int) (seeds[r0]*(N+i))%N, (int) (seeds[r1]*(N+i))%N);
			r0 = r1;
			if (++r1 >= 10) r1 = 0;
			
		}
		
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
		
		color = new AtomicIntegerArray(N);
		int[] stack = new int [N]; 
		int[] connected_comps  = new int [N]; 
		
		int top=-1;
		ncomps=0;
		
		for(int i=0;i<N ;i++) {
			if (color.get(i) ==1) continue;
			connected_comps[ncomps++]=i;
			stack[++top]=i;
			color.set(i,1);
			while(top!=-1) {
				int v = stack[top];
				top--;
				
				for(int j=0;j<D[v];j++) {
					final int mm = NB[v][j];
					if(color.get(mm)==0){
						top++;
						stack[top]=mm;
						color.set(mm,1);
					}
				}
			}
		}
		
		//System.out.println("ncomps="+ncomps);
		El1 = new E [m+ncomps-1]; 
		
		for(int i=0;i<N;i++) color.set(i,0);
		
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
		
		G = new V[N];
		for(int i=0;i<N;i++) {
			G[i]=new V();
			G[i].degree=D[i];
			G[i].parent = i;
			G[i].neighbors=new int [D[i]];
			for(j=0;j<D[i];j++) {
				G[i].neighbors[j]=NB[i][j];
				//System.out.println("G[" + i + "]=" + G[i] + "-->"+ G[i].neighbors[j]);
			}
		}     
	}
	
	public  class TFrame extends Frame {
		int u; // vertex
		volatile int k;
		public TFrame(int u) {
			this.u=u;
		}
		public void setOutlet(final Closure c) {
			// nothing to do
		}
		public Closure makeClosure() {
			return new Traverser(this);
		}
		public String toString() { return "SpanCI(" + "u=" + u + ",k=" + k+")";}
	}

	 void traverseBody(final Worker w,  TFrame frame) throws StealAbort {
		final V[] g = G;
		final AtomicIntegerArray c = color;
		final int baseU = frame.u;
		for (;;) {
			final int u = frame.u;
			final int k = frame.k-1;
			final int deg = g[u].degree;
			if (k==g[u].degree) {
				w.popFrame();
				w.abortOnSteal();
				if (u==baseU) break;
				frame = (TFrame) w.cache.currentFrame();
			} else {
				final int v = g[u].neighbors[k];
				boolean result = c.get(v) == 0 && c.compareAndSet(v,0,1);
				frame.k++;
				if (result) {
					g[v].parent=u;
					if (k+1==deg) {
						frame.u=v;
						frame.k=1;
					} else {
						TFrame f = new TFrame(v);
						f.k=1;
						w.pushFrame(f);
						frame=f;
					}
				}
			}	
		}
	}
	 void traverse(final Worker w, final int uu) throws StealAbort {
		 TFrame frame = new TFrame(uu);
			frame.k=1;
			w.pushFrame(frame);
			traverseBody(w, frame);
	 }
	public class Traverser extends Closure {
		public Traverser( TFrame t) { 
			super(t);
		}
		public boolean requiresGlobalQuiescence() { return true; }
		public String toString() { return "T("+ frame + ")";}
		public void compute(Worker w, Frame ff) throws StealAbort {
			traverseBody(w, (TFrame) ff);
			setupGQReturnNoArg();
			return;
		}
	}
	boolean verifyTraverse(int root) {
		int[] X = new int [N];
		for(int i=0;i<N;i++) X[i]=G[i].parent;
		for(int i=0;i<N;i++) while(X[i]!=X[X[i]]) X[i]=X[X[i]];
		for(int i=0;i<N;i++) {
			
			if(X[i]!=root)  
				return false;
		}
		return true;
	}
	static SpanCI graph;
	static final long NPS = (1000L * 1000 * 1000);
	public static void main(String[] args) {
		int procs;
		try {
			procs = Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
		}
		catch (Exception e) {
			System.out.println("Usage: java SpanT <threads>");
			return;
		}
		Pool g = new Pool(procs);
		for (int i=0; i < Ns.length; i++) {
			
			int N = Ns[i], M = 3*N/5;
			graph = new SpanCI(N,M);
			System.gc();
			System.out.printf("N:%8d ", N);
			for (int k=0; k < 9; ++k) {
				GloballyQuiescentJob job = new GloballyQuiescentJob(g) {
					@Override
					protected void compute(Worker w, Frame frame) throws StealAbort {
						GFrame f = (GFrame) frame;
						int PC = f.PC;
						f.PC=LABEL_1;
						if (PC==0) {
							graph.color.set(1,1);
							graph.traverse(w, 1);
							//w.abortOnSteal();
						}
						setupGQReturnNoArg();
					}
					@Override
					public int spawnTask(Worker ws) throws StealAbort { 
						return 0;
					}
					public String toString() { 
						return "GJob(SpanC,#" + hashCode() + ",status=" + status + ",frame=" + frame + ")";}
				};
				
				
				long s = System.nanoTime();
				g.submit(job);
				try {
					job.waitForCompletion();
				} catch (InterruptedException z) {}
				long t = System.nanoTime() - s;
				double secs = ((double) t)/NPS;
				System.out.printf("%7.3f %b",secs, graph.verifyTraverse(1));
				graph.clearColor();
				
			}
			System.out.println();
		}   
		g.shutdown(); 
	}
	void clearColor() {
		int n = color.length();
		for (int i = 0; i < n; ++i) 
			color.set(i, 0);
	}
}




/**
 * (c) IBM Corporation 2007
 * Author: Guojing Cong
 * Tong Wen
 * Vijay Saraswat
 * 
 * Iterative version of main loop.
 */

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import x10.runtime.cws.Cache;
import x10.runtime.cws.Closure;
import x10.runtime.cws.Frame;
import x10.runtime.cws.Job;
import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentJob;

public class SpanCID {
	
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
	
	public SpanCID (int n, int m){
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
		//visited = new boolean[N];
		for(int i=0;i<N;i++) {
			G[i]=new V();
			G[i].degree=D[i];
			G[i].parent = i;
			G[i].neighbors=new int [D[i]];
			
			for(j=0;j<D[i];j++) {
				G[i].neighbors[j]=NB[i][j];
			}
			if (graphOnly)
				System.out.println(G[i]);
			
		}     
		
	}
	
	public  static class TFrame extends Frame {
		int u; // vertex
		public TFrame(int u) {
			this.u=u;
		}
		@Override
		public void setInt(int x) {
			u=x;
		}
		public void setOutlet(final Closure c) {
			assert false;
		}
		public Closure makeClosure() {
			return new Traverser(this);
		}
		public String toString() { return "n(" + u+ ")";}
	}

	void work(final Worker w, final int u) {
		final V[] g = G;
		final AtomicIntegerArray c = color;
		int index = u;
		for (;;) {
			final int degree = g[index].degree;
			int lastV = -1;
			for (int k=0; k < degree; k++) {
				final int v = g[index].neighbors[k];
				if (c.get(v)==0 && c.compareAndSet(v,0,1)) {
					g[v].parent=index;
					if (lastV >= 0) {
						w.pushIntUpdatingInPlace(lastV);
						if (reporting)
							System.out.println(w + " sets stack[" + (w.cache.tail()-1) +"]="+ lastV 
									+ " yielding " + w.cache.dump());
					}
					lastV =v;
				}
			}
			if ((index=lastV) < 0) 
				return;
		}
	}
	void traverseNode(final Worker w, final int u) throws StealAbort {
		int index = u;	
		Cache cache = w.cache;
		for(;;) {
			if (index >= 0) 
				work(w, index);
			Frame f = cache.popAndReturnFrame(w);
			if (f == null) return;
			if (reporting)
				System.out.println(w + " pops stack[" + (w.cache.tail()) + "]=" + f + " yielding " + w.cache.dump());
			index = ((TFrame) f).u;
		}
	}
	public static class Traverser extends Closure {
		public Traverser(  TFrame t) { 
			super(t);
		}
		public boolean requiresGlobalQuiescence() { return true; }
		public String toString() { return "T("+ frame + ",status=" + status + ")";}
		public void compute(Worker w, Frame ff) throws StealAbort {
			assert (w.cache.currentFrame()==ff);
			assert(w.cache.tail()==1);
			assert(w.cache.head()==0);
			TFrame tf = (TFrame) ff;
			int index = tf.u;
			if (index >= 0) {
				tf.u=-1; // in case this frame gets stolen. -1 indicates no more work to do.
				graph.traverseNode(w, index);
			}
			setupGQReturnNoArg();
			return;
		}
	}
	boolean verifyTraverse(int root) {
		int[] X = new int [N];
		for (int i=0;i<N;i++) 
				if (G[i].parent==i && i!=1) 
					System.out.println("Questionable guy " + i);
		for(int i=0;i<N;i++) X[i]=G[i].parent;
		for(int i=0;i<N;i++) while(X[i]!=X[X[i]]) X[i]=X[X[i]];
		for(int i=0;i<N;i++) {
			
			if(X[i]!=root)  
				return false;
		}
		return true;
	}
	static SpanCID graph;
	static boolean reporting = false;
	static final long NPS = (1000L * 1000 * 1000);
	static boolean graphOnly =false;
	public static void main(String[] args) {
		int procs;
		int num=-1;
		try {
			procs = Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
			if (args.length > 1) {
				num = Integer.parseInt(args[1]);
				System.out.println("N=" + num);
			}
			if (args.length > 2) {
				boolean b = Boolean.parseBoolean(args[2]);
				reporting=b;
			}
			if (args.length > 3) {
				boolean b = Boolean.parseBoolean(args[3]);
				graphOnly=b;
			}
		}
		catch (Exception e) {
			System.out.println("Usage: java SpanCID <threads> [<N>] [false|true] [false|true]");
			return;
		}
		Pool g = new Pool(procs);
		g.initFrameGenerator(new Worker.FrameGenerator() {
			public Frame make() {
				return new TFrame(0);
			}
		});
		if (num >= 0) {
			Ns = new int[] {num};
		}
		for (int i=0; i < Ns.length; i++) {
			
			int N = Ns[Ns.length-i-1], M = 3*N/5;
			graph = new SpanCID(N,M);
			if (graphOnly) return;
			System.gc();
			System.out.printf("N:%8d ", N);
			for (int k=0; k < 20; ++k) {
				graph.color.set(1,1);
				GloballyQuiescentJob job = new GloballyQuiescentJob(g, new TFrame(1)) {
					@Override
					protected void compute(Worker w, Frame frame) throws StealAbort {
						TFrame f = (TFrame) frame;
						int u = f.u;
						if (u >0) {
							f.u=-1;
							graph.traverseNode(w, u);
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
				System.out.printf("%7.3f ",secs);
				if (! graph.verifyTraverse(1))
					System.out.printf("%b ", false);
				graph.clearColor();
				
			}
			System.out.println();
		}   
		g.shutdown(); 
	}
	void clearColor() {
		int n = color.length();
		for (int i = 0; i < n; ++i) {
			color.set(i, 0);
		}
	}
}


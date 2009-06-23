/**
 * (c) IBM Corporation 2007
 * Author: Vijay Saraswat
 * 
 * 
 * Single-place spanning tree program based on breadth-first search,
 */

public class BFS(clock c) {
	
	 const long NPS = (1000L * 1000 * 1000);
	 int randSeed = 17673573; 
	 int rand32() { return randSeed = 1664525 * randSeed + 1013904223;}
	 final boolean reporting, graphOnly;
	 
	public class V  {
		public final int index;
		public nullable<V> parent;
		public V [] neighbors;
		public V(int i){index=i;}
		public void compute()  {
			for (int k=0; k < neighbors.length; k++) {
				final V v = neighbors[k];
				atomic v.parent= (v.parent==null?this:v.parent);
				if (v.parent==this)
					next async clocked (c) { v.compute();}
			}	
		}
		public void computeTree() {finish compute();}
		public boolean verify(V root) {
			boolean result = false;
			try { 
				nullable<V> p = parent;
				if (p == null) return result;
				while (p !=root) {
					if (p==null) return result;
					p=p.parent;
				}
			return result = true;
			} finally {
				if ((BFS.this.reporting) && ! result)
					System.out.println(Thread.currentThread() + " finds bad guy " + this);
			}
		}
		public String toString() {
			String s="[" + (neighbors.length==0? "]" : "" + neighbors[0].index);
			for (int i=1; i < neighbors.length; i++) s += ","+neighbors[i].index;
			return "v(" + index + ",degree="+ neighbors.length + ",n=" + s+"])";
		}
	}
	
	class E {
		public int v1,v2;
		public boolean inTree;
		public E(int u1, int u2){ v1=u1;v2=u2;inTree=false;}
	}
	
	V[] G;
	E[] El1;
	
	void clearParents() {
		for (int i = 0; i < N; ++i) G[i].parent=null;
	}
	int N, M;
	public BFS (int n, int m, boolean reporting, boolean graphOnly){
		property(clock.factory.clock());
		N=n; M=m;
		this.reporting=reporting; this.graphOnly=graphOnly;
		/*constructing edges*/
		G = new V[N]; for(int i=0;i<N;i++) G[i]=new V(i);
		E[] El = new E [M];
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
			boolean r=false;
			E edge = El[i];
			int s = edge.v1, e = edge.v2;
			/* filtering out repeated edges*/
			for(int j=0;j<D[s] && !r ;j++) if(e==NB[s][j]) r=true;
			if(r){
				edge.v1=edge.v2=-1; /*mark as repeat*/
			} else {
				m++;
				NB[s][D[s]++]=e;
				NB[e][D[e]++]=s;
			}
		}  
		
		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		//visitCount = new AtomicIntegerArray(N);
		int[] stack = new int [N], CC  = new int [N], level = new int[N];
		for (int i=0; i < N; i++) level[i]=-1;
		int top=-1;
		int ncomps=0;
		for(int i=0;i<N ;i++) {
			if (level[i]==1) continue;
			CC[ncomps++]=i;
			stack[++top]=i;
			level[i]=1;
			while(top!=-1) {
				int v = stack[top];
				top--;
				
				for(int j=0;j<D[v];j++) {
					final int mm = NB[v][j];
					if(level[mm] !=1){
						top++;
						stack[top]=mm;
						level[mm]=1;
					}
				}
			}
		}
		
		if (BFS.this.reporting && BFS.this.graphOnly) 
			System.out.println("ncomps="+ncomps);
		El1 = new E [m+ncomps-1]; 
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(El[i].v1!=-1) El1[j++]=El[i]; 
		
		if (BFS.this.reporting && BFS.this.graphOnly) 
			System.out.println( (j!=m)? "Remove duplicates failed"
					:"Remove duplicates succeeded,j=m="+j);
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			NB[CC[i]][D[CC[i]]++]=CC[i+1];
			NB[CC[i+1]][D[CC[i+1]]++]=CC[i];
			El1[i+m]=new E (CC[i], CC[i+1]);
		}
		for(int i=0;i<N;i++) {
			G[i].neighbors=new V [D[i]];
			for(j=0;j<D[i];j++) G[i].neighbors[j]=G[NB[i][j]];
			if (BFS.this.reporting || BFS.this.graphOnly)
				System.out.println("G[" + i + "]=" + G[i]);
		}     
	}
	
	boolean verifyTraverse(V root) {
		boolean result = true;
		for (int i=0;i<N  ;i++) if (! G[i].verify(root)) result = false;
		return result;
	}
	
	public static void main(String[] args) {
		int num=-1, procs =1, D=4;
		boolean reporting = false, graphOnly =false;
		try {
			procs = java.lang.Integer.parseInt(args[0]);
			System.out.println("P=" + procs);
			if (args.length > 1) {
				num = java.lang.Integer.parseInt(args[1]);
				System.out.println("N=" + num);
			}
			if (args.length > 2) {
				D = java.lang.Integer.parseInt(args[2]);
				System.out.println("D=" + D);
			}
			if (args.length > 3) {
				boolean b = java.lang.Boolean.parseBoolean(args[3]);
				reporting=b;
			}
			if (args.length > 4) {
				boolean b = java.lang.Boolean.parseBoolean(args[4]);
				graphOnly=b;
			}
		}
		catch (x10.lang.Exception e) {
			System.out.println("Usage: x10 BFS <P>  [<N> [<Degree> [[false|true] [false|true]]]]");
			return;
		}
		int[] Ns = num >=0 ?
				new int[]{num}
				: new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
		
		for (int i=Ns.length-1; i >= 0; i--) {
			
			System.gc();
			int N = Ns[i], M = D*N;
			BFS graph = new BFS(N,M, reporting, graphOnly);
			if (graphOnly) return;
			for (int k=0; k < 10; ++k) {
				long s = System.nanoTime();
				final V root = graph.G[1];
				root.parent=root;
				root.computeTree();
				long t = System.nanoTime() - s;
				double secs = ((double) t)/NPS;
				System.out.println("N=" + N + " t=" + secs);
				if (! graph.verifyTraverse(root))
					System.out.println("Test fails.");
				graph.clearParents();
			}
			System.out.println("Completed iterations for N="+N);
			
		}   
		
	}
	
}


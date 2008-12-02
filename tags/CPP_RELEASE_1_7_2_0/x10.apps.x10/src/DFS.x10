
/**
 * (c) IBM Corporation 2007
 * Author: Guojing Cong
 * Tong Wen
 * Vijay Saraswat
 * 
 * Iterative version of main loop. Uses the frame directly to represent tasks. Edges are
 * represented as references to vertices, not their indices.
 */

public class DFS {
	
	public class V {
		public final int index;
		public nullable<V> parent;
		public V [] neighbors;
		public V(int i){index=i;}
		
		public void compute()  {
			nullable<V> node = this;
			for (;;) {
				nullable<V> lastV = null;
				for (int k=0; k < node.neighbors.length; k++) {
					final V v = node.neighbors[k];
					atomic {
						v.parent=v.parent==null?node : v.parent;
					}
					if (v.parent==node) {
						if (lastV != null) {
							final nullable<V> target=lastV;
							async target.compute();
						} else { 
							lastV =v;
						}
					}
				}
				if ((node=lastV)==null) break;
			}
		}
		public void computeTree() {
			finish compute();
		}
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
				if ((DFS.this.reporting) && ! result)
					System.out.println(Thread.currentThread() + " finds bad guy " + this);
			}
		}
		
		public String toString() {
			String s="[" + (neighbors.length==0? "]" : "" + neighbors[0].index);
			for (int i=1; i < neighbors.length; i++) s += ","+neighbors[i].index;
			return "v(" + index +  ",parent=" + parent.index +  ",n=" + s+"])";
		}
	}
	
	class E{
		public int v1,v2;
		public boolean in_tree;
		public E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
	}
	
	V[] G;
	E[] El1;
	boolean reporting, graphOnly;
	int N, M;
	
	int randSeed = 17673573; 
	int rand32() { return randSeed = 1664525 * randSeed + 1013904223;}
	public DFS (int n, int m, boolean reporting, boolean graphOnly){
		N=n;
		M=m;
		this.reporting=reporting; this.graphOnly=graphOnly;
	
		
		/*constructing edges*/
		E[] El = new E [M];
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
		int[] stack = new int [N], CC  = new int [N], level = new int[N];
		
		int top=-1, ncomps=0;
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
					if(level[mm]==0){
						top++;
						stack[top]=mm;
						level[mm]=1;
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
			NB[CC[i]][D[CC[i]]++]=CC[i+1];
			NB[CC[i+1]][D[CC[i+1]]++]=CC[i];
			El1[i+m]=new E (CC[i], CC[i+1]);
		}
		
		//visited = new boolean[N];
		
		for(int i=0;i<N;i++) {
			G[i].parent = null;
			G[i].neighbors=new V [D[i]];
			for(j=0;j<D[i];j++) {
				G[i].neighbors[j]=G[NB[i][j]];
				
			}
			if (graphOnly)
				System.out.println("G[" + i + "]=" + G[i]);
		}     
	}
	
	
	boolean verifyTraverse(V root) {
		boolean result = true;
		for (int i=0;i<N  ;i++) if (! G[i].verify(root)) result = false;
		return result;
	}
	

	static final long NPS = (1000L * 1000 * 1000);

	public static void main(String[] args) {
		int procs;
		int num=-1;
		int D=4;
		boolean reporting=false, graphOnly = false;
		try {
			procs = java.lang.Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
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
		catch (java.lang.Exception e) {
			System.out.println("Usage: java SpanF <threads> [<N> [<Degree> [[false|true] [false|true]]]]");
			return;
		}
	
		int[] Ns = (num >=0) ?
				new int[] {num}
			: new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
		
		for (int i=Ns.length-1; i >= 0; i--) {
			
			int N = Ns[i], M = D*N;
			// ensure the sole reference to the previous graph is nulled before gc.
			DFS graph = new DFS(N,M,reporting, graphOnly);
			if (graphOnly) return;
		
			//System.out.printf("N:%8d ", N);
			for (int k=0; k < 9; ++k) {
				final V root = graph.G[1];
				root.parent=root;
				long s = System.nanoTime();
				root.computeTree();
				long t = System.nanoTime() - s;
				double secs = ((double) t)/NPS;
				System.out.println("N=" + N + " t="+secs);
				System.out.println();
				if (! graph.verifyTraverse(root))
					System.out.println("Test failed.");
				graph.clearParent();
				
			}
			System.out.println("Completed iterations for N="+N);
			
		}   
		
	}
	void clearParent() {
		for (int i = 0; i < N; ++i) {
			G[i].parent=null;
		}
	}
}


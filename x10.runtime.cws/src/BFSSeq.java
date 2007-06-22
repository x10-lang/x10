



/**
 * (c) IBM Corporation 2007
 * Author: Vijay Saraswat
 * 
 * 
 * A purely sequential breadth-first search, with no parallellism associated overhead. 
 * Intended to be used as the basis for speedup numbers.
 */

import java.util.Set;
import java.util.TreeSet;


public class BFSSeq {
	
	public class V  implements Comparable {
		public final int index;
		public int level=-1;
		public int degree;
		public V parent;
		public V [] neighbors;
		public V(int i){index=i;}
		
		public void compute(int l, Set<V> a)  {
			assert level >=0;
			if (reporting)
				System.out.println(Thread.currentThread() + " visits " + this);
						
			for (int k=0; k < degree; k++) {
				final V v = neighbors[k];
				if (v.level < 0) {
					v.level = l;
					v.parent=this;
					a.add(v);
					if (reporting)
						System.out.println(Thread.currentThread() + " marks " + v + " at ply " + l) ;
				}
			}	
		}
		
		public int compareTo(Object o) {
			assert o instanceof V;
			V other = (V) o;
			return index  - other.index;
		}
		public void computeBFS() {
			Set<V> nodes = new TreeSet<V>();
			level = 1;
			parent=this;
			
			nodes.add(this);
			int l=1;
			
			while (! nodes.isEmpty()) {
				Set<V> next = new TreeSet<V>();
				for( V node : nodes) node.compute(l,next);
				nodes=next;
				l++;
				if (reporting)
					System.out.println(Thread.currentThread() + " moves to level " + level);
			}
		}
		public boolean verify(V root) {
			boolean result = false;
			try { if (level < 0) return result;
			V p = parent;
			for (int i=0; i < level; i++) {
				if (p==null) return result;
				p=p.parent;
			}
			return result = (p==root);
			} finally {
				if (reporting && ! result)
					System.out.println(Thread.currentThread() + " finds bad guy " + this);
			}
		}
		@Override
		public String toString() {
			String s="[" + (neighbors.length==0? "]" : "" + neighbors[0].index);
			for (int i=1; i < neighbors.length; i++) s += ","+neighbors[i].index;
			return "v(" + index + ",level=" + level + ",degree="+degree+ ",n=" + s+"])";
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
	
	//AtomicIntegerArray visitCount ;
	int ncomps=0;
	
	static int[] Ns = new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
	int N, M;
	
	int randSeed = 17673573; 
	int rand32() { return randSeed = 1664525 * randSeed + 1013904223;}
	public BFSSeq (int n, int m){
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
		int[] connected_comps  = new int [N]; 
		
		int top=-1;
		ncomps=0;
		for(int i=0;i<N ;i++) {
			if (G[i].level==1) continue;
			connected_comps[ncomps++]=i;
			stack[++top]=i;
			G[i].level=1;
			while(top!=-1) {
				int v = stack[top];
				top--;
				
				for(int j=0;j<D[v];j++) {
					final int mm = NB[v][j];
					if(G[mm].level==0){
						top++;
						stack[top]=mm;
						G[mm].level=1;
					}
				}
			}
		}
		
		if (reporting && graphOnly) System.out.println("ncomps="+ncomps);
		El1 = new E [m+ncomps-1]; 
		
		
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(El[i].v1!=-1) El1[j++]=El[i]; 
		
		if (reporting && graphOnly) 
			if(j!=m) 
				System.out.println("Remove duplicates failed");
			else System.out.println("Remove duplicates succeeded,j=m="+j);
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			NB[connected_comps[i]][D[connected_comps[i]]++]=connected_comps[i+1];
			NB[connected_comps[i+1]][D[connected_comps[i+1]]++]=connected_comps[i];
			El1[i+m]=new E (connected_comps[i], connected_comps[i+1]);
		}
		
		//visited = new boolean[N];
		
		for(int i=0;i<N;i++) {
			G[i].degree=D[i];
			G[i].level=-1;
			G[i].neighbors=new V [D[i]];
			for(j=0;j<D[i];j++) {
				G[i].neighbors[j]=G[NB[i][j]];
				
			}
			if (reporting || graphOnly)
				System.out.println("G[" + i + "]=" + G[i]);
		}     
	}
	
	
	boolean verifyTraverse(V root) {
		int i=0;
		boolean result = false;
		try {
			for (;i<N && G[i].verify(root);i++) ;
			return result = (i==N);
		}finally {
			if (reporting && ! result)
				System.out.println(Thread.currentThread() + " fails at " + G[i]);
		}
	}
	static BFSSeq graph;
	static boolean reporting = false;
	static final long NPS = (1000L * 1000 * 1000);
	static boolean graphOnly =false;
	public static void main(String[] args) {
		int num=-1;
		int D=4;
		try {
			
			if (args.length > 0) {
				num = Integer.parseInt(args[0]);
				System.out.println("N=" + num);
			}
			if (args.length > 1) {
				D = Integer.parseInt(args[1]);
				System.out.println("D=" + D);
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
			System.out.println("Usage: java BFSSeq  [<N> [<Degree> [[false|true] [false|true]]]]");
			return;
		}
		
		if (num >= 0) {
			Ns = new int[] {num};
		}
		for (int i=Ns.length-1; i >= 0; i--) {
			
			int N = Ns[i], M = D*N;
			// ensure the sole reference to the previous graph is nulled before gc.
			graph = null;
			System.gc();
			graph = new BFSSeq(N,M);
			if (graphOnly) return;
		
			//System.out.printf("N:%8d ", N);
			for (int k=0; k < 9; ++k) {
				long s = System.nanoTime();
				graph.G[1].computeBFS();
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
		
	}
	void clearColor() {
		for (int i = 0; i < N; ++i) {
			V v= graph.G[i];
			v.level=-1;
			v.parent=null;
		}
	}
}


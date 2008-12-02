/**
 * (c) IBM Corporation 2007
 * Author: Vijay Saraswat
 * 
 * 
 * Shiloach Vishkin algorithm.
 */


public class SV {
	
	class E{
		public int v1,v2;
		public boolean inTree;
		public E(int u1, int u2){ v1=u1;v2=u2;inTree=false;}
		public String toString() { return v1 + "--" + v2;}
	}
	
	final E[] El1;
	int N=1000, M=4000; //N is the number of vertexes and M is the number of edges

	int randSeed = 17673573; 
	int rand32() { return randSeed = 1664525 * randSeed + 1013904223;}
	static void print(int[] a, String prefix) { 
		  System.out.print(prefix + "={"); 
		  if (a.length > 0) {
			  System.out.print(a[0]); 
			  for (int i=1; i < a.length; i++) System.out.print(","+a[i]);
		  }
		  System.out.println("}"); 
		}
	public SV (int n, int m, boolean reporting, boolean graphOnly){
		N=n;
		M=m;
		this.reporting=reporting; this.graphOnly=graphOnly;
		/*constructing edges*/
		
		E[] El = new E [M]; for (int i=0; i <M; i++) El[i] = new E(Math.abs(rand32())%N, Math.abs(rand32())%N);
		
		/* D[i] is the degree of vertex i (duplicate edges are counted).*/
		int[] D = new int [N];
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
		if (reporting || graphOnly) {
			System.out.println((m-1) + " edges.");
			for(int i=0;i<N;i++) {
				System.out.print(i + "-->");
				for (int j=0; j < D[i]; j++) System.out.print(NB[i][j] + " ");
				System.out.println();
			}     
		}
		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		//visitCount = new AtomicIntegerArray(N);
		int[] stack = new int [N]; 
		int[] cc  = new int [N]; 
		int[] level = new int[N];
		int top=-1;
		int ncomps=0;
		for(int i=0;i<N ;i++) {
			if (level[i]==1) continue;
			level[i]=1;
			cc[ncomps++]=stack[++top]=i;
			while(top!=-1) {
				int v = stack[top--];
				for(int j=0;j<D[v];j++) {
					final int mm = NB[v][j];
					if(level[mm] !=1){
						stack[++top]=mm;
						level[mm]=1;
					}
				}
			}
		}
		
		if (reporting) System.out.println("ncomps="+ncomps);
		El1 = new E [m+ncomps-1]; 
		
		
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(El[i].v1!=-1) El1[j++]=El[i]; 
		
		if (reporting) {
			if(j!=m) 
				System.out.println("Remove duplicates failed");
			else System.out.println("Remove duplicates succeeded,j=m="+j);
			
		}
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			int s=cc[i], e=cc[i+1];
			NB[s][D[s]++]=e;
			NB[e][D[e]++]=s;
			El1[m+i]=new E (e,s);
		}
	
	}
	boolean changed=true;
	class SVJob  {
		final int numWorkers;
		final clock clock;
		SVJob(int n) {
			numWorkers=n; 	
			clock = clock.factory.clock();
		}
		public String toString() {  return "SVJob(" + numWorkers+")";}
		public void compute(){
			final int [] D1 = new int [N]; for (int i = 0; i <N; i++) D1[i]= i;
			final long[] padding = new long[128];
			final int [] ID = new int [N]; for (int i = 0; i <N; i++) ID[i]= -1;
		
			final int localVertexSize = N/numWorkers;
			final int edgeSize =El1.length;
			final int localEdgeSize = edgeSize/numWorkers;
			class SVWorker {
				int j, vLow, vHigh, eLow, eHigh;
				
				SVWorker(int j) {
					this.j=j; 
					this.vLow=j*localVertexSize; 
					this.vHigh=(j+1)*localVertexSize-1;
					this.eLow = j*localEdgeSize;
					this.eHigh = (j+1)*localEdgeSize-1;
					if (j==numWorkers-1) {
						if (localEdgeSize*numWorkers != edgeSize) eHigh= edgeSize-1;
						if (localVertexSize*numWorkers !=N) vHigh = N-1;
					}
				}
				public String toString() { return "SVWorker " + j;}
				public void compute()   {
					boolean locallyChanged = true;
					while (locallyChanged) {
						locallyChanged = false;
						if (j==0) changed=false;
						for (int i=eLow; i <=eHigh; i++) {
							final int v1=El1[i].v1, 
							v2=El1[i].v2, 
							s=D1[v1], 
							e=D1[v2],
							ee=D1[e];
							if(s < e && e==ee) ID[e]=i;
							if(e < s && s==D1[s]) ID[s]=i;
						}
						next;
						for (int i=eLow; i <=eHigh; ++i) {
							final int v1=El1[i].v1, v2=El1[i].v2,s=D1[v1], e=D1[v2], ee=D1[e];
							if(s < e && e==ee && ID[e]==i) {
								D1[e]=s; 
								El1[i].inTree=locallyChanged=true; 
							}
							if(e < s && s==D1[s] && ID[s]==i) {
								D1[s]=e; 
								El1[i].inTree=locallyChanged=true; 
							}                        
						}
						if (locallyChanged) changed=true;
						next;
						//						locallyChanged = barrier.arriveAndAwaitData(locallyChanged); // global redn
						locallyChanged=changed;
						/*Make sure the labels of each group is the same.*/
						for (int i=vLow; i <=vHigh; i++) 
							while (D1[D1[i]]!=D1[i]) D1[i]=D1[D1[i]];
						next;

					} // while
							
				}
			}
			
			foreach (point [i] : [0:numWorkers-1]) clocked (clock) {
				new SVWorker(i).compute();
			}
		}
	}
	boolean verifySV() {
		int sum=0; for(E e : El1) if(e.inTree) sum++;
		if(sum<N-1){
			System.out.println("verifySV failed " + sum);
			return false;
		}
		return true;
	}


	const long NPS = (1000L * 1000 * 1000);
	
	public static void main(String[] args) {
		int num=-1;
		int procs =1;
		int D=4;
		boolean reporting=false,graphOnly=false;
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
		catch (java.lang.Exception e) {
			System.out.println("Usage: x10 SV <P>  [<N> [<Degree> [[false|true] [false|true]]]]");
			return;
		}
		
		int[] Ns = (num >=0) ? new int[] {num} : new int[] {1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
		
		for (int i=Ns.length-1; i >= 0; i--) {
			int N = Ns[i], M = D*N;
			SV graph = new SV(N,M, reporting, graphOnly);
			if (graphOnly) return;
			for (int k=0; k < 10; ++k) {
				long s = - System.nanoTime();
				graph.new SVJob().compute();
				//System.out.println(" ...done with " + job);
				s += System.nanoTime();
				double secs = ((double) s)/NPS;
				System.out.println("N="+N+" t=" + secs);
				if (! graph.verifySV())
					System.out.printf("Test failed.");
				graph.reset();
				
			}
			System.out.printf("Completed iterations for N=%d", N);
			System.out.println();
		}   
		
	}
	void reset() {
		for (int i = 0; i < El1.length; ++i) {
			El1[i].inTree = false;
		}
	}


}


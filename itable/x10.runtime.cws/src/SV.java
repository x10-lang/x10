/**
 * (c) IBM Corporation 2007
 * Author: Vijay Saraswat
 * 
 * 
 * Shiloach Vishkin algorithm.
 */


import x10.runtime.cws.Frame;
import x10.runtime.cws.Pool;
import x10.runtime.cws.StealAbort;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;

public class SV {
	
	class E{
		public int v1,v2;
		public boolean inTree;
		public E(int u1, int u2){ v1=u1;v2=u2;inTree=false;}
		public String toString() { return v1 + "--" + v2;}
	}
	
	int m;
	E[] El, El1;
	int ncomps=0;


	static int[] Ns = new int[] {10};//1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000, 5*1000*1000};
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
	public SV (int n, int m, char graphType){
		N=n;
		M=m;
		if (graphType=='E') {
			randomEdgeGraph();
		} else if (graphType=='T') {
			N=n*n;
			torusGraph(n);
		} else if (graphType=='K') {
			kGraph();
		} else if (graphType=='R') {
			
			//rGraph(G,El);
		}
	}
	public void torusGraph (int k){
		System.out.println("Generating graph...");
		int n = k*k,i,j,l,s;
		int [] buff = new int [n];
  		for(i=0;i<n;i++) buff[i]=i;

  		for(i=0;i<n/2;i++){
			l=(int)(Math.random()*n)%n;
 			s=(int)(Math.random()*n)%n;
			j=buff[l];
			buff[l]=buff[s];
			buff[s]=j;
  		}
	
  		El1 = new E[4*n];
  		int c=0;
  		for(i=0;i<k;i++)
      		for(j=0;j<k;j++) {
      			int v1= buff[i*k+j];
      			El1[c++] = new E(v1, buff[((k+i-1)%k)*k+j]);
      			El1[c++] = new E(v1, buff[((i+1)%k)*k+j]);
      			El1[c++] = new E(v1, buff[i*k+((k+j-1)%k)]);
      			El1[c++] = new E(v1, buff[i*k+((j+1)%k)]);
      		}
	 		
   		System.out.println("Graph generated.");
	}

	public void kGraph(){
		int n=N;
		int k=4;
		final int TIMES=5;
		final int THRESHOLD=100;
		int neighbor;
		char [] visited = new char [n];
		int [] stack = new  int [n];
		int [] SUPER = new int [n];
		int i,j,u,v,nextn,top=-1,n_comp=0,rep,s;
		int [] counter = new int [n];
		final int [][] array = new int [n][k*TIMES];

		for(i=0;i<n;i++){
			counter[i]=0;
			visited[i]=0;
		}

		for(i=0;i<n;i++){
			for(j=counter[i];j<k;j++){
				if(i<n-THRESHOLD)
					neighbor=(int)(Math.random()*(n-i))%(n-i)+i;
				else 
					neighbor=(int)(Math.random()*THRESHOLD)%(THRESHOLD);
				rep=0;
				for(s=0;s<counter[i];s++) 
					if(array[i][s]==neighbor) rep=1;
				while(rep==1){
					rep=0;
					if(i<n-THRESHOLD)
						neighbor=(int)(Math.random()*(n-i))%(n-i)+i;
					else 
						neighbor=(int)(Math.random()*THRESHOLD)%(THRESHOLD);
					for(s=0;s<counter[i];s++) 
						if(array[i][s]==neighbor) rep=1;
				}

				while(counter[neighbor]>TIMES*k-1 || neighbor==i) neighbor=(neighbor+1)%n;
				array[i][counter[i]]=neighbor;
				counter[i]++;
				array[neighbor][counter[neighbor]]=i;
				counter[neighbor]++;
			}
		}
	

  		/* now make the graph connected if it is not*/
  		for(i=0;i<n;i++){
      			if(visited[i]==0){
				visited[i]=1;
				stack[++top]=i;
				SUPER[n_comp++]=i;

				while(top!=-1){
	    				v = stack[top];
	    				top--;

	    				for (j=0; j<counter[v]; j++) {
	      					nextn = array[v][j];
	      					if(visited[nextn]==0) {  /* not seen yet */
							visited[nextn]=1;
							stack[++top]=nextn;
	      					}
	    				}
	  			}
      			}
    		}
 
  		for(i=1;i<n_comp;i++){
      			u = SUPER[i];
      			v = SUPER[i-1];
      			array[u][counter[u]++]=v;
      			array[v][counter[v]++]=u;
    		}

  		int count = 0;
  		for(i=0;i<n;i++) count += counter[i];
  		El1 = new E[count];
  		count = 0;
   		for(i=0;i<n;i++) for (j=0; j<counter[i];j++)
   			El1[count++] = new E(i,array[i][j]);
   		
   		
  		System.out.println("generating graph done\n");
	}

	void randomEdgeGraph() {
		El = new E[M];
		/*constructing edges*/
		for (int i=0; i <M; i++) El[i] = new E(Math.abs(rand32())%N, Math.abs(rand32())%N);
		
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
		ncomps=0;
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
	class SVJob extends Frame {
		final int numWorkers;
		volatile int PC=0; 
		SVJob(int n) {
			numWorkers=n; 	
			// System.out.println("Created SVJob " + numWorkers);
		}
		public String toString() {  return "SVJob(" + numWorkers+")";}
		public void compute(Worker w){
			// An improperly nested task.
			if (PC==1) {
				w.popFrame();
				return;
			}
			PC=1;
			final int [] D1 = new int [N]; for (int i = 0; i <N; i++) D1[i]= i;
			final long[] padding = new long[128];
			final int [] ID = new int [N]; for (int i = 0; i <N; i++) ID[i]= -1;
		
			final int localVertexSize = N/numWorkers;
			final int edgeSize =El1.length;
			final int localEdgeSize = edgeSize/numWorkers;
			
			final TaskBarrier barrier = new TaskBarrier() { public String name() { return "barrier";}};
			class SVWorker extends Frame {
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
				public void compute(Worker w)  throws StealAbort {
					// this is a properly nested task. 
					// There are at least two schemes to implement the global reduction for changed.
					// (1) Use a shared variable, changed (does not need to be volatile).
					// It is reset by proc 0 at the beginning of
					// each phase. During a phase it is set by any proc that wishes to set it.
					// Existing barriers (arriveAndAwait) will ensure that the change is seen by
					// others when they read changed.
					// (2) Use a reduction, barrier.arriveAndAwaitData.
					// I have a suspicion (1) will be faster.
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
						barrier.arriveAndAwait(); // changed is now reset.
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
						barrier.arriveAndAwait();
						//						locallyChanged = barrier.arriveAndAwaitData(locallyChanged); // global redn
						locallyChanged=changed;
						/*Make sure the labels of each group is the same.*/
						for (int i=vLow; i <=vHigh; i++) 
							while (D1[D1[i]]!=D1[i]) D1[i]=D1[D1[i]];
						barrier.arriveAndAwait(); // will reflect the updates to changed, if any.

					} // while
					barrier.arriveAndDeregister();
					w.popFrame();
							
				}
			}
			
			for (int i=0; i < numWorkers; i++) {
				barrier.register(); 
				// this is not a recursive call, it just pushes the frame.
				w.pushFrame(new SVWorker(i));
			}
			//System.out.println(w + " ends " + this);
			// return, must not w.popFrame() because some other frames are on the stack now.
			// when those are executed to completion, this frame will be left on the dequeue with PC=1.
			// a normal execution of it by this worker will result in the frame being popped...see 
			// the code at the beginning of this method.
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

	static boolean reporting = false;
	static final long NPS = (1000L * 1000 * 1000);
	static boolean graphOnly =false;
	public static void main(String[] args) {
		int procs;
		int num=-1;
		int D=4;
		char graphType='E';
		try {
			procs = Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
			if (args.length > 1) {
				String s = args[1];
				if (s.equalsIgnoreCase("T")) {
					graphType='T'; 
				}
				else if (s.equalsIgnoreCase("R"))
					graphType='R'; 
				else if (s.equalsIgnoreCase("K"))
					graphType='K'; 
				System.out.println("graphType=" + graphType);
			}
			if (args.length > 2) {
				num = Integer.parseInt(args[2]);
				System.out.println("N=" + num);
			}
			
			if (args.length > 3) {
				D = Integer.parseInt(args[3]);
				System.out.println("D=" + D);
			}
			if (args.length > 4) {
				boolean b = Boolean.parseBoolean(args[4]);
				reporting=b;
			}
			if (args.length > 5) {
				boolean b = Boolean.parseBoolean(args[5]);
				graphOnly=b;
			}
		}
		catch (Exception e) {
			System.out.println("Usage: java SV <threads> [<Type> [<N> [<Degree> [[false|true] [false|true]]]]]");
			return;
		}
		Pool g = new Pool(procs);
		if (num >= 0) {
			Ns = new int[] {num};
		}
		for (int i=Ns.length-1; i >= 0; i--) {
			int N = Ns[i], M = D*N;
			System.gc();
			SV graph = new SV(N,M, graphType);
			if (graphOnly) return;
		
			for (int k=0; k < 10; ++k) {
				long s = -System.nanoTime();
				GloballyQuiescentVoidJob job = 
					new GloballyQuiescentVoidJob(g, graph.new SVJob(procs));
				//System.out.println("Starting " + job);
				g.invoke(job);
				//System.out.println(" ...done with " + job);
				s += System.nanoTime();
				double MEps = (M*1000)/(double) s;
				System.out.printf("N=%d t=%d ns %5.3f GE/s", N, s, MEps);
				System.out.println();
				if (! graph.verifySV())
					System.out.printf("%b ", false);
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


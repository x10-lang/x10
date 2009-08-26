package graph;
/**
 * 
 * Random, K and Torus graph generators.
 * @author vj, Guojing Cong, Tong Wen 1/2008
 *
 */
public class GraphGenerator {
	

	public static void torusGraph (int k, final Vertex [] graph){
		System.out.println("Generating graph...");
		final int n = k*k;
        // buf[i*k+j] is the index of the (i,j)th vertex. We may scramble.
		int[] buff = new int [n]; for(int i=0;i<n;i++) buff[i]=i;
  		/*for(int i=0;i<n/2;i++){
			int l=(int)(Math.random()*n)%n, s=(int)(Math.random()*n)%n;
			int j=buff[l];
			buff[l]=buff[s];
			buff[s]=j;
  		}*/
  		for(int i=0;i<k;i++) for(int j=0;j<k;j++) {
      			int m = buff[i*k+j];
      			graph[m].initNeighbors(4);
      			graph[m].addEdge(graph[buff[((k+i-1)%k)*k+j]]);
      			graph[m].addEdge(graph[buff[((i+1)%k)*k+j]]);
      			graph[m].addEdge(graph[buff[i*k+((k+j-1)%k)]]);
      			graph[m].addEdge(graph[buff[i*k+((j+1)%k)]]);
      		}
   		System.out.println("Graph generated.");
	}
	
	public static void kGraph(final Vertex [] graph){
		final int n = graph.length;
		int k=4;
		final int TIMES=5;
		final int THRESHOLD=100;
		int neighbor;
		char [] visited = new char [n];
		int [] stack = new  int [n];
		int [] SUPER = new int [n];
		int u,v,nextn,top=-1,n_comp=0,rep,s;
		int [] counter = new int [n];
		final int [][] array = new int [n][k*TIMES];

		for(int i=0;i<n;i++){
			counter[i]=0;
			visited[i]=0;
		}

		for(int i=0;i<n;i++){
			for(int j=counter[i];j<k;j++){
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
		for(int i=0;i<n;i++){
			if(visited[i]==0){
				visited[i]=1;
				stack[++top]=i;
				SUPER[n_comp++]=i;

				while(top!=-1){
					v = stack[top];
					top--;

					for (int j=0; j<counter[v]; j++) {
						nextn = array[v][j];
						if(visited[nextn]==0) {  /* not seen yet */
							visited[nextn]=1;
							stack[++top]=nextn;
						}
					}
				}
			}
		}

		for(int i=1;i<n_comp;i++){
			u = SUPER[i];
			v = SUPER[i-1];
			array[u][counter[u]++]=v;
			array[v][counter[v]++]=u;
		}

		for(int i=0;i<n;i++){

			/*graph[i].self=i;*/
			//graph[i].parent=i;
			//graph[i].degree=counter[i];
			graph[i].initNeighbors(counter[i]);
			for(int j=0;j<counter[i];j++){
				graph[i].addEdge(graph[array[i][j]]);
			}
		}
		System.out.println("generating graph done\n");
	}
	static int randSeed = 17673573; 
	static int rand32() { return randSeed = 1664525 * randSeed + 1013904223;}
	static class E{
		public int v1,v2;
		public boolean in_tree;
		public E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
	}
	public static void randomEdgeGraph(final int M, final Vertex[] G) {
		int ncomps=0;
		E[] El = new E[M];
		final int N=G.length;
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

		int m=0;
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
		E[] El1 = new E [m+ncomps-1]; 


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
			G[i].reset();
			G[i].initNeighbors(D[i]);
			for(j=0;j<D[i];j++) G[i].addEdge(G[NB[i][j]]);
			
		}     
	}

	

}

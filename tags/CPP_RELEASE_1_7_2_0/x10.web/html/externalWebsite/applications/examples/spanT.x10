//package Tree;

class V {
	public int parent;
	public int degree;
	public int [] my_neighbors;
	public V(){}
}

class IntJavaArray{ 
	public int [] D1;
	public IntJavaArray(){}
}

class E{
	public int v1,v2;
	public boolean in_tree;
	public E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
}


public class spanT{
	const int N=1000,M=4000;
	int m;
	V [] G;
	E [] El;
	E [.] El1;
	int [] parent;
	int [] color;
	int [.] D1;
	int [.] ID;
	int ncomps=0;
	boolean changed=true;
	
	public spanT (){
		int i,j;
		int [] D,stack,connected_comps;
		IntJavaArray [] NB; /*NB[i].D1[j] stores the jth neighbor of vertex i*/
			
		System.out.println("graph construction begin 2----");
		/*constructing edges*/
		El = new E [M];
		for(i=0;i<M;i++){
			El[i]=new E ((int) (Math.random()*N)%N, (int) (Math.random()*N)%N);
			/*System.out.println("E="+El[i].v1+","+El[i].v2);*/
		}
		
		D = new int [N];
		stack = new int [N];
		
		/* D[i] is the degree of vertex i. It counts duplicated edges*/
		for(i=0;i<N;i++) D[i]=0;
		for(i=0;i<M;i++){
			D[El[i].v1]++;
			D[El[i].v2]++;
		}
		
		NB = new IntJavaArray [N];
	
		for(i=0;i<N;i++) {
			NB[i]=new IntJavaArray();
			NB[i].D1 =new int [D[i]+2]; /* leave room for making connected graph by +2*/
			for(j=0;j<D[i]+2;j++)
			{
				NB[i].D1[j]=0;
			}
		}
		
		/*Now D[i] is the index for storing the neighbors of vertex i into NB[i]
		  NB[i].D1[D[i]] is the current neighbor*/
		  
		for(i=0;i<N;i++) D[i]=0;
		
		m=0;
		for(i=0;i<M;i++)//for each edge
		{
			int r=0;    
			/* filtering out repeated edges*/
		        for(j=0;j<D[El[i].v1];j++){ 
		        	if(El[i].v2==NB[El[i].v1].D1[j]) {r=1; break;}
		        }
		        if(r==1){
		        	El[i].v1=El[i].v2=-1; /*mark as repeat*/
		        	continue;
		        }
		        m++;
		        NB[El[i].v1].D1[D[El[i].v1]]=El[i].v2;
		        NB[El[i].v2].D1[D[El[i].v2]]=El[i].v1;
		        D[El[i].v1]++;
		        D[El[i].v2]++;
		}  

		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		color = new int [N];
		stack = new int [N]; 
		connected_comps  = new int [N]; 

		for(i=0;i<N;i++)  color[i]=0;
		int top=-1;
		ncomps=0;
		
		for(i=0;i<N;i++)
		{
			int v;
			if(color[i]==1) continue;
			else connected_comps[ncomps++]=i;

			stack[++top]=i;
			color[i]=1;
			while(top!=-1) {
				v = stack[top];
				top--;

				for(j=0;j<D[v];j++)
					if(color[NB[v].D1[j]]==0){
						top++;
						stack[top]=NB[v].D1[j];
						color[NB[v].D1[j]]=1;
					}
			}
		}
		
		System.out.println("ncomps="+ncomps);
		El1 = new E [[0:m+ncomps-2]]; //should be -2
		for(i=0;i<N;i++) color[i]=0;	
		for(i=0,j=0;i<M;i++)
		{
			if(El[i].v1!=-1) El1[j++]=El[i]; //Remove duplicated edges
		}
		if(j!=m) System.out.println("Remove duplicates failed");
		else System.out.println("Remove duplicates succeeded,j=m="+j);
		
		 /*add edges between neighboring connected comps*/
		for(i=0;i<ncomps-1;i++)
		{
			NB[connected_comps[i]].D1[D[connected_comps[i]]++]=connected_comps[i+1];
			NB[connected_comps[i+1]].D1[D[connected_comps[i+1]]++]=connected_comps[i];
			El1[i+m]=new E (connected_comps[i], connected_comps[i+1]);
		}
		System.out.println("4=======+++++");
		
		
		G = new V[N];
		for(i=0;i<N;i++)
		{			
			G[i]=new V();
			G[i].degree=D[i];
			G[i].my_neighbors=new int [D[i]];
			for(j=0;j<D[i];j++)
				G[i].my_neighbors[j]=NB[i].D1[j];
		}
		System.out.println("graph construction done+++++----");		
	}
	
	public static void main(String[] args) {
		System.out.println("Initializing");
		new spanT().run(0);
		System.out.println("N="+N);

		System.out.println("Done===");
	    }
	 

	    void run(int root) {
	    	
	    	traverse(root);
	    	verify_traverse(root);
		System.out.println("parent[N-1]="+G[N-1].parent);
		System.out.println("traverse run done ....++++");
		
		SV(); 
		System.out.println("SV done successfully? "+verify_SV());
		resetTreeEdges();
		SV_twophase();
		System.out.println("SV_2phase done successfully? "+verify_SV());
	    } 
	    
	    void SV (){
	    	D1 = new int [[0:N-1]] (point [i]){ return i;};
	    	/*foreach(point [i]: D1) {
	    		D1[i]=i;
	    	}*/
	    	//System.out.println(" edge region="+El1.region);
	    	changed=true;
	    	while(changed){
	    		changed = false;
	    		finish foreach(point [i]: El1.region){
	    			
	    			async { atomic{
	    				if(D1[El1[i].v1] < D1[El1[i].v2] && D1[El1[i].v2]==D1[D1[El1[i].v2]]) {
	    					D1[D1[El1[i].v2]]=D1[El1[i].v1];
	    					changed=true;
	    					El1[i].in_tree=true;
	    				          
	    				}
	    			} }
	    			async { atomic {
	    				if(D1[El1[i].v2] < D1[El1[i].v1] && D1[El1[i].v1]==D1[D1[El1[i].v1]]) {
	    
	    					D1[D1[El1[i].v1]]=D1[El1[i].v2];
	    					changed=true;
	    					El1[i].in_tree=true;
	    				}
	    			} }
	    			/*atomic{
	    				if(D1[El1[i].v1] < D1[El1[i].v2]){
	    					if (D1[El1[i].v2]==D1[D1[El1[i].v2]]){
	    						D1[D1[El1[i].v2]]=D1[El1[i].v1];
		    					changed=true;
		    					El1[i].in_tree=true;
	    					}
	    				}
	    				else{
	    					if (D1[El1[i].v1]==D1[D1[El1[i].v1]]){
	    						D1[D1[El1[i].v1]]=D1[El1[i].v2];
		    					changed=true;
		    					El1[i].in_tree=true;
	    					}
	    				}
	    			}*/
	    		}
	    		finish foreach(point [i]:D1.region) {
	    			if(D1[D1[i]]!=D1[i]) D1[i]=D1[D1[i]];
	    		}
	    	}
	    }
	    

	    void SV_twophase (){
	    	D1 = new int [[0:N-1]] (point [i]){ return i;};
	    	ID = new int [[0:N-1]];
	    	foreach(point [i]: ID.region){
	    		ID[i]=-1;
	    	}
	    	
	    	System.out.println(" edge region="+El1.region);
	    	changed=true;
	    	while(changed){
	    		
	    		changed = false;
	    	
	    		finish foreach(point [i]: El1.region){
	    			if(D1[El1[i].v1] < D1[El1[i].v2] && D1[El1[i].v2]==D1[D1[El1[i].v2]]) {	
	    					ID[D1[El1[i].v2]]=i;
	    			}
	    				    			
	    			if(D1[El1[i].v2] < D1[El1[i].v1] && D1[El1[i].v1]==D1[D1[El1[i].v1]]) {
	    					
	    					ID[D1[El1[i].v1]]=i;
	    			}
	    		}
	    		
	    		finish foreach(point [i]: El1.region){
	    			if(D1[El1[i].v1] < D1[El1[i].v2] && D1[El1[i].v2]==D1[D1[El1[i].v2]] && ID[D1[El1[i].v2]]==i) {
    					D1[D1[El1[i].v2]]=D1[El1[i].v1];
    					El1[i].in_tree=true;
    					changed=true;
	    			}
	    			if(D1[El1[i].v2] < D1[El1[i].v1] && D1[El1[i].v1]==D1[D1[El1[i].v1]] && ID[D1[El1[i].v1]]==i) {
	    				D1[D1[El1[i].v1]]=D1[El1[i].v2];
	    			    	El1[i].in_tree=true;
	    			    	changed=true;
	    			}
	    		}
	    		finish foreach(point [i]:D1.region) {
	    			if(D1[D1[i]]!=D1[i]) D1[i]=D1[D1[i]];
	    		}
	    	}
	    }

	    void resetTreeEdges()
	    {
			foreach(point [i]: El1.region){
				El1[i].in_tree=false;
			}
			System.out.println("Tree edges are reset");
	    }
	    
	    boolean verify_SV()
	    {
	    	int i, sum=0;
	    	for(i=0;i<m+ncomps-1;i++) if(El1[i].in_tree) sum++;
	    	if(sum<N-1){
	    		System.out.println("verify_SV failed " + sum);
	    		return(false);
	    	}
	    	System.out.println("verify_SV succeeded");
	    	return(true);
	    }
	    
	    void traverse(int u) {
    		int k,v;
    		/*System.out.println("in traverse "+u);*/
	    	finish for(k=0;k<G[u].degree;k++)
    		{
    			v=G[u].my_neighbors[k];
    			if(color[v]==0) { //note that color is not initialized.
    				color[v]=1;
    				G[v].parent=u;
    				final int V=v;
    				async { 
    					x10.lang.perf.addLocalOps(1);
    					traverse(V);
    				}
    			}
    		}
	    }
	    
	    boolean verify_traverse(int root)
	    {
	    	int [] D;
	    	int i;
	    	
	    	D = new int [N];
	    	for(i=0;i<N;i++)
	    	{
	    		D[i]=G[i].parent;
	    	}
	    	for(i=0;i<N;i++)
	    	{
	    		while(D[i]!=D[D[i]]) D[i]=D[D[i]];
	    	}
	    	for(i=0;i<N;i++)
	    		if(D[i]!=root) {
	    			System.out.println("verify_traverse failed at "+i);
	    			return(false);
	    		}
	    	System.out.println("verify_traverse succeeded");
	    	return(true);
	    }

}


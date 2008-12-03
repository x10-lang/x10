#include <stdio.h>
#include <stdlib.h>
#include "graph.h"

int * color;

/*Nov 7,03: the previous random graph generator may create repeated edges between two vertices, fixed*/
/*Jun 4, 04: m 's value can be changed as we add edges to make it a connected graph*/
V* r_graph(int n,int m)
{
    int i,j,v1,v2,v,top,tail,r;
    E *L;
    char * color;
    int * * M;
    int * D,*info,*stack;
    V* graph;

    L=(E *) malloc(sizeof(E)*m);
    j=0; 
    for(i=0;i<m;i++)
     {
	v1=(int)(drand48()*n)%n;
	v2=(int)(drand48()*n)%n;
	while(v2==v1) v2=(int)(drand48()*n) %n;
        L[j].v1=v1;
	L[j++].v2=v2;
     } 
    printf(" number of edges got is %d\n",j);

    M = malloc(sizeof(int *)*n);
    D = malloc(sizeof(int)*n);

    for(i=0;i<n;i++) D[i]=0;

    for(i=0;i<m;i++) {
	D[L[i].v1]++;
	D[L[i].v2]++;
    }

    for(i=0;i<n;i++) {
	M[i]=malloc(sizeof(int)*(D[i]+2)); /*+2 leaves space for making connected*/
    }
    printf("finished allocating structures\n");

    for(i=0;i<n;i++) D[i]=0;

    for(i=0;i<m;i++)
    {
		r=0;
		for(j=0;j<D[L[i].v1];j++)
			if(L[i].v2==M[L[i].v1][j]) r=1; 
		if(r==1) continue;
		
		M[L[i].v1][D[L[i].v1]]=L[i].v2;
        M[L[i].v2][D[L[i].v2]]=L[i].v1;
        D[L[i].v1]++;
        D[L[i].v2]++;
    }   
    free(L);
    printf("finished generating the matrix\n");
    printf("checking if is connected\n");
    printf("n is %d\n",n);
    color = malloc(sizeof(char)*n);
    stack = malloc(sizeof(int)*n);
    info  = malloc(sizeof(int)*n);
    
    if(!color || ! stack || ! info ){
      printf("error allocating memory\n");
      exit(0);
    }

    for(i=0;i<n;i++)  color[i]=0;
    top=-1;
    tail=0;
    
    for(i=0;i<n;i++)
    {
      if(color[i]==1) continue;
      else info[tail++]=i;
      
      stack[++top]=i;
      color[i]=1;
      while(top!=-1) {
      v = stack[top];
      top--;
     
      for(j=0;j<D[v];j++)
        if(color[M[v][j]]==0){
	         top++;
		 stack[top]=M[v][j];
		 color[M[v][j]]=1;
         }
      }
    }

    printf("checking done,tail is %d\n",tail);
    for(i=0;i<tail-1;i++)
    {
       M[info[i]][D[info[i]]++]=info[i+1];
       M[info[i+1]][D[info[i+1]]++]=info[i];
    } 
    	
   graph = (V*)malloc(n *sizeof (V));
   if(graph==NULL)
   {
        printf("1 mem alloc error\n");
        return (NULL);
   }
   for(i=0;i<n;i++)
   {
        /*there should be n neighbors for this vertex*/
		/*graph[i].self=i;*/
 		graph[i].n_neighbors=D[i];
        graph[i].my_neighbors=(int *)malloc(D[i]*sizeof(int));
		/*graph[i].is_tree_edge=(int *)malloc(D[i]*sizeof(int));*/
        if(graph[i].my_neighbors==NULL) {
                printf("2 mem alloc error\n");
                return(NULL);
        }
        for(j=0;j<D[i];j++)
        {
            graph[i].my_neighbors[j]=M[i][j];
	   /* graph[i].is_tree_edge[j]=0; */
        }
   }

    printf("done generating graph structure\n");
    for(i=0;i<n;i++)
     free(M[i]);
    free(M);
    free(D);   
    free(stack);
    free(info);
    free(color); 
    return(graph);
 }

V* torus(int k)
{
  int *Buff;
  int ** Adj;
  int i,j,l,s; 
  V* graph;
  int n = k*k; 
  
  Buff = malloc(sizeof(int)*n);
  Adj  = malloc(sizeof(int*)*n);
  for(i=0;i<n;i++) Adj[i]=malloc(sizeof(int)*4);

#if 1
  for(i=0;i<n;i++) Buff[i]=i;

  for(i=0;i<n/2;i++){
	l=(int)(drand48()*n)%n;
 	s=(int)(drand48()*n)%n;
	j=Buff[l];
	Buff[l]=Buff[s];
	Buff[s]=j;
  }
	
  for(i=0;i<k;i++)
    {
      for(j=0;j<k;j++)
	{
	  Adj[Buff[i*k+j]][0]= Buff[((k+i-1)%k)*k+j];
	  Adj[Buff[i*k+j]][1]= Buff[((i+1)%k)*k+j];
	  Adj[Buff[i*k+j]][2]= Buff[i*k+((k+j-1)%k)];
	  Adj[Buff[i*k+j]][3]= Buff[i*k+((j+1)%k)];
	}
  
    }	
#endif


#if 0

	for(i=0;i<k;i++)
    {
      for(j=0;j<k;j++)
	{
	  Adj[i*k+j][0]= ((k+i-1)%k)*k+j;
	  Adj[i*k+j][1]= ((i+1)%k)*k+j;
	  Adj[i*k+j][2]= i*k+((k+j-1)%k);
	  Adj[i*k+j][3]= i*k+((j+1)%k);
	}
  
    }
	
#endif
	  
   graph = (V*)malloc(n *sizeof (V));
   if(graph==NULL)
   {
        printf("1 mem alloc error\n");
        return (NULL);
   }
   for(i=0;i<n;i++)
   {
        /*there should be n neighbors for this vertex*/
		/*graph[i].self=i;*/
 		graph[i].n_neighbors=4;
        graph[i].my_neighbors=(int *)malloc(4*sizeof(int));	
	/*graph[i].is_tree_edge=(int *)malloc(4*sizeof(int));*/	
        if(graph[i].my_neighbors==NULL) {
                printf("2 mem alloc error\n");
                return(NULL);
        }
        for(j=0;j<4;j++)
        {
            graph[i].my_neighbors[j]=Adj[i][j];
	    /*graph[i].is_tree_edge[j]=0;*/
        }
   }
  
  for(i=0;i<n;i++) free(Adj[i]);
  free(Adj);
  free(Buff);
  return(graph);

 }


V* k_graph(int n, int k)
{
  int neighbor;
  char * visited;
  int * stack,*super;
  int i,j,u,v,nextn,top=-1,n_comp=0,rep,s;
  int ** array,*counter; 
  V* graph;
  
  int TIMES=3;
  int THRESHOLD=100;

  array = malloc(sizeof(int *)*n);
  visited = malloc(sizeof(char)*n);
  counter = malloc(sizeof(int )*n);

  if(array==NULL || visited==NULL || counter==NULL) 
    {
      printf("not enough mem\n");
      exit(-1);
    }

  for(i=0;i<n;i++)
  {
    array[i]=malloc(sizeof(int)*k*TIMES);
    if(array[i]==NULL) { 
      printf("not enough mem\n");
      exit(-1);
    }
    counter[i]=0;
    visited[i]=0;
  }

 
  stack = malloc(sizeof(int)*(n)/2);
  super = malloc(sizeof(int)*(n)/2);
  
  if(stack==NULL || super==NULL)
    {
      printf("not enough mem\n");
      exit(-1);
    }

  for(i=0;i<n;i++)
  {
     for(j=counter[i];j<k;j++)
     {
     	if(i<n-THRESHOLD)
       		neighbor=(int)(drand48()*(n-i))%(n-i)+i;
       	else neighbor=(int)(drand48()*THRESHOLD)%(THRESHOLD);
		rep=0;
		for(s=0;s<counter[i];s++) 
	   		if(array[i][s]==neighbor) rep=1;
		while(rep==1)
		{
			rep=0;
			if(i<n-THRESHOLD)
       			neighbor=(int)(drand48()*(n-i))%(n-i)+i;
       		else neighbor=(int)(drand48()*THRESHOLD)%(THRESHOLD);
			for(s=0;s<counter[i];s++) 
	   			if(array[i][s]==neighbor) rep=1;
		}
#if 0
        neighbor=rand()%(n-i)+i;
       else neighbor=rand()%THRESHOLD;
#endif
	while(counter[neighbor]>TIMES*k-1 || neighbor==i) neighbor=(neighbor+1)%n;
        array[i][counter[i]]=neighbor;
        counter[i]++;
        array[neighbor][counter[neighbor]]=i;
        counter[neighbor]++;
     }
  }

  printf("check if it is connected\n");

  /* now make the graph connected if it is not*/
  for(i=0;i<n;i++)
    {
      if(!visited[i]){
	visited[i]=1;
	stack[++top]=i;
	super[n_comp++]=i;

	while(top!=-1)
	  {
	    v = stack[top];
	    top--;

	    for (j=0; j<counter[v]; j++) {
	      nextn = array[v][j];
	      if(!visited[nextn]) {  /* not seen yet */
		visited[nextn]=1;
		stack[++top]=nextn;
	      }
	    }
	  }
      }
    }
  
  for(i=1;i<n_comp;i++)
    {
      u = super[i];
      v = super[i-1];
      array[u][counter[u]++]=v;
      array[v][counter[v]++]=u;
    }
  
   graph = (V*)malloc(n *sizeof (V));
   if(graph==NULL)
   {
        printf("1 mem alloc error\n");
        return (NULL);
   }
   for(i=0;i<n;i++)
   {
        /*there should be n neighbors for this vertex*/
		/*graph[i].self=i;*/
 		graph[i].n_neighbors=counter[i];
        graph[i].my_neighbors=(int *)malloc(counter[i]*sizeof(int));
		/*graph[i].is_tree_edge=(int *)malloc(counter[i]*sizeof(int));*/
        if(graph[i].my_neighbors==NULL) {
                printf("2 mem alloc error\n");
                return(NULL);
        }
        for(j=0;j<counter[i];j++)
        {
            graph[i].my_neighbors[j]=array[i][j];
	/*graph[i].is_tree_edge[j]=0;*/
        }
   }

  for(i=0;i<n;i++) free(array[i]);
  free(array);
  free(counter);
  free(stack);
  free(super);
  free(visited);
  return(graph);
}

void traverse(V* G,int r)
{
   int i,v;
 
   for(i=0;i<G[r].n_neighbors;i++)
   {
      v = G[r].my_neighbors[i];
      if(color[v]==0) {
	 color[v]=1;
	 traverse(G,v);
      }
   }
}

void ST(V* G,int n)
{
    traverse(G,0);
}

int main(int argc, char *argv[])
{
	V* G;
	int n_vertices=100, n_edges=400,i,size;
 	G=r_graph(n_vertices,n_edges);
	color = (int *) malloc(sizeof(int)*n_vertices);
	for(i=0;i<n_vertices;i++) color[i]=0;
	printf("r_graph done\n");
	ST(G,n_vertices);	
	printf("ST done");
	return(0);
}

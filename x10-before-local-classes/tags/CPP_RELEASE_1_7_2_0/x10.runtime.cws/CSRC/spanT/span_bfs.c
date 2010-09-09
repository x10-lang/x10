#include "simple.h"
#include "graph.h"
#include "queue.h"

#define NANO 1000000000

/* A balanced breadth-first search*/
int span_BFS(V* G,int n_vertices,THREADED)
{
#define THRESHOLD 10
#define STEPS 6
   
   int * Q, *Q1, *Qt, *visited;
   int ** Q_M, ** tail_M, ** head_M;
   int i,j,k,r,v,u, head, tail, h1,t1,root =0,size, done, count=0, level=0;
   
   Q = (int *) malloc(sizeof(int)*n_vertices);
   visited = (int*) node_malloc(sizeof(int)*n_vertices,TH);
   Q_M = (int **) node_malloc(sizeof(int*)*THREADS,TH);
   Q_M[MYTHREAD] = Q;
   tail_M = (int **) node_malloc(sizeof(int*)*THREADS,TH);
   head_M = (int **) node_malloc(sizeof(int*)*THREADS,TH);
   tail_M[MYTHREAD]=&tail;
   head_M[MYTHREAD]=&head;
   tail=0;
   head=0;
   
   pardo(i,0,n_vertices,1)
   	visited[i]=0;
	
   node_Barrier();
     
   on_one{
   
	G[root].parent=root;
	visited[root]=1;
	Q1 = malloc(sizeof(int)*n_vertices);
	 
	h1 = t1 = 0;
	
	enQ(root, Q1, &t1);
	for(i=0; i<STEPS ; i++)
	{
		size = t1-h1;
		for(j=0;j<size;j++)
		{
			r = deQ(Q1, &h1, &t1);
			for(k=0;k<G[r].n_neighbors;k++){
				v = G[r].my_neighbors[k];
				if(!visited[v]){
					visited[v]=1;
					G[v].parent=r;
					enQ(v, Q1, &t1);
				}
			}
		}
		if(t1-h1 >= THREADS*THRESHOLD) break;
		/*printf("t1 - h1 =%d\n", t1-h1);*/
	}
	level=i+1;	
	/*printf("Init takes %d steps, and got %d elements\n", i, t1-h1);*/
	
	for(i=0;i<t1-h1;i++)
	{
		enQ(Q1[h1+i], Q_M[i%THREADS], tail_M[i%THREADS]);		
	}
	
	free(Q1);
	
   }
   node_Barrier();
   
   for(j=0;;j++)
   {
    done = 0;
   	size=tail-head;
	for(i=0;i<size;i++)
	{
		r = deQ(Q, &head, &tail);
		for(k=0;k<G[r].n_neighbors;k++)
		{
			v = G[r].my_neighbors[k];
			if(!visited[v])
			{
				visited[v]=1;
				G[v].parent=r;
				enQ(v, Q, &tail);
				count++;
			}
		}
	}
	node_Barrier();
	if(size==0){
		done=1;
	}
	done = node_Reduce_i(done,MIN,TH);
	if(done==1) break;
	
   }
   
   on_one printf("Takes %d iterations\n", j+1+level);
   /*printf("THREAD %d: find %d vertices\n", MYTHREAD,count);*/
   
   node_free(visited,TH);
   node_free(Q_M,TH);
   node_free(tail_M,TH);
   node_free(head_M,TH);
   free(Q);
   
   
#undef THRESHOLD  
#undef STEPS  
}





#include "simple.h"
#include "graph.h"

#define NANO 1000000000

/* BFS with local  Q*/

int MergQ(int * Q, int * frontier, int * workspace, int head, int tail, THREADED);

int span_BFS_localq(V* G,int n_vertices,THREADED)
{
	int * visited;
	int * Q, *frontier, *workspace;
	int head=0, tail=0, q_size, frontier_size;
	int i,j, level=0, u, v;
	
	Q = (int *) malloc(sizeof(int)*n_vertices);
	frontier = (int *) node_malloc(sizeof(int)*n_vertices*2,TH);	
	visited = (int *) node_malloc(sizeof(int)*n_vertices,TH);
	workspace = (int *) node_malloc(sizeof(int)*THREADS,TH);

	pardo(i,0,n_vertices,1)
		visited[i]=0;

	node_Barrier();

	on_one{
		Q[tail++]=0;
		visited[0]=1;
	}
	level++;
	node_Barrier();
	q_size = tail - head;

	frontier_size = node_Reduce_i(q_size, SUM, TH);

	while(frontier_size!=0){
		MergQ(Q, frontier, workspace,head,tail,TH);
		head=tail=0;
		pardo(i,0,frontier_size,1)
		{
			u = frontier[i];
			for(j=0;j<G[u].n_neighbors;j++)
			{
				v = G[u].my_neighbors[j];
				if(visited[v]==0) {
					visited[v]=1;
					Q[tail++]=v;	
				}
			}
		}
		node_Barrier();
		level++;
		q_size = tail - head; 
		frontier_size = node_Reduce_i(q_size,SUM,TH);
		if(frontier_size>n_vertices) printf("frontier overflow\n");
	}
	/*on_one printf("there are %d levels\n", level);*/
}

int MergQ(int * Q, int * frontier, int * workspace, int head, int tail, THREADED)
{
	int i;

	workspace[MYTHREAD] = tail - head;
	node_Barrier();
	on_one{
		for(i=1;i<THREADS;i++)
			workspace[i]+=workspace[i-1];
		for(i=THREADS-1;i>=1;i--)
			workspace[i]=workspace[i-1];
		workspace[0]=0;
	}
	node_Barrier();
	for(i=0;i<tail-head;i++)
		frontier[i+workspace[MYTHREAD]]=Q[i];
	node_Barrier();
	return(0);
}



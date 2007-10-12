#include <sys/types.h>
#include "simple.h"
#include "graph.h"

V* G;
V* T,*T1,*T2;
E* El;
int n_edge;

V* r_graph(int n,int m);
E* r_graph_L(int n,int *m);
V* k_graph(int n, int k);
V* torus(int k);

#define NANO 1000000000
#define DEBUG_BICONN 0 
#define DEBUG_VERIFY 0

void *SIMPLE_main(THREADED)
{
  int i,t,j, n_vertices,N,k,opt;
  double interval,total=0;
  long seed;
  double tm, best_tm;

  on_one{
  	opt = atoi(THARGV[0]);
	seed = -712456314 ;
	/*seed =660;*/
	seed=568;
	srand(seed);
	printf("METRICS: seed is %d \n", seed);
  	switch(opt){
  	case 0: n_vertices=atoi(THARGV[1]); n_edge=atoi(THARGV[2]);
				  G=r_graph(n_vertices,n_edge); break; 
  	case 1: k=atoi(THARGV[1]); n_vertices=k*k; 
				  G = torus(k); break;
  	case 2: n_vertices=atoi(THARGV[1]); k = atoi(THARGV[2]);
				  G = k_graph(n_vertices,k); break;

	default: printf("unknown graph type, exit\n"); exit(1);
  	}
	printf("n_edges=%d\n",n_edge);
  }       
  n_vertices=node_Bcast_i(n_vertices,TH);
  initialize_graph_edgelist(G, n_vertices,&El,&n_edge, TH);
  node_Barrier();

#if 0
  for(i=0;i<5;i++){
  tm = get_seconds();
  spanning_tree_breadth_B2(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  Verify(G,n_vertices,TH);
  }
  on_one printf("Best time used on TRAVERSE2 is %f s\n", best_tm);

  for(i=0;i<5;i++){
  tm = get_seconds();
  spanning_tree_breadth_B1(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  Verify(G,n_vertices,TH);
  }
  on_one printf("Best time used on DFS is %f s\n", best_tm);
#endif

  pardo(i,0,n_edge, 1)
	El[i].workspace=0;
  for(i=0;i<5;i++){
  tm = get_seconds();
  spanning_tree_CRCW(G,El,n_vertices,n_edge,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  }
  on_one printf("Best time used on SV is %f s\n", best_tm);


  pardo(i,0,n_edge, 1)
	El[i].workspace=0;
  for(i=0;i<5;i++){
  tm = get_seconds();
  spanning_tree_CRCW_spinlock(G,El,n_vertices,n_edge,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  }
  on_one printf("Best time used on CRCW_spinlock is %f s\n", best_tm);

  for(i=0;i<5;i++){
  tm = get_seconds();
  span_BFS(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  }
  on_one printf("Best time used on BFS is %f s\n", best_tm);

  for(i=0;i<5;i++){
  tm = get_seconds();
  span_BFS_localq(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  }
  on_one printf("Best time used on BFS_localq is %f s\n", best_tm);

  node_Barrier();
  SIMPLE_done(TH);
}


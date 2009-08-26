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
  char * input_file;
  int * D;
  long seed;
  double tm, best_tm;

#if 1 
  on_one{
  	opt = atoi(THARGV[0]);
	seed = -712456314 ;
	/*seed =660;*/
	seed=568;
	srand(seed);
#if debug
	printf("METRICS: seed is %d \n", seed);
#endif
  	switch(opt){
  	case 0: n_vertices=atoi(THARGV[1]); n_edge=atoi(THARGV[2]);
				  G=r_graph(n_vertices,n_edge); break; 
#if 0

				  El=r_graph_L(n_vertices,&n_edge); break; 
#endif
  	case 1: k=atoi(THARGV[1]); n_vertices=k*k; 
				  G = torus(k); break;
  	case 2: n_vertices=atoi(THARGV[1]); k = atoi(THARGV[2]);
				  G = k_graph(n_vertices,k); break;

	default: printf("unknown graph type, exit\n"); exit(1);
  	}
#if debug
	printf("n_edges=%d\n",n_edge);
#endif
  }       
  node_Barrier();
# endif

#if 0
  on_one{
     initialize_graph(THARGV[0], &G,&n_vertices);
  }
#endif

  node_Barrier();
  n_vertices=node_Bcast_i(n_vertices,TH);
  node_Barrier();

  for(i=0;i<5;i++){
  tm = get_seconds();
  spanning_tree_breadth_B2(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  Verify(G,n_vertices,TH);
  }
  on_one printf(" %4f ", n_edge/(1000000*best_tm));
  /*on_one printf("Best time used on TRAVERSE2 is %f s\n", best_tm);*/

#if 0
  for(i=0;i<5;i++){
  tm = get_seconds();
  spanning_tree_breadth_B1(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  Verify(G,n_vertices,TH);
  /*on_one printf("Time used on TRAVERSE is %f s\n", tm);*/
  }
  /*on_one printf("Best time used on TRAVERSE is %f s\n", best_tm);*/
  on_one printf("  %4f ", n_edge/(1000000*best_tm));

  for(i=0;i<5;i++){
  tm = get_seconds();
  spanning_tree_breadth_spinlock(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  /*on_one printf("Time used on TRAVERSE_spinlock is %f s\n", tm);*/
  }

  on_one printf("Best time used on TRAVERSE_spinlock is %f s\n", best_tm);



  for(i=0;i<5;i++){
  tm = get_seconds();
  spanning_tree_breadth_cws(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  /*on_one printf("Time used on TRAVERSE_cws is %f s\n", tm);*/
  }
  on_one printf("Best time used on TRAVERSE_cws is %f s\n", best_tm);

  for(i=0;i<5;i++){
  tm = get_seconds();
  span_BFS(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  }
  /*on_one printf("Best time used on BFS is %f s\n", best_tm);*/
  on_one printf(" %4f ", n_edge/(1000000*best_tm));
#endif

  for(i=0;i<5;i++){
  tm = get_seconds();
  span_BFS_localq(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  }
  /*on_one printf("Best time used on BFS_localq is %f s\n", best_tm);*/
  on_one printf(" %4f ", n_edge/(1000000*best_tm));

#if 0 
  for(i=0;i<5;i++){
  tm = get_seconds();
  span_BFS_lb(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  }
  on_one printf("Best time used on BFS_lb is %f s\n", best_tm);

  for(i=0;i<5;i++){
  tm = get_seconds();
  span_BFS_spinlock(G,n_vertices,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  if(i==0) best_tm=tm;
  else if(best_tm >tm) best_tm=tm;
  }
  on_one printf("Best time used on BFS_spinlock is %f s\n", best_tm);
#endif

#if 0
  initialize_graph_edgelist(G, n_vertices,&El,&n_edge, TH);
  node_Barrier();
  on_one printf("init edge list done, n_edge = %d, n_vertices=%d\n", n_edge, n_vertices);

  node_Barrier();

  tm = get_seconds();  
  spanning_tree_CRCW(G,El,n_vertices,n_edge,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  on_one printf("Time used on CRCW is %f s\n", tm);
  
  
  pardo(i,0,n_edge,1)
    El[i].workspace=0;
  node_Barrier();

  tm = get_seconds();  
  spanning_tree_CRCW_lock(G,El,n_vertices,n_edge,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  on_one printf("Time used on CRCW_lock is %f s\n", tm);

  pardo(i,0,n_edge,1)
    El[i].workspace=0;
  node_Barrier();


  pardo(i,0,n_edge,1)
    El[i].workspace=0;
  node_Barrier();
  tm = get_seconds();  
  spanning_tree_CRCW_cas(G,El,n_vertices,n_edge,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  on_one printf("Time used on CRCW_cas is %f s\n", tm);


  pardo(i,0,n_edge,1)
    El[i].workspace=0;
  node_Barrier();


  tm = get_seconds();  
  spanning_tree_CRCW_spinlock(G,El,n_vertices,n_edge,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  on_one printf("Time used on CRCW_spinlock is %f s\n", tm);
  pardo(i,0,n_edge,1)
    El[i].workspace=0;
  node_Barrier();
  
  tm = get_seconds();  
  spanning_tree_CRCW_race(G,El,n_vertices,n_edge,TH);
  node_Barrier();
  tm = get_seconds() -tm;
  on_one printf("Time used on CRCW_race is %f s\n", tm);
#endif


  on_one_thread{
	delete_graph(G,n_vertices);
	if(El) free(El);
  } 

  node_Barrier();
  SIMPLE_done(TH);
}


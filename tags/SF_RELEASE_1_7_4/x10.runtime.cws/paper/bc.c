#include "graph.h"
#include "stdio.h"
#include "stdlib.h"
#include "queue.h"
#include "stack.h"

V * G;

main(int argc, char **argv)
{
  int i, k, s, w, v, n, m, d, h, t, opt;
  int * S, * Q, top, *D, *P, *np ,*pp, *sigma;
  double * BC, *delta;

#if 1 
  if(argc!=2) {
    printf("usage: bc <file>\n");
    exit;
  }
  /*initialize_graph(argv[1],&G,&n,&m); */
  /*G = rdprodat(argv[1], &n, &m);*/
  G = rdactor(argv[1], &n, &m);
  printf("init done, n=%d, m=%d\n", n, m);
  for(i=0;i<n;i++)
     for(k=0;k<G[i].n_neighbors;k++)
         if(G[i].neighbors[k]>n) printf("error in graph: i=%d, k=%d, v=%d\n", i, k, G[i].neighbors[k]);
#else
  opt = atoi (argv[1]);
  switch(opt){
  case 0: n = atoi(argv[2]); m = atoi(argv[3]); 
          printf("random graph, n=%d, m=%d\n", n, m);
          G = r_graph(n,m); break;
  case 1: k=atoi(argv[2]); n=k*k; m = 2 * n; 
          G = torus(k); break;
  case 2: n=atoi(argv[2]); k = atoi(argv[3]); m = n * k;
          G = k_graph(n,k); break;
  default: printf("unknown graph type, exit\n"); exit(1);
  }
  printf("generate graph done\n"); 
#endif

  S = (int *) malloc(sizeof(int)*n);
  Q = (int *) malloc(sizeof(int)*n);
  D = (int *) malloc(sizeof(int)*n);
  P = (int *) malloc(sizeof(int)*2*m);
  pp = (int*) malloc(sizeof(int)*n); /* pp[i]:start of i's predecessor buffer*/ 
  np = (int*) malloc(sizeof(int)*n); /* np[i]:how many predecessor*/ 
  sigma = (int*) malloc(sizeof(int)*n);  
  BC =  (double *) malloc (sizeof(double)*n); 
  delta =  (double *) malloc (sizeof(double)*n); 
  
  for(i=0;i<n;i++) BC[i] = 0.0;
  for(i=0;i<n;i++) Q[i] = G[i].n_neighbors; /* Q as the prefix buffer for now */ 
  for(i=1;i<n;i++) Q[i] += Q[i-1];
  for(i=0;i<n;i++) pp[i] = Q[i] - G[i].n_neighbors;
 
  for(s=0;s<n;s++)
  {
      for(i=0;i<n;i++) D[i] = -1;
      D[s] = 0;
      for(i=0;i<n;i++) np[i] = sigma[i] = 0;
      sigma[s] = 1;
      h = t =0;
      top = -1;
      enQ(Q,s,&t);
      while(!emptyQ(Q,&h,&t))
      {
	v = deQ(Q,&h,&t);
	push(S,v,&top);
	for(k=0;k<G[v].n_neighbors;k++)
        {
            w = G[v].neighbors[k];
            if(D[w] < 0) {
               D[w] = D[v]+1;
               enQ(Q,w,&t);
            }
            if(D[w] == D[v]+1) {
               sigma[w] += sigma[v];
               P[ pp[w]+np[w] ] = v;
               np[w] ++; 
            }
        }
      }
     
      /*printf("top=%d\n", top);*/
 
      for(k=0;k<n;k++) delta[k]=0;

      while(!emptyS(S,&top))
      {
          w = pop(S,&top);
          for(k=0; k<np[w]; k++) 
          {
             v = P[pp[w]+k];
             delta[v] += (double)sigma[v]*(1+delta[w])/(double)sigma[w];
          }
          if(w != s) BC[w] += delta[w]; 
      }  
  } 
  
  for(i=0;i<n;i++)
     printf(" %d: %5f\n", i, BC[i]);

  free(S);
  free(Q);
  free(D);
  free(P);
  free(pp);
  free(np);
  free(BC);  
}


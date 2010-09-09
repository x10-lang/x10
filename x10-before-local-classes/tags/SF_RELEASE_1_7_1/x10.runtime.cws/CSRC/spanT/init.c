#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include "graph.h"
#include "simple.h"

#define DEBUG_GRAPH 0 
#define DEBUG_TIMING 0 
#define DEBUG_CORRECTNESS 1 
#define DEBUG_WAIT 0

E* shrink_edgeL(E* El, int *pn_edge,int *D,THREADED);
void push(int a, int * stack, int *top);
int pop(int * stack, int *top);
int is_empty(int * stack, int * top);


/*initialize the graph, and initialize the pal index*/
int initialize_graph(const char * file,V** pGraph, int *pnVertices)
/*all the global data will be initialized*/
{
   int i,j,k,n,u, nVer=0;
   FILE *fp;
   V* graph;
   size_t  size;

   fp = fopen(file,"r");
   if(fp==NULL){
         printf("open file error\n");
         return( -1);
   }

  if(fscanf(fp,"%d", pnVertices)==EOF)
  {
        printf("read file error\n");
        return(-1);
  }

  size=(unsigned int)(*pnVertices) *sizeof (V);
  printf("size is %d, number of vertices is %d\n",size, *pnVertices);
  printf("sizeof(v) is %d\n",sizeof(V));
  graph = (V*)malloc(size);
  if(graph==NULL)
  {
        printf("3 mem alloc error\n");
        return (-1);
  }

  while(fscanf(fp,"%d", &n)!=EOF && nVer!=*pnVertices)
  {
        /*there should be n neighbors for this vertex*/
 	graph[nVer].n_neighbors=0;
        if(n==0) {
           graph[nVer++].my_neighbors=NULL;
           continue;
        }
        graph[nVer].my_neighbors=(int *)malloc(n*sizeof(int));
        if(graph[nVer].my_neighbors==NULL) {
                printf("4 mem alloc error,n=%d\n",n);
                return(-1);
        }
        for(i=0;i<n;i++)
        {
                if(EOF==fscanf(fp,"%d",&u)) /* in the first run just to allocate all data structure*/
                {
                        printf("error reading file\n");
                        fclose(fp);
                        return(-1);
                }
        }
        nVer++;
  }


  rewind(fp);
  
 if(fscanf(fp,"%d", pnVertices)==EOF)
  {
        printf("read file error\n");
        return(-1);
  }


  nVer=0;
  while(fscanf(fp,"%d", &n)!=EOF && nVer!=*pnVertices)
  {     
        for(i=0;i<n;i++)
        {
                if(EOF==fscanf(fp,"%d",&u))
                {
                        printf("error reading file\n");
                        fclose(fp);
                        return(-1);
                }else{
		  if(u<nVer) continue; /*this edge has been processed*/
		  else {
                    if(graph[u].my_neighbors==NULL) printf("NULL\n");
		    graph[u].my_neighbors[graph[u].n_neighbors]=nVer;
		    graph[nVer].my_neighbors[graph[nVer].n_neighbors]=u;
		    graph[u].n_neighbors++;
		    graph[nVer].n_neighbors++;
		  }
		}
        }
        nVer++;
  }

#if 0
  for(i=0;i<nVer;i++) 
    if(graph[i].tmp!=graph[i].n_neighbors) printf("INIT GRAPH ERROR\n");
#endif

  if(nVer!=*pnVertices)
  {
        printf("error reading file\n");
        fclose(fp);
        return(-1);
  }

  fclose(fp);
  *pGraph=graph;
  return (0);
}

#if 0

void set_graph_pal_index(V* graph, int nVer,THREADED)
{
 int i,j,k;

 pardo(i,0,nVer,1)
  {
     for(j=0;j<graph[i].n_neighbors;j++)
        for(k=0;k<graph[graph[i].my_neighbors[j]].n_neighbors;k++)
                if(i==graph[graph[i].my_neighbors[j]].my_neighbors[k]){
                         graph[i].pal_index[j]=k;
                         break;
                }
  }

}

#endif

int delete_graph(V* graph, int nVertices)
{
#if 0
   int i;
   for(i=0;i<nVertices;i++)
   {
      if(graph[i].my_neighbors) free(graph[i].my_neighbors);
      if(graph[i].is_tree_edge) free(graph[i].is_tree_edge);
      if(graph[i].pal_index) free(graph[i].pal_index);
   }
   if(graph) free(graph);
  
   return 0;
#endif
}


/*initialize the edge list. That is, to change from the adjacency list representation to the edge list representation*/

int initialize_graph_edgelist(V* graph, int n_vertices,E **pEL,int *pn_edge, THREADED)
{
  int * position;
  E* edge_list;
  int i,j,ret=0;

  position = node_malloc(sizeof(int)*n_vertices,TH);

  pardo(i,0,n_vertices,1)
    {
      position[i]=graph[i].n_neighbors;
    }

  node_Barrier();

  /*now run prefix-sum on buff*/
  prefix_sum(position,n_vertices,TH);

  node_Barrier();
  *pn_edge=position[n_vertices-1];
  node_Barrier();

  pardo(i,0,n_vertices,1)
    {
      if(i==0)
        graph[i].edge_start=0;
      else graph[i].edge_start=position[i-1];
    }
  edge_list =node_malloc(sizeof(E)*(*pn_edge),TH);

  pardo(i,0,n_vertices,1){
    for(j=0;j<graph[i].n_neighbors;j++)
      {
        if(i==0){
          edge_list[j].v1=i;
          edge_list[j].v2=graph[i].my_neighbors[j];
          /*edge_list[j].is_in_tree=0;*/
          edge_list[j].workspace=0;
        } else {
          edge_list[position[i-1]+j].v1=i;
          edge_list[position[i-1]+j].v2=graph[i].my_neighbors[j];
          /*edge_list[position[i-1]+j].is_in_tree=0;*/
          edge_list[position[i-1]+j].workspace=0;
        }
      }
  }
  node_Barrier();
  on_one_thread
    (*pEL)=edge_list;
  node_free(position, TH);

  return(0);
}


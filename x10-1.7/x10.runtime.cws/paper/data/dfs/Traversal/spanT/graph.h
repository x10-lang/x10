#ifndef _GRAPH_H_
#define _GRAPH_H_

#define DIRECTED 1
#define UNDIRECTED 0
#define DEBUG_BALANCE 1

typedef struct vertex{
   int n_neighbors;
   int *my_neighbors;
   int parent;    /*used only in spanning tree*/
#if 0
   int self;
   int v_attribute;
   int *is_tree_edge; /* if<i,graph[i].my_neighbors[k]> is a tree edge in spanning tree*/
   int *pal_index;          /*for edge <i,graph[i].my_neighbors[k]> , what is the index for i in i,graph[i].my_neighbors[k]>'s neighborlist*/ 
#endif

   int edge_start; /* the position of the first edge going out from the vertex in edgelist*/
} V; /*tree structure for graph and spanning tree*/

typedef struct edge{
	int v1,v2;
	/*int is_in_tree;*/
	int workspace; /* used to show v2's index in v1's neighbor list*/
	} E;
 
int initialize_graph(const char * file,V** graph, int *nVertices);

#endif /*_GRAPH_H_*/

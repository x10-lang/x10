
#include "simple.h"
#include "graph.h"
#include "stack.h"

/*  a balanced breadth-first search*/
int Verify(V* graph, int nVertices,THREADED)
{
	int * D,i;

	D = node_malloc(sizeof(int)*nVertices,TH);
	pardo(i,0, nVertices,1)
		D[i]=graph[i].parent;

	node_Barrier();
	pardo(i,0, nVertices,1)
		while(D[i]!=D[D[i]]) D[i]=D[D[i]];
	node_Barrier();
	pardo(i,0,nVertices,1)
		if(D[i]!=D[0]){
			 printf("error in the tree\n");
			 break;
	}
	node_Barrier();
	node_free(D,TH);

}





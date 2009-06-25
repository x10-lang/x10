#include "simple.h"
#include "graph.h"

int spanning_tree_CRCW(V* graph,E* El, int nVertices,int n_edges,THREADED)
{
  int *buffer,*D,*assign,*done,changed,i,j,n,iter_count=0;
  V * tree;
  E *El_tmp;

  changed=1;
  buffer=node_malloc(sizeof(int)*nVertices,TH);
  D=node_malloc(sizeof(int)*nVertices,TH);
  assign=node_malloc(sizeof(int)*nVertices,TH); 
  done=node_malloc(sizeof(int)*nVertices,TH);

  pardo(i,0,nVertices,1)
    {     
      done[i]=0;
      D[i]=i;
    }
  node_Barrier();
  while((changed)==1)
    {
      	iter_count++;
      	changed=0;

    	pardo(n,0,n_edges,1)
	{
	  if(El[n].workspace==1) continue; /*this edge has been added to tree*/
	  i=El[n].v1;
	  j=El[n].v2;
	  if(D[j]<D[i] && D[i]==D[D[i]])
	    {
	      done[D[i]]=0;
	      assign[D[i]]=MYTHREAD;
	    }	  
	}
      	node_Barrier();
    	pardo(n,0,n_edges,1)
	{
	  if(El[n].workspace==1) continue;
	  i=El[n].v1;
	  j=El[n].v2;
      
	  if(D[j]<D[i] && D[i]==D[D[i]] && assign[D[i]]==MYTHREAD && done[D[i]]==0)
	    {
	      done[D[i]]=1;
	      changed=1;
	      D[D[i]]=D[j];
	      El[n].workspace=1;
	    }
	}
	changed=node_Reduce_i(changed,MAX,TH);
	if(changed==0) break;
	node_Barrier();

   	pardo(i,0,nVertices,1)
	{
	  while(D[i]!=D[D[i]]) D[i]=D[D[i]];
    	}
	node_Barrier();
    } /*while*/
    node_free(buffer, TH);
    node_free(D, TH);
    node_free(assign, TH);
    node_free(done, TH);
}


#include "simple.h"
#include "graph.h"
#include "locksMacros.il"

int spanning_tree_CRCW_spinlock(V* graph,E* El, int nVertices,int n_edges,THREADED)
{
  int *buffer,*D,*assign,*done,changed,i,j,n,s=0,l=0,t=0;
  int *lock_array;

  D=node_malloc(sizeof(int)*nVertices,TH); 
  done=node_malloc(sizeof(int)*nVertices,TH);
  lock_array = node_malloc(sizeof(int)*nVertices,TH);
  
  pardo(i,0,nVertices,1) 
    {     
      done[i]=0;
      D[i]=i;
      lock_array[i]=0;
    }

  node_Barrier();
  changed=1;
  while(changed==1)
    {
	changed=0;
    	pardo(n,0,n_edges,1)
	{
	  if(El[n].workspace==1) continue;
	  i=El[n].v1;
	  j=El[n].v2;
      
	  if(D[j]<D[i] && D[i]==D[D[i]] && done[D[i]]==0)
	    {
		  t = spin_lock_i(&(lock_array[D[i]]), MYTHREAD+1);
		  l ++;
		  s += t;
		  if(done[D[i]]==0){
		  	done[D[i]]=1;
			spin_unlock(&(lock_array[D[i]]));
	        	changed=1;
		    	El[n].workspace=1;
	        	D[D[i]]=D[j];
	     	}else spin_unlock(&(lock_array[D[i]]));
   	  }
	}
	changed=node_Reduce_i(changed,MAX,TH);
	if(changed==0) break;
    	pardo(i,0,nVertices,1)
	{
	  while(D[i]!=D[D[i]]) D[i]=D[D[i]];
    	}
    	node_Barrier();

    } /*while*/

    node_free(D,TH);
    node_free((void*)lock_array,TH);
    node_free(done,TH);
}

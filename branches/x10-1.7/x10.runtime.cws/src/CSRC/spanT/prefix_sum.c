#include "simple.h"

/* this is used for block prefix sum. Based on the idea that just to keep all the threads busy. */

int prefix_sum(int *buff, int n, THREADED)
{
  int * result,i,start,t=0;

  result = node_malloc(sizeof(int)*THREADS,TH);

  /*now run prefix-sum on buff*/
  pardo(i,0,n,1) {
  	start=i;
	break;
  }
  pardo(i,0,n,1){
	if(i!=start) buff[i]+=buff[i-1];
	t=buff[i];
  }
  node_Barrier();
  result[MYTHREAD]=t;
  node_Barrier();

  on_one_thread{
    for(i=1;i<THREADS;i++) result[i]+=result[i-1];
    for(i=THREADS-1;i>0;i--) result[i]=result[i-1];
    result[0]=0;
  } 
  node_Barrier();

  t = result[MYTHREAD];
  pardo(i,0,n,1)
	if(MYTHREAD!=0) buff[i]+=t;	
  node_Barrier();
  node_free(result,TH);
}





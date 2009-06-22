#include "simple.h"
#include "graph.h"
#include "stack.h"
#include "xchg.il"

/*  a balanced breadth-first search*/
int spanning_tree_breadth_B2(V* graph, int nVertices,THREADED)
{
#define S_POINTS (THREADS*THREADS*THREADS*2)
#define MYCOLOR (MYTHREAD+1)
#define GREY    (THREADS+1)
#define MAX_WALKS 10
#define MIN_WALKS 4
#define INDEX(a,b) ((a-1)*(THREADS)+(b-1))

  volatile int * color;
  int first_time, work_to_steal,myroot,b,max, host, *stack,top,count,count1,bottom=-1,i,j,n,t,root,walks,r,*starting_points,counter=0;
  double power;
  volatile int ** stack_M, **top_M, **bottom_M;
  int * finished;
  unsigned int seed=MYCOLOR;
  int * count_M;


  stack_M = (volatile int **)node_malloc(THREADS*sizeof(int *),TH);
  top_M = (volatile int **) node_malloc(THREADS*sizeof(int *),TH);
  bottom_M=(volatile int **)node_malloc(THREADS*sizeof(int *),TH);

  stack=(int *)malloc(nVertices*sizeof(int));
  stack_M[MYTHREAD]=stack;
  top_M[MYTHREAD]=&top;
  bottom_M[MYTHREAD]=&bottom;

  finished = (int *)node_malloc(THREADS*sizeof(int),TH);
  finished[MYTHREAD]=0;
  count_M=node_malloc(THREADS*sizeof(int),TH);

  color=node_malloc(sizeof(int)*nVertices,TH); 

  pardo(i,0,nVertices,1){
    color[i]=0;
  }

  bottom=-1;
  top=-1;
  count=0;
  count1=0;
  /*lets select a point to start in the graph*/
  on_one_thread {
    root=(rand_r(&seed)%nVertices);
    power=1/(float)THREADS;
    walks=pow(nVertices,power);
    if(walks<MIN_WALKS) walks=MIN_WALKS;
    if(walks>MAX_WALKS) walks=MAX_WALKS;
    color[root]=MYCOLOR;
    graph[root].parent=root;
  }

  root=node_Bcast_i(root,TH);
  walks=node_Bcast_i(walks,TH);
  starting_points=node_malloc(sizeof(int)*S_POINTS,TH);
  
  /* printf("walks is %d, root is %d\n",walks,root);*/

  /*lets first generate some candidates for the threads to start their random walks*/

  myroot=root;
  on_one_thread{
    j=0;
    push(myroot,stack_M[j],top_M[j]);
    for(i=0;i<S_POINTS;i++)
      {
	starting_points[i]=myroot;
	r=rand_r(&seed);
	if(r%2==0){
	  for(r=0;r<graph[myroot].n_neighbors;r++)
	    {
	      n=graph[myroot].my_neighbors[r];
	      if(color[n]==0){
		graph[n].parent=myroot;
		color[n]=MYCOLOR;
		myroot=n;
		count++;
		break;
	      }
	    }
	}
	else {
	  for(r=graph[myroot].n_neighbors-1;r>=0;r--)
	    {
	      n=graph[myroot].my_neighbors[r];
	      if(color[n]==0){
		graph[n].parent=myroot;
		color[n]=MYCOLOR;
		myroot=n;
		count++;
		break;
	      }      
	    }
	}
	push(myroot,stack_M[j],top_M[j]);
	j=(j+1)%THREADS;
      }
  }
	       
  node_Barrier();

  /*now each thread randomly picks a start point from the staring points*/
  r=rand_r(&seed)%(THREADS*THREADS);
  myroot=starting_points[2*MYTHREAD*THREADS*THREADS+r];
    
  node_Barrier();
  
  push(myroot, stack, &top);
  count++;

  node_Barrier();

  first_time=1;
  work_to_steal=0;

  while(first_time || work_to_steal)
    {
      /*if(work_to_steal) printf("stealing work\n");*/
      while(!is_empty( stack, &top, &bottom))
	{
	  n=pop(stack,&top,bottom);
	  count1++;
	  if(n==-1){
	    printf("stack overflow\n");
	    top=-1;
	    bottom=-1;
	    break;
	  }

	  for(i=0;i<graph[n].n_neighbors;i++)
	    {
	      if(Xchg(&color[graph[n].my_neighbors[i]],1)==0){
		graph[graph[n].my_neighbors[i]].parent=n;
		push(graph[n].my_neighbors[i],stack,&top);
		count++;
	      }
	  
	    }
	}
      /*printf("Thread %d:done with this stack,current count is %d \n", MYTHREAD, count);*/

      if(first_time){
	first_time=0;
	finished[MYTHREAD]=1;
      }

      work_to_steal=0;
      max=-1;
      host=-1;
      for(i=0;i<THREADS;i++)
	{
	  if(!finished[i]){
	    n=*(top_M[i]);
	    b=*(bottom_M[i]);
	    if(n<=0) continue;
	    if(n-b>max) {max=n-b;host=i;}
	  }
	}

      if(count>nVertices/THREADS) /* I already did my share*/
	  r=b+(n-b)/THREADS; /*I won't steal half of the work, that will put too much work on me. I take a 1/THREADS piece*/
      else  /* I didn't have chance to do my share, but lets make up.*/ 
	r=b+max((n-b)/THREADS,min(nVertices/THREADS-count,(n-b)/2));

      if(host!=-1 && r<(*top_M[host])) {
	work_to_steal=1;		 
	(*bottom_M[host])=(r-1);
	while((r--)>b)
	  push(stack_M[host][r],stack,&top);
	break;
      }
    }

  count_M[MYTHREAD]=count;
  node_Barrier();
  /*printf("Thread %d count is %d\n",MYTHREAD, count);  */

#if 0
  on_one_thread{
    int max=0, min=nVertices;
    for(i=0;i<THREADS;i++)
      {
	if(count_M[i]>max) max=count_M[i];
	if(count_M[i]<min) min=count_M[i];
      }
    printf("===span_breadth_B1:The difference between counts is %d\n",max-min);
  }
  node_Barrier();
#endif

  count1=node_Reduce_i(count1,SUM,TH);
  count=node_Reduce_i(count,SUM,TH);
/*  on_one printf("count =%d, redundancy is %d, count1=%d\n", count, count - nVertices, count1);*/
  node_free(count_M,TH);
  node_free(finished,TH);
  node_free(color, TH);
  node_free(starting_points, TH);
  node_free(stack_M,TH);
  node_free(top_M,TH);
  free(stack);
}





/**
 * A C++ version of the recursive SpanT implemented in Java (in
 * x10 cws).  
 * @author Rajkishore Barik
 */

#include <iostream>
#include <string>

#include "Closure.h"
#include "Cache.h"
#include "Frame.h"
#include "Worker.h"
#include "Job.h"
#include "Pool.h"
#include <assert.h>
#include <stdlib.h>
#include <vector>

using namespace std;
using namespace x10lib_cws;



class V {
public:
	int parent;
	int degree;
	int[] neighbors;
	V(){}
};

class E {
public:
	int v1,v2;
	bool in_tree;
	E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
};

typedef vector<V *> V_ARR;
typedef vector<E *> E_ARR;
typedef vector<int> I_ARR;

class TFrame : public Frame {
public:
	int u; // vertex
	volatile int k;
		
	TFrame(int u1) {
		u=u1;
	}
	void setOutlet(Closure *c) {
		// nothing to do
	}
	Closure *makeClosure() {
		Closure *retc=new Traverser(graph,this);
		assert (retc != NULL);
		return retc;
	}
}

class Traverser : public Closure {
public: 
		SpanC *graph;
		V_ARR *G;
		I_ARR c; // Atomic Integer Array TODO RAJ
		Traverser(SpanC *g, TFrame *t) : Closure(t) { 
			graph=g;
			G=graph->G;
			c=graph->color;
		}
		
		void compute(Worker *w, Frame *frame) throw StealAbort {
			TFrame *f = (TFrame *) frame;
			int u = f->u;
			int k = f->k;
			while (k < G[u].degree) {
				int v=G[u].neighbors[k];
				bool result = compare_exchange (&c[v],0,1);
				if (result) {
					G[v].parent=u;
					traverse(w,v);
					w->abortOnSteal();
				}
				++k;
				f->k=k;
			}
			setupGQReturnNoArg();
			return;
		}
}

class SpanC {
public:
	int m;
	V_ARR G;
	E_ARR El;
	E_ARR El1;
	I_ARR color; // Atomic Integer Array
	int ncomps=0;
	
	static int[] Ns = {10*1000,50*1000, 100*1000,
		500*1000, 1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000};
	int N, M;
	
	SpanC (int n, int m){
		N=n;
		M=m;
		
		/*constructing edges*/
		El = new E [M];
		double[] seeds = {
				0.21699440277541715, 0.9099040926714322, 0.5586793832519766,
				0.15656203486110076, 0.3716929310972751, 0.6327328452004045,
				0.9854204833301402, 0.8671652950975213, 0.1079976151083556,
				0.5993517714916581
		};
		
		int r0 = 0;
		int r1 = 1;
		for(int i=0;i<M;i++){
			El[i]=new 
			E ((int) (seeds[r0]*(N+i))%N, (int) (seeds[r1]*(N+i))%N);
			r0 = r1;
			if (++r1 >= 10) r1 = 0;
			
		}
		
		int[] D = new int [N];
		/* D[i] is the degree of vertex i (duplicate edges are counted).*/
		for(int i=0;i<M;i++){
			D[El[i].v1]++;
			D[El[i].v2]++;
		}
		
		int[][] NB = new int[N][];/*NB[i][j] stores the jth neighbor of vertex i*/
		// leave room for making connected graph by +2
		for(int i=0;i<N;i++) NB[i]=new int [D[i]+2]; 
		
		/*Now D[i] is the index for storing the neighbors of vertex i
		 into NB[i] NB[i][D[i]] is the current neighbor*/
		for(int i=0;i<N;i++) D[i]=0;
		
		m=0;
		for(int i=0;i<M;i++) {
			boolean r=false;;  
			/* filtering out repeated edges*/
			for(int j=0;j<D[El[i].v1] && !r ;j++){ 
				if(El[i].v2==NB[El[i].v1][j]) r=true;
			}
			if(r){
				El[i].v1=El[i].v2=-1; /*mark as repeat*/
			} else {
				m++;
				NB[El[i].v1][D[El[i].v1]]=El[i].v2;
				NB[El[i].v2][D[El[i].v2]]=El[i].v1;
				D[El[i].v1]++;
				D[El[i].v2]++;
			}
		}  
		
		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		color = new AtomicIntegerArray(N);
		int[] stack = new int [N]; 
		int[] connected_comps  = new int [N]; 
		
		int top=-1;
		ncomps=0;
		
		for(int i=0;i<N ;i++) {
			if (color.get(i) ==1) continue;
			connected_comps[ncomps++]=i;
			stack[++top]=i;
			color.set(i,1);
			while(top!=-1) {
				int v = stack[top];
				top--;
				
				for(int j=0;j<D[v];j++) {
					final int mm = NB[v][j];
					if(color.get(mm)==0){
						top++;
						stack[top]=mm;
						color.set(mm,1);
					}
				}
			}
		}
		
		//System.out.println("ncomps="+ncomps);
		El1 = new E [m+ncomps-1]; 
		
		for(int i=0;i<N;i++) color.set(i,0);
		
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(El[i].v1!=-1) El1[j++]=El[i]; 
		
		//if(j!=m) System.out.println("Remove duplicates failed");
		//else System.out.println("Remove duplicates succeeded,j=m="+j);
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			NB[connected_comps[i]][D[connected_comps[i]]++]=connected_comps[i+1];
			NB[connected_comps[i+1]][D[connected_comps[i+1]]++]=connected_comps[i];
			El1[i+m]=new E (connected_comps[i], connected_comps[i+1]);
		}
		
		G = new V[N];
		for(int i=0;i<N;i++) {
			G[i]=new V();
			G[i].degree=D[i];
			G[i].parent=i;
			G[i].neighbors=new int [D[i]];
			for(j=0;j<D[i];j++)
				G[i].neighbors[j]=NB[i][j];
			//System.out.println("G["+i+"]=" + G[i]);
		}     
		
	}
	
	
	public static final int LABEL_0 = 0;
	static void traverse(final Worker w,  final int u) throws StealAbort {
		TFrame frame = new TFrame(u);
		frame.k=1;
		final V[] G = graph.G;
		final AtomicIntegerArray c = graph.color;
		w.pushFrame(frame);
		int k=0;
		while (k < G[u].degree) {
			int v=G[u].neighbors[k];
			boolean result = c.compareAndSet(v,0,1);
			if (result) {
				G[v].parent=u;
				traverse(w,v);
				w.abortOnSteal();
			}
			++k;
			frame.k=k;
		}
		w.popFrame();
		return;
	}
	
	boolean verifyTraverse(int root) {
		int[] X = new int [N];
		for(int i=0;i<N;i++) X[i]=G[i].parent;
		for(int i=0;i<N;i++) while(X[i]!=X[X[i]]) X[i]=X[X[i]];
		for(int i=0;i<N;i++) {
			
			if(X[i]!=X[0])  
				return false;
		}
		return true;
	}
	static SpanC graph;
	static final long NPS = (1000L * 1000 * 1000);

















class anon_GloballyQuiescentJob : public  GloballyQuiescentJob {
public:
	volatile int PC;
					
protected:
	void compute(Worker *w, Frame *frame) throw StealAbort {
		GFrame f = (GFrame) frame;
		int PC = f.PC;
		f.PC=LABEL_1;
		if (PC==0) {
			atomic_exchange(&graph->color[1],1)
			//graph->color.set(1,1); // TODO 
			traverse(w, 1);
			w->abortOnSteal();
		}
		setupGQReturnNoArg();
	}
					
public:
	int spawnTask(Worker ws) throw StealAbort { 
		return 0;
	}
};


int main(int argc, char *argv[]) {
  int procs;

  if(argc < 2) {
    printf("Usage: %s <threads>\n", argv[0]);
    exit(0);
  }

  procs = atoi(argv[1]);
  cout<<"Number of procs=" << procs <<endl;
  if (argc > 2) 
    Worker::reporting = true;

  Pool *g = new Pool(procs);
  assert(g != NULL);
  
  
  
  int expectedSolutions[] = {0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,73712, 365596, 2279184, 14772512};
    
  for (int i = 0; i < 16; i++) {
    NQueensC::boardSize = i;
    Job *job = new anon_Job1(g);
    assert(job != NULL);
          g->submit(job);
    int result = job->getInt();

    cout<<"NQueens("<<i<<")\t="<<result<<"\t"<<expectedSolutions[i])<<endl;
  }

  g->shutdown();
  delete g;
}
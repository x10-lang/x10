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

class SpanC;
class TFrame;
class Traverser;



static SpanC *graph;
static int[] Ns = {10*1000,50*1000, 100*1000,
		500*1000, 1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000};

typedef vector<int> ARR;
class V {
public:
	int parent;
	int degree;
	ARR  *neighbors;
	V(){}
};

class E {
public:
	int v1,v2;
	bool in_tree;
	E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
};

typedef vector<V *> V_ARR; // Array of vertices
typedef vector<E *> E_ARR; // Array of edges
typedef vector<int> A_ARR; // Atomic Integer Array -- make sure write and reads are visible to all
typedef vector<vector<int> *>  TD_ARR;

class TFrame : public Frame {
private:
	friend class SpanC;
public:
	int u; // vertex
	volatile int k; // TODO Raj -- may need MEM_BARRIER
		
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
};

class Traverser : public Closure {
private:
  friend class TFrame;
  friend class SpanC;
public: 
		SpanC *graph;
		V_ARR *G;
		A_ARR *c; // Atomic Integer Array TODO RAJ
		
		Traverser(SpanC *g, TFrame *t) : Closure(t) { 
			graph=g;
			G=graph->G;
			c=graph->color;
		}
		
		void compute(Worker *w, Frame *frame) /*throw StealAbort*/ {
			TFrame *f = (TFrame *) frame;
			int u = f->u;
			int k = f->k;
			while (k < (G->at(u))->degree) {
				int v=(G->at(u))->neighbors->at(k);
				bool result = compare_exchange (&c[v],0,1);
				if (result) {
					(G->at(v))->parent=u;
					traverse(w,v);
					if (w->abortOnSteal()) return;
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
	V_ARR *G;
	E_ARR *El;
	E_ARR *El1;
	A_ARR *color; // Atomic Integer Array
	int ncomps=0;
	
	
	int N, M;
	
	SpanC (int n, int m){
		N=n;
		M=m;
		
		/*constructing edges*/
		El = new vector<E *>(0);
		double[] seeds = {
				0.21699440277541715, 0.9099040926714322, 0.5586793832519766,
				0.15656203486110076, 0.3716929310972751, 0.6327328452004045,
				0.9854204833301402, 0.8671652950975213, 0.1079976151083556,
				0.5993517714916581
		};
		
		int r0 = 0;
		int r1 = 1;
		for(int i=0;i<M;i++){
			
			E *e = new E ((int) (seeds[r0]*(N+i))%N, (int) (seeds[r1]*(N+i))%N);
			El->push_back(e);
			r0 = r1;
			if (++r1 >= 10) r1 = 0;
			
		}
		
		//int[] D = new int [N];
		ARR *D = new vector<int>(0);
		for(int i=0;i<N;i++) D->push_back(0);
		/* D[i] is the degree of vertex i (duplicate edges are counted).*/
		for(int i=0;i<M;i++){
			((*D)[El->at(i)->v1])++;
			((*D)[El->at(i)->v2])++;
		}
		
		// vector of vector of integers
		
		TD_ARR *NB = new vector<vector<int> *>(0);
		
		//int[][] NB = new int[N][];/*NB[i][j] stores the jth neighbor of vertex i*/
		// leave room for making connected graph by +2
		
		for(int i=0;i<N;i++) {
			
			ARR *a_r = new vector<int>(0);
			for (int j=0; j < D->at(i)+2; j++)
				a_r->push_back(0);
			NB->push_back(a_r); 
		}
		
		/*Now D[i] is the index for storing the neighbors of vertex i
		 into NB[i] NB[i][D[i]] is the current neighbor*/
		for(int i=0;i<N;i++) (*D)[i]=0;
		
		m=0;
		for(int i=0;i<M;i++) {
			bool r=false;;  
			/* filtering out repeated edges*/
			for(int j=0;j<(*D)[El->at(i)->v1] && !r ;j++){ 
				if(El->at(i)->v2==NB->at(El->at(i)->v1)->at(j)) r=true;
			}
			if(r){
				El->at(i)->v1=El->at(i)->v2=-1; /*mark as repeat*/
			} else {
				m++;
				NB->at(El->at(i)->v1)->at(D->at(El->at(i)->v1))=El->at(i)->v2;
				NB->at(El->at(i)->v2)->at(D->at(El->at(i)->v2))=El->at(i)->v1;
				((*D)[El->at(i)->v1])++;
				((*D)[El->at(i)->v2])++;
			}
		}  
		
		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		//color = new AtomicIntegerArray(N);
		color = new vector<int>(N);
		ARR *stack = new vector<int>(N); 
		ARR *connected_comps  = new vector<int>(N); 
		
		int top=-1;
		ncomps=0;
		
		for(int i=0;i<N ;i++) {
			if (atomic_fetch(&((*color)[i])) == 1) continue;
			(*connected_comps)[ncomps++]=i;
			(*stack)[++top]=i;
			atomic_exchange(&((*color)[i]),1);
			while(top!=-1) {
				int v = (*stack)[top];
				top--;
				
				for(int j=0;j<(*D)[v];j++) {
					int mm = NB->at(v)->(j);
					if(atomic_fetch(&((*color)[mm]))==0){
						top++;
						(*stack)[top]=mm;
						atomic_exchange(&((*color)[mm]),1);
					}
				}
			}
		}
		
		//System.out.println("ncomps="+ncomps);
		El1 = new vector<E *> (m+ncomps-1); 
		
		for(int i=0;i<N;i++) atomic_exchange(&((*color)[i]),0);
		
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(El->at(i)->v1!=-1) (*El1)[j++]=El->at(i); 
		
		//if(j!=m) System.out.println("Remove duplicates failed");
		//else System.out.println("Remove duplicates succeeded,j=m="+j);
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			(*(NB->at(connected_comps->at(i))))[((*D)[connected_comps->at(i)])++] = connected_comps->at(i+1);
			(*(NB->at(connected_comps->at(i+1))))[((*D)[connected_comps->at(i+1)])++] = connected_comps->at(i);
			(*El1)[i+m]=new E (connected_comps->at(i), connected_comps->at(i+1));
		}
		
		G = new vector<V *>(0);
		for(int i=0;i<N;i++) {
		
			V *v = new V();
			v->degree=D->at(i);
			v->parent=i;
			v->neighbors=new vector<int>(0);
			
			for(j=0;j<D[i];j++)
				v->neighbors->push_back(NB->at(i)->at(j));
			
			G->push_back(v);
			
			//System.out.println("G["+i+"]=" + G[i]);
		}     
		
	}
	
	
	static const int LABEL_0 = 0;
	static void traverse(Worker *w, int u) /*throws StealAbort*/ {
		TFrame *frame = new TFrame(u);
		frame->k=1;
		V *G = graph->G;
		ARR *c = graph->color; // atomic int array
		w->pushFrame(frame);
		int k=0;
		while (k < G->at(u)->degree) {
			int v=G->at(u)->neighbors->at(k);
			bool result = compare_exchange(&(*c)[v],0,1);
			if (result) {
				G->at(v)->parent=u;
				traverse(w,v);
				if (w->abortOnSteal()) return;
			}
			++k;
			frame->k=k;
		}
		w->popFrame();
		return;
	}
	
	bool verifyTraverse(int root) {
		ARR *X = new vector<int> (N);
		for(int i=0;i<N;i++) (*X)[i]=G->at(i)->parent;
		for(int i=0;i<N;i++) while(X->at(i)!=X->at(X->at(i))) (*X)[i]=X->at(X->at(i));
		for(int i=0;i<N;i++) {
			
			if(X->at(i)!=X->at(0))  
				return false;
		}
		return true;
	}
	
	static const long NPS = (1000L * 1000 * 1000);
};

















class anon_GloballyQuiescentJob : public  GloballyQuiescentJob {
public:
	volatile int PC;
					
protected:
	void compute(Worker *w, Frame *frame) /*throw StealAbort*/ {
		GFrame *f = (GFrame *) frame;
		int PC = f->PC;
		f->PC=LABEL_1;
		if (PC==0) {
			atomic_exchange(&((*(graph->color))[i]),1); 
			traverse(w, 1);
			if (w->abortOnSteal()) return;
		}
		setupGQReturnNoArg();
	}
					
public:
	int spawnTask(Worker *ws) /*throw StealAbort*/ { 
		return 0;
	}
};


int main(int argc, char *argv[]) {
  int result;

  if(argc < 3) {
	printf("Usage: %s <threads> <nRepetitions> \n", argv[0]);
	exit(0);
  }

  const int procs = atoi(argv[1]);
  const int nReps = atoi(argv[2]);
  cout<<"Number of procs=" << procs <<endl;
  if (argc > 2) 
	Worker::reporting = true;
	  
	  
  Pool *g = new Pool(procs);
  assert(g != NULL);
  
  for (int i=0; i < 8; i++) {
  			
    int N = Ns[i], M = 3*N/5;
  	graph = new SpanC(N,M);
  	Pool *g = new Pool(procs);
  	assert(g != NULL);
  			
    System.out.printf("N:%8d ", N);
    for (int k=0; k < 9; ++k) {
    	
    	GloballyQuiescentJob *job = new anon_GloballyQuiescentJob(g);
    	assert(job != NULL);
    	    
    	long long s = nanoTime();
    	    
    	for(int j=0; j<nReps; j++) {
      	  g->submit(job);
    	  //try {
    	  job.waitForCompletion();
    	  //} catch (InterruptedException z) {}
    	}
    	long long t = nanoTime();
    	cout<<"SpanT("<<i<<")\t"<<"verify="<<graph->verifyTraverse(1)<<"\t Time="<<(t-s)/1000/nReps<<"us"<<endl;
    	graph->clearColor();
    	
    }
    g->shutdown();
    delete g;
  }

  
  
  
  
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
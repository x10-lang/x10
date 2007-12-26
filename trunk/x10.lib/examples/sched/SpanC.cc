/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: SpanC.cc,v 1.1 2007-12-26 12:51:27 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/**
 * A C++ version of the recursive SpanT implemented in Java (in
 * x10 cws).  
 */

#include <iostream>
#include <string>

#include <x10/xws/Closure.h>
#include <x10/xws/Cache.h>
#include <x10/xws/Frame.h>
#include <x10/xws/Worker.h>
#include <x10/xws/Job.h>
#include <x10/xws/Pool.h>
#include <x10/xassert.h>
#include <stdlib.h>
#include <vector>



#define READ_1D_VEC(vp,i) (*(vp))[i]
#define WRITE_1D_VEC(vp,i,val) (*(vp))[i]=val
#define READ_2D_VEC(vp,i,j) (*((*(vp))[i]))[j]
#define WRITE_2D_VEC(vp,i,j,val) (*((*(vp))[i]))[j]=val

using namespace std;
using namespace x10lib_xws;

class SpanC;
class TFrame;
class Traverser;
class V;
class E;




static SpanC *graph;
static int Ns[] = {10*1000,50*1000, 100*1000, 500*1000, 1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000};
static const long NPS = (1000L * 1000 * 1000);

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
	TFrame(const TFrame& f) 
	      : Frame(f), u(f.u), k(f.k) {} // Barrier needed as k is volatile TODO RAJ
public:
	int u; // vertex
	volatile int k; // TODO Raj -- may need MEM_BARRIER
		
	TFrame(int u1) {
		u=u1;
	}
	//virtual void setOutletOn(Closure *c) {
	void setOutletOn(Closure *c) {
		// nothing to do
	}
	//virtual Closure *makeClosure();
	Closure *makeClosure();
	
	//virtual TFrame *copy() {
	TFrame *copy() {
		  return new TFrame(*this);
	}
	virtual ~TFrame() {}
};


class SpanC {
public:
	
	int m;
	V_ARR *G;
	E_ARR *El;
	E_ARR *El1;
	A_ARR *color; // Atomic Integer Array
	int ncomps;
	
	
	int N, M;
	
	SpanC (int n, int m){
		N=n;
		M=m;
		ncomps = 0;
		
		/*constructing edges*/
		El = new vector<E *>(M);
		double seeds[] = {
				0.21699440277541715, 0.9099040926714322, 0.5586793832519766,
				0.15656203486110076, 0.3716929310972751, 0.6327328452004045,
				0.9854204833301402, 0.8671652950975213, 0.1079976151083556,
				0.5993517714916581
		};
		
		int r0 = 0;
		int r1 = 1;
		for(int i=0;i<M;i++){
			E *e = new E ((int) (seeds[r0]*(N+i))%N, (int) (seeds[r1]*(N+i))%N);
			WRITE_1D_VEC(El,i,e);
			r0 = r1;
			if (++r1 >= 10) r1 = 0;
			
		}
		
		//int[] D = new int [N];
		ARR *D = new vector<int>(N);
		for(int i=0;i<N;i++) WRITE_1D_VEC(D,i,0);
		/* D[i] is the degree of vertex i (duplicate edges are counted).*/
		for(int i=0;i<M;i++){
			int v_1 = READ_1D_VEC(El,i)->v1;
			int v_2 = READ_1D_VEC(El,i)->v2;
			WRITE_1D_VEC(D,v_1,READ_1D_VEC(D,v_1)+1);
			WRITE_1D_VEC(D,v_2,READ_1D_VEC(D,v_2)+1);
		}
		
		// vector of vector of integers
		
		TD_ARR *NB = new vector<vector<int> *>(0);
		
		//int[][] NB = new int[N][];/*NB[i][j] stores the jth neighbor of vertex i*/
		// leave room for making connected graph by +2
		
		for(int i=0;i<N;i++) {
			
			ARR *a_r = new vector<int>(0);
			for (int j = 0; j < READ_1D_VEC(D,i)+2; j++)
				a_r->push_back(0);
			NB->push_back(a_r); 
		}
		
		/*Now D[i] is the index for storing the neighbors of vertex i
		 into NB[i] NB[i][D[i]] is the current neighbor*/
		for(int i=0;i<N;i++) WRITE_1D_VEC(D,i,0);
		
		m=0;
		for(int i=0;i<M;i++) {
			bool r=false;;  
			/* filtering out repeated edges*/
			for(int j=0;j<READ_1D_VEC(D,READ_1D_VEC(El,i)->v1) && !r ;j++){ 
				if(READ_1D_VEC(El,i)->v2==READ_2D_VEC(NB,READ_1D_VEC(El,i)->v1,j)) r=true;
			}
			if(r){
				READ_1D_VEC(El,i)->v1=-1;
				READ_1D_VEC(El,i)->v2=-1; /*mark as repeat*/
			} else {
				m++;
				int v_1 = READ_1D_VEC(El,i)->v1;
				int v_2 = READ_1D_VEC(El,i)->v2;
				WRITE_2D_VEC(NB,v_1,READ_1D_VEC(D,v_1),v_2); 
				WRITE_2D_VEC(NB,v_2,READ_1D_VEC(D,v_2),v_1);
				WRITE_1D_VEC(D,v_1,READ_1D_VEC(D,v_1)+1);
				WRITE_1D_VEC(D,v_2,READ_1D_VEC(D,v_2)+1);
			}
		}  
		
		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		
		color = new vector<int>(N); // Atomic Integer
		ARR *stack = new vector<int>(N); 
		ARR *connected_comps  = new vector<int>(N); 
		
		for (int i = 0; i < N; i++) {
			WRITE_1D_VEC(color,i,0);
			WRITE_1D_VEC(stack,i,0);
			WRITE_1D_VEC(connected_comps,i,0);
		}
		
		int top=-1;
		ncomps=0;
		
		for(int i=0;i<N ;i++) {
			if (atomic_fetch(&(READ_1D_VEC(color,i))) == 1) continue;
			WRITE_1D_VEC(connected_comps,ncomps++,i);
			WRITE_1D_VEC(stack,++top,i);
			atomic_exchange(&(READ_1D_VEC(color,i)),1);
			while(top!=-1) {
				int v = READ_1D_VEC(stack,top);
				top--;
				
				for(int j=0;j<READ_1D_VEC(D,v);j++) {
					int mm = READ_2D_VEC(NB,v,j);
					if(atomic_fetch(&(READ_1D_VEC(color,mm)))==0){
						top++;
						WRITE_1D_VEC(stack,top,mm);
						atomic_exchange(&(READ_1D_VEC(color,mm)),1);
					}
				}
			}
		}
		
		//System.out.println("ncomps="+ncomps);
		El1 = new vector<E *> (m+ncomps-1); 
		
		for(int i=0;i<N;i++) atomic_exchange(&(READ_1D_VEC(color,i)),0);
		
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(READ_1D_VEC(El,i)->v1!=-1) WRITE_1D_VEC(El1, j++, READ_1D_VEC(El,i)); 
		
		//if(j!=m) System.out.println("Remove duplicates failed");
		//else System.out.println("Remove duplicates succeeded,j=m="+j);
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			int c_1 = READ_1D_VEC(connected_comps,i);
			int c_2 = READ_1D_VEC(connected_comps,i+1);
			int d_1 = READ_1D_VEC(D,c_1);
			int d_2 = READ_1D_VEC(D,c_2);
			
			WRITE_2D_VEC(NB, c_1, d_1, c_2);
			WRITE_1D_VEC(D,c_1,READ_1D_VEC(D,c_1)+1); // Check
							
			WRITE_2D_VEC(NB, c_2, d_2, c_1);
			WRITE_1D_VEC(D,c_2,READ_1D_VEC(D,c_2)+1); // Check
			
			WRITE_1D_VEC(El1,i+m, new E(c_1,c_2));
		}
		
		G = new vector<V *>(0);
		for(int i=0;i<N;i++) {
		
			V *v = new V();
			v->degree=READ_1D_VEC(D,i);
			v->parent=i;
			v->neighbors=new vector<int>(0);
			
			for(j=0;j<READ_1D_VEC(D,i);j++)
				v->neighbors->push_back(READ_2D_VEC(NB,i,j));
			
			G->push_back(v);
			
		}     
		
	}
	
	
	static const int LABEL_0 = 0;
	
	static void traverse(Worker *w, int u) /*throws StealAbort*/ {
		TFrame *frame = new TFrame(u);
		frame->k=1;
		V_ARR *G = graph->G;
		ARR *c = graph->color; // atomic int array
		w->pushFrame(frame);
		int k=0;
		while (k < READ_1D_VEC(G,u)->degree) {
			int v=READ_1D_VEC(READ_1D_VEC(G,u)->neighbors, k);
			bool result = compare_exchange(&(READ_1D_VEC(c,v)),0,1);
			if (result) {
				READ_1D_VEC(G,v)->parent=u;
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
		for(int i=0;i<N;i++) WRITE_1D_VEC(X,i,READ_1D_VEC(G,i)->parent);
		for(int i=0;i<N;i++) 
			while(READ_1D_VEC(X,i)!=READ_1D_VEC(X,READ_1D_VEC(X,i))) 
				WRITE_1D_VEC(X,i,READ_1D_VEC(X,READ_1D_VEC(X,i)));
		for(int i=0;i<N;i++) {
			
			if(READ_1D_VEC(X,i)!=READ_1D_VEC(X,0))  
				return false;
		}
		return true;
	}
	
	
	
	void clearColor() {
			int n = color->size();
			for(int i=0;i<n;i++) atomic_exchange(&(READ_1D_VEC(color,i)),0);
	}
};


//class Traverser : public virtual Closure {
class Traverser : public Closure {

public: 
		SpanC *grph;
		V_ARR *G;
		A_ARR *c; // Atomic Integer Array TODO RAJ
		
		Traverser(SpanC *g, Frame *t) : Closure(t) { 
			grph=g;
			G=grph->G;
			c=grph->color;
		}
		
		//virtual void compute(Worker *w, Frame *frame) /*throw StealAbort*/ {
		void compute(Worker *w, Frame *frame) /*throw StealAbort*/ {
			TFrame *f = (TFrame *) frame;
			int u = f->u;
			int k = f->k;
			while (k < (READ_1D_VEC(G,u)->degree)) {
				int v= READ_1D_VEC((READ_1D_VEC(G,u))->neighbors,k); // Check
				bool result = compare_exchange (&(READ_1D_VEC(c,v)),0,1);
				if (result) {
					(READ_1D_VEC(G,v))->parent=u;
					SpanC::traverse(w,v);
					if (w->abortOnSteal()) return;
				}
				++k;
				f->k=k;
			}
			setupGQReturnNoArg(w);
			return;
		}
		virtual ~Traverser() {} 
};

Closure *TFrame::makeClosure() {
	Closure *retc=new Traverser(graph,this);
	assert (retc != NULL);
	return retc;
}




class anon_GloballyQuiescentJob : public  GloballyQuiescentJob {
public:
	volatile int PC;
					
protected:
	void compute(Worker *w, Frame *frame) /*throw StealAbort*/ {
		GFrame *f = (GFrame *) frame;
		int PC = f->PC;
		f->PC=LABEL_1;
		if (PC==0) {
			atomic_exchange(&(READ_1D_VEC(graph->color,1)),1); 
			SpanC::traverse(w, 1);
			if (w->abortOnSteal()) return;
		}
		setupGQReturnNoArg(w);
	}
					
public:
	anon_GloballyQuiescentJob (Pool *p) : GloballyQuiescentJob(p, new GFrame()){}
	int spawnTask(Worker *ws) /*throw StealAbort*/ { 
		return 0;
	}
};


int main(int argc, char *argv[]) {
  
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
  	MEM_BARRIER();
  	Pool *g = new Pool(procs);
  	assert(g != NULL);
  			
    cout << "N=" << N << endl;
    for (int k=0; k < 9; ++k) {
    	
    	GloballyQuiescentJob *job = new anon_GloballyQuiescentJob(g);
    	assert(job != NULL);
    	    
    	long long s = nanoTime();
    	    
    	for(int j=0; j<nReps; j++) {
      	  g->submit(job);
      	  
    	  job->waitForCompletion();
    	  
    	}
    	long long t = nanoTime();
    	cout<<"SpanT("<<i<<")\t"<<"verify="<<graph->verifyTraverse(1)<<"\t Time="<<(t-s)/1000/nReps<<"us"<<endl;
    	graph->clearColor();
    	
    }
    g->shutdown();
    delete g;
  }
}

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
class V;
class E;




static SpanC *graph;
static int Ns[] = {/*added by raj*/1000,10*1000,50*1000, 100*1000, 500*1000, 1000*1000, 2*1000*1000, 3*1000*1000, 4*1000*1000};
static const long NPS = (1000L * 1000 * 1000);

class V {
public:
	int parent;
	int degree;
	int  *neighbors;
	V(){}
};

class E {
public:
	int v1,v2;
	bool in_tree;
	E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
};


class TFrame : public Frame {
private:
	TFrame(const TFrame& f) 
	      : Frame(f), u(f.u), k(f.k) {} 
public:
	int u; 
	volatile int k; 
		
	TFrame(int u1) : u(u1) {} 
	virtual void setOutletOn(Closure *c) { }
	virtual Closure *makeClosure();
	
	virtual TFrame *copy() {
		  return new TFrame(*this);
	}
	virtual ~TFrame() {}
};


class SpanC {
public:
	
	int m;
	V **G;
	E **El;
	E **El1;
	int *color; // Atomic Integer Array
	int ncomps;
	
	
	int N, M;
	
	SpanC (int n, int m){
		N=n;
		M=m;
		ncomps = 0;
		
		cout << "N=" << n << " M=" << m << endl;
		
		/*constructing edges*/
		El = new E *[M];
		double seeds[] = {
				0.21699440277541715, 0.9099040926714322, 0.5586793832519766,
				0.15656203486110076, 0.3716929310972751, 0.6327328452004045,
				0.9854204833301402, 0.8671652950975213, 0.1079976151083556,
				0.5993517714916581
		};
		
		int r0 = 0;
		int r1 = 1;
		for(int i=0;i<M;i++){
			El[i] = new E((int) (seeds[r0]*(N+i))%N, (int) (seeds[r1]*(N+i))%N);
			r0 = r1;
			if (++r1 >= 10) r1 = 0;
			
		}
		
		int *D = new int[N];
		for(int i=0;i<N;i++) D[i]=0;
		/* D[i] is the degree of vertex i (duplicate edges are counted).*/
		for(int i=0;i<M;i++){
			D[El[i]->v1]++;
			D[El[i]->v2]++;
		}
		
		// vector of vector of integers
		
		int **NB = new int *[N];
		
		for(int i=0;i<N;i++) {
			NB[i] = new int [D[i]+2];
			for(int j=0;j<D[i]+2;j++)
				NB[i][j]=0;
		}
		
		
		/*Now D[i] is the index for storing the neighbors of vertex i
		 into NB[i] NB[i][D[i]] is the current neighbor*/
		for(int i=0;i<N;i++) D[i]=0;
		
		m=0;
		for(int i=0;i<M;i++) {
			bool r=false;;  
			/* filtering out repeated edges*/
			for(int j=0;j< D[El[i]->v1] && !r ;j++){ 
				if(El[i]->v2==NB[El[i]->v1][j]) r=true;
			}
			if(r){
				El[i]->v1=-1;
				El[i]->v2=-1; /*mark as repeat*/
			} else {
				m++;
				NB[El[i]->v1][D[El[i]->v1]]=El[i]->v2;
				NB[El[i]->v2][D[El[i]->v2]]=El[i]->v1;
				D[El[i]->v1]++;
				D[El[i]->v2]++;
			}
		}  
		
		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		
		color = new int[N]; // Atomic Integer
		int *stack = new int [N]; 
		int *connected_comps  = new int [N];
		
		for (int i = 0; i < N; i++) {
			color[i]=0;
			stack[i]=0;
			connected_comps[i]=0;
		}
		
		int top=-1;
		ncomps=0;
		
		for(int i=0;i<N ;i++) {
			if (atomic_fetch(&color[i]) == 1) continue;
			connected_comps[ncomps++]=i;
			stack[++top]=i;
			atomic_exchange(&color[i],1);
			while(top!=-1) {
				int v = stack[top];
				top--;
				
				for(int j=0;j<D[v];j++) {
					int mm = NB[v][j];
					if(atomic_fetch(&color[mm])==0){
						top++;
						stack[top]=mm;
						atomic_exchange(&color[mm],1);
					}
				}
			}
		}
		
		//System.out.println("ncomps="+ncomps);
		El1 = new E *[m+ncomps-1]; 
		
		for(int i=0;i<N;i++) atomic_exchange(&color[i],0);
		
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(El[i]->v1!=-1) El1[j++]= El[i]; 
		
		//if(j!=m) System.out.println("Remove duplicates failed");
		//else System.out.println("Remove duplicates succeeded,j=m="+j);
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			NB[connected_comps[i]][D[connected_comps[i]]++]=connected_comps[i+1];
			NB[connected_comps[i+1]][D[connected_comps[i+1]]++]=connected_comps[i];
			El1[i+m]=new E (connected_comps[i], connected_comps[i+1]);
		}
		
		G = new V*[N];
		for(int i=0;i<N;i++) {
			G[i]=new V();
			G[i]->degree=D[i];
			G[i]->parent=i;
			G[i]->neighbors=new int [D[i]];
			for(j=0;j<D[i];j++)
				G[i]->neighbors[j]=NB[i][j];
			
		}
		
	}
	
	
	static const int LABEL_0 = 0;
	
	static void traverse(Worker *w, int u) /*throws StealAbort*/ {
		TFrame *frame = new TFrame(u);
		frame->k=1;
		V **G = graph->G;
		int *c = graph->color; // atomic int array
		w->pushFrame(frame);
		int k=0;
		while (k < G[u]->degree) {
			int v=G[u]->neighbors[k];
			bool result = compare_exchange(&c[v],0,1);
			if (result) {
				G[v]->parent=u;
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
		int *X = new int [N];
		for(int i=0;i<N;i++) X[i]=G[i]->parent;
		for(int i=0;i<N;i++) while(X[i]!=X[X[i]]) X[i]=X[X[i]];
		for(int i=0;i<N;i++) {
			
			if(X[i]!=X[0])  
				return false;
		}
		return true;
	}
	
	
	
	void clearColor() {
			for(int i=0;i<N;i++) atomic_exchange(&color[i],0);
	}
};


class Traverser : public virtual Closure {

public: 
		SpanC *grph;
		V **G;
		int *c; // Atomic Integer Array TODO RAJ
		
		Traverser(SpanC *g, Frame *t) : Closure(t) { 
			grph=g;
			G=grph->G;
			c=grph->color;
		}
		//virtual bool requiresGlobalQuiescence() /*const*/ { cout << "calling Traverser::requires"<< endl;return true; }
		virtual void compute(Worker *w, Frame *frame) /*throw StealAbort*/ {
			TFrame *f = dynamic_cast<TFrame *>(frame);
			int u = f->u;
			int k = f->k;
			while (k < G[u]->degree) {
				int v= G[u]->neighbors[k]; // Check
				bool result = compare_exchange (&c[v],0,1);
				if (result) {
					G[v]->parent=u;
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
	//virtual bool requiresGlobalQuiescence() /*const*/ { return true; }
					
protected:
	void compute(Worker *w, Frame *frame) /*throw StealAbort*/ {
		GFrame *f = dynamic_cast<GFrame *>(frame);
		int PC = f->PC;
		f->PC=LABEL_1;
		if (PC==0) {
			atomic_exchange(&graph->color[1],1); 
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
  
  for (int i=0; i < 1; i++) {
  			
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
    	//cout << "Submitting the job" << endl;    
    	for(int j=0; j<nReps; j++) {
      	  g->submit(job);
      	  //cout << "Waiting for the completion of the job" << endl;
    	  //try {
    	  job->waitForCompletion();
    	  //} catch (InterruptedException z) {}
    	}
    	long long t = nanoTime();
    	cout<<"SpanT("<<i<<")\t"<<"verify="<<graph->verifyTraverse(1)<<"\t Time="<<(t-s)/1000000/nReps<<"ms"<<endl;
    	graph->clearColor();
    	
    }
    //cout << " going to block " << endl;
    g->shutdown();
    delete g;
  }
}

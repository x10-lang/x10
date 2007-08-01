







/**
 * A C++ version of the recursive NQueens implemented in Java (in
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

class NFrame;
class NQueensC;



//typedef vector<int> ARR;

static int boardSize;


class anon_Outlet1 : public virtual Outlet {
private:
  NFrame *f;
  Closure *c;
public:
  anon_Outlet1(NFrame *f, Closure *c) {
    this->f = f;
    this->c = c;
  }
  void run();
};

class NFrame : public Frame {
private:
  friend class anon_Outlet1;
  
  // assigning sofar with f.sofar may not be appropriate for stack allocation
  NFrame(const NFrame& f) 
      : Frame(f), sum(f.sum), PC(f.PC), q(f.q), ownerClosure(f.ownerClosure) {
	  /*sofar=new int[f.sofar_size]; 
	  for (int k = 0; k < f.sofar_size; ++k)
	   sofar[k]=f.sofar[k];*/
	  sofar = f.sofar;
	  sofar_size = f.sofar_size;
  } // Barrier needed as q and PC are volatile TODO RAJ
  
public:
  volatile int PC;
  volatile int q;
  int sum;
  //ARR *sofar; 
  int *sofar;
  int sofar_size;
  Closure *ownerClosure;
  
  
  //NFrame(ARR *a) { sofar = a; }
  NFrame(int *a, int a_size, Closure* cl) { 
	  /*sofar=new int[a_size]; 
	  for (int k = 0; k < a_size; ++k)
		  sofar[k]=a[k];*/
	  sofar = a;
	  sofar_size = a_size; 
	  ownerClosure=cl;
  }
  virtual Closure *makeClosure();
  virtual NFrame *copy() {
	  return new NFrame(*this);
  }
  virtual ~NFrame() { /*delete sofar;*/}
  virtual void setOutletOn(Closure *c) {
	  Outlet *o = new anon_Outlet1(this,c);
	  assert (o!= NULL);
	  c->setOutlet(o);
  }
};

class NQueensC : public virtual Closure {
	
private:
  friend class NFrame;
  friend class anon_Outlet1;
  
private:
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2, LABEL_3=3;

public:
	
  
	
  // fast path execution
  //static int nQueens(Worker *w, ARR *a) {	
  static int nQueens(Worker *w, int *a, int a_size, Closure *cl) {
	  
	  int row = a_size;
	  //int row = a->size();
	  
	  //cout << endl << "Fast path with row=" << row << endl;
	  //cout << "\n row=" << row << " boardSize=" << boardSize << endl;
	  if (row >= boardSize) {
		  //cout << endl << "row >= boardsize -- returning" << endl;
	  	return 1;
	  }
	  
	  NFrame nFrame(a,a_size, cl);
	  NFrame *frame = &nFrame;
	  frame->q=1; // TODO  RAJ Barrier needed as q is volatile
	  frame->sum=0;
	  frame->PC=LABEL_1; // TODO RAJ Barrier needed as PC is volatile
	  //MEM_BARRIER();
	  w->pushFrame(frame);
	  int sum=0;
	  int q=0;
	  
	  
	  while (q < boardSize) {
		  bool attacked = false;
		  for (int i = 0; i < row && ! attacked; i++) {
			  int p = a[i];
			  attacked = (q == p || q == p - (row - i) || q == p + (row - i));
	  	  }
		  if (!attacked) { 
			  //ARR *next = new vector<int> (0);
			  int *next = new int[row+1];
			  
			  //cout << "sizeof(next)=" << next.size() << endl;
			  for (int k = 0; k < row; ++k)
				  next[k]=a[k]; // next->push_back(a->at(k));
			  next[row]=q; // next->push_back(q);
			  
			  //cout << "sizeof(next)=" << next.size() << endl; 
	  				
			  int y = nQueens(w, next, row+1, cl);
			  //int y =  nQueens(w, next);
			  
			  if (w->abortOnSteal(y)) return -1;
			  if(w->cache->parentInterrupted()) {
			        w->lock(w);
			        NQueensC *ocl = dynamic_cast<NQueensC*>(frame->ownerClosure);
			        if(ocl) 
			        	frame = dynamic_cast<NFrame *>(ocl->frame);
			        w->unlock();
			        
			        assert(frame != NULL);
			  }
			  sum +=y;
			  frame->sum +=y;
			  //MEM_BARRIER();
		  }
		  //MEM_BARRIER();
		  q++; 
		  
		  frame->q=q+1; // TODO RAJ Barrier needed as q is volatile
		  //MEM_BARRIER();
	  }
	  //MEM_BARRIER();
	  w->popFrame();
	  /*if(!w->cache->interrupted()) {
	        //delete frame;
	  }*/
	  if(w->cache->interrupted()) {
	        w->lock(w);
	        //wait for promotion of child, before the stacked frame is deleted/popped
	        w->unlock();
	   }
	  return sum;
	  
  }
  
  NQueensC(NFrame *frame) : Closure(frame) { frame->ownerClosure = this; }
  ~NQueensC() {    /*delete frame;*/  }
  // Slow path
  virtual void compute(Worker *w, Frame *frame)  {
	  
	  NFrame *f = (NFrame *) frame;
	  int *a = f->sofar;
	  int row = f->sofar_size;
	  //int row = a->size();
	  int sum=0;
	  int q;
	  
	  //cout << endl << "Slow path with row=" << row << endl;
	  		
	  switch (f->PC) {
	  	case LABEL_0:
	  		if (row >= boardSize) {
	  			result =1;
	  			setupReturn(w);
	  			return;
	  		}
	  	case LABEL_1: 
	  		q=f->q;
	  		sum=0;
	  		while (q < boardSize) {
	  			f->q =q+1;
	  			bool attacked = false;
	  			for (int i = 0; i < row && ! attacked; i++) {
	  				int p = a[i];
	  				attacked = (q == p || q == p - (row - i) || q == p + (row - i));
	  			}
	  			if (!attacked) {
	  				
	  				//ARR *next = new vector<int> (0);
	  				int *next = new int[row+1];
	  							  
	  				//cout << "sizeof(next)=" << next.size() << endl;
	  				for (int k = 0; k < row; ++k)
	  				  next[k]=a[k]; // next->push_back(a->at(k));
	  				next[row]=q; // next->push_back(q);
	  							  			  
	  				//cout << "sizeof(next)=" << next.size() << endl; 
	  					  				  				
	  				int y = nQueens(w, next, row+1, this);
	  				//int y = nQueens(w, next);
	  				
	  				if (w->abortOnSteal(y)) return;
	  				sum += y;
	  				// this cannot be f.sum=y. f.sum may have been updated by other
	  				// joiners in the meantime.
	  				f->sum +=y;
	  				//MEM_BARRIER();
	  			}
	  			q++;
	  		}
	  		f->PC=LABEL_2;
	  		//MEM_BARRIER();
	  		if (sync(w))
	  				return;
	  	case LABEL_2:
	  		result=f->sum;
	  		setupReturn(w);
	  		break;
	  	default: 
	  		assert(0);
	  }
	  return;
  }
protected:
  int result;
  
public:
  virtual int resultInt() { return result;}
  virtual void setResultInt(int x) { result=x;}
  
};



void anon_Outlet1::run() {
	NFrame *fr = (NFrame *) c->parentFrame(); // should not f do instead of fr? TODO RAJ
	int value = c->resultInt();
	fr->sum += value;
}

Closure *NFrame::makeClosure() {
	NFrame *f=copy();
	//MEM_BARRIER();
	NQueensC *c = new NQueensC(f);
	assert(c != NULL);
	ownerClosure = c;
	return c;
}

class anon_Job1 : public Job {
private:
  int result;
public:
  anon_Job1(Pool *g) : Job(g) {}
  virtual void setResultInt(int x) { result = x;}
  virtual int resultInt() { return result;}
  virtual int spawnTask(Worker *ws) { 
	  int *a = new int[0]; 
	  return NQueensC::nQueens(ws, a, 0, this); 
	  /* TODO delete a; */
  }
  void jobCompleted() { 
      //cerr<<"Job::result="<<result<<endl; 
      Job::jobCompleted(); 
  }
  virtual ~anon_Job1() {}

protected:
  
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
  
  const int expectedSolutions[] = {0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,73712, 365596, 2279184, 14772512};
  
  
  Pool *g = new Pool(procs);
  assert(g != NULL);

   
  long sc = 0, sa = 0;
  for (int i = 1; i < 12; i++) {
	boardSize = i;
	MEM_BARRIER(); //Sriram: How do we guarantee all threads can see the new boardsize?
    long long s = nanoTime();
    
    for(int j=0; j<nReps; j++) {
    	anon_Job1 job(g);
        g->submit(&job);
        result = job.getInt();
    }
    long long t = nanoTime();
    cout<<"C++CWS NQueens("<<i<<")" << "\t" <<(t-s)/1000000/nReps
    			<<" ms" << "\t" << (result == expectedSolutions[i] ? "ok" : "fail") 
    			<< "\t" << "steals="<< ((g->getStealCount()-sc)/nReps) 
    			<< "\t"  << "stealAttempts=" << ((g->getStealAttempts()-sa)/nReps)<<endl;
    
    sc=g->getStealCount();
    sa=g->getStealAttempts();
  }
  
  g->shutdown();
  delete g; 
}

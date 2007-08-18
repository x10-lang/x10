







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
#include <alloca.h>

using namespace std;
using namespace x10lib_cws;

class NFrame;
class NQueensC;





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
  
  
  NFrame(const NFrame& f) 
      : Frame(f), sum(f.sum), PC(f.PC), q(f.q), ownerClosure(f.ownerClosure), sofar_size(f.sofar_size) {
      //int *sofar = (int *)malloc((sofar_size) * sizeof(int));
	  //cout << "SOFAR_SIZE=" << sofar_size;
	  sofar = new int[sofar_size];
	  assert(sofar!=NULL);
	  memcpy(sofar, f.sofar, sofar_size * sizeof(int));
  } 
  
public:
  volatile int PC;
  volatile int q;
  int sum;
   
  int *sofar;
  int sofar_size;
  Closure *ownerClosure;
  
  NFrame(int *a, int a_size, Closure* cl) { 
	  sofar_size = a_size;
	  sofar = a; 
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
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2;
public:

// fast path execution
static int nQueens(Worker *w, int *a, int a_size, Closure *cl) {
	  int row = a_size;
	  if (row >= boardSize) {
		  return 1;
	  }
	  NFrame nFrame(a,a_size, cl);
	  NFrame *frame = &nFrame;
	  frame->q=1; 
	  frame->sum=0;
	  frame->PC=LABEL_1; 
	  w->pushFrame(frame);
	  
	  int sum=0;
	  int q=0;
	  
	  while (q < boardSize) {
		  bool attacked = false;
		  for (int i = 0; i < row && !attacked; i++) {
			  int p = a[i];
			  attacked = (q == p || q == p - (row - i) || q == p + (row - i));
	  	  }
		  if (!attacked) { 
			  int *next = (int *)alloca((row + 1) * sizeof(int));
			  assert(next!=NULL);
			  memcpy(next, a, row * sizeof(int));
			  next[row] = q;
			  
			  int y = nQueens(w, next, row+1, cl);
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
		  }
		  q++;
		  frame->q=q+1; 
	  }
	  w->popFrame();
	  if(w->cache->parentInterrupted()) {
	    w->lock(w);
	    // wait for promotion of child, before the stacked frame is deleted
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
	  
	  int sum=0;
	  int q;
	  
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
	  				int *next = (int *)alloca((row + 1) * sizeof(int));
	  				assert (next != NULL);
	  				memcpy(next, a, row * sizeof(int));
	  				next[row] = q;
	  				int y = nQueens(w, next, row+1, this);	
	  				if (w->abortOnSteal(y)) return;
	  				if(w->cache->parentInterrupted()) {
	  					w->lock(w);
	  					NQueensC *ocl = dynamic_cast<NQueensC*>(f->ownerClosure);
	  					if(ocl) 
	  						f = dynamic_cast<NFrame *>(ocl->frame);
	  					w->unlock();
	  					assert(f != NULL);
	  				}
	  				sum += y;
	  				f->sum +=y;
	  			}
	  			q++;
	  		}
	  		f->PC=LABEL_2;
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
	NFrame *fr = (NFrame *) c->parentFrame(); 
	int value = c->resultInt();
	fr->sum += value;
}

Closure *NFrame::makeClosure() {
	NQueensC *c = new NQueensC(copy());
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
	  int *a = (int *)alloca(boardSize * sizeof(int));
	  assert(a!=NULL);
	  return NQueensC::nQueens(ws, a, 0, this); 
	  
  }
  void jobCompleted() { 
	  Job::jobCompleted(); 
  }
  virtual ~anon_Job1() {}
};

int main(int argc, char *argv[]) {
  int result;

  if(argc < 4) {
	    printf("Usage: %s <threads> <nRepetitions> <input-number>\n", argv[0]);
	    exit(0);
  }

  const int procs = atoi(argv[1]);
  const int nReps = atoi(argv[2]);
  const int n = atoi(argv[3]);
  
  //cout<<"Number of procs=" << procs <<endl;
  if (argc > 2) 
	    Worker::reporting = true;
  
  const int expectedSolutions[] = {0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,73712, 365596, 2279184, 14772512};
  
  
  Pool *g = new Pool(procs);
  assert(g != NULL);

   
  long sc = 0, sa = 0;
  //for (int i = 11; i < 16; i++) {
  int i  = n;
  boardSize = i;
  MEM_BARRIER(); //Sriram: How do we guarantee all threads can see the new boardsize?
  long long minT;
  for(int j=0; j<nReps; j++) {
    anon_Job1 job(g);
    long _ssc = g->getStealCount();
    long _ssa = g->getStealAttempts();
    long long s = nanoTime();
    g->submit(&job);
    result = job.getInt();
    long long t = nanoTime();
    long _tsc = g->getStealCount();
    long _tsa = g->getStealAttempts();

    if(j==0 || minT>(t-s)) {
      minT = t-s;
      sa = _tsa - _ssa;
      sc = _tsc - _ssc;
    }
  }

    cout<<"nprocs="<<procs
	<<" NQueensCStack_a("<<i<<")" << "\t" <<minT/1000<<" us" << "\t"
    	<< ((result == expectedSolutions[i]) ? "ok" : "fail" )
    	<< "\t" << " steals="<< sc
        << "\t" << "stealAttempts=" << sa
	<<endl;

    sc=g->getStealCount();
    sa=g->getStealAttempts();
  //}
  
  g->shutdown();
  delete g; 
}

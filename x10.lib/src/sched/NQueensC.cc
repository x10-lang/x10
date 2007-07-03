







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



typedef vector<int> ARR;



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
      : Frame(f), sum(f.sum), sofar(f.sofar), PC(f.PC), q(f.q) {} // Barrier needed as q and PC are volatile TODO RAJ
public:
  volatile int PC;
  volatile int q;
  int sum;
  ARR *sofar;
  
  
  NFrame(ARR *a) { sofar = a; }
  virtual Closure *makeClosure();
  virtual NFrame *copy() {
	  return new NFrame(*this);
  }
  virtual ~NFrame() {}
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
  static int nQueens(Worker *w, ARR *a) {
	  
	  int row = a->size();
	  
	  //cout << endl << "Fast path with row=" << row << endl;
	  //cout << "\n row=" << row << " boardSize=" << boardSize << endl;
	  if (row >= boardSize) {
		  //cout << endl << "row >= boardsize -- returning" << endl;
	  			return 1;
	  }
	  
	  
	  NFrame *frame = new NFrame(a);
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
			  int p = a->at(i);
			  attacked = (q == p || q == p - (row - i) || q == p + (row - i));
	  	  }
		  if (!attacked) { 
			  ARR *next = new vector<int> (0);
			  
			  //cout << "sizeof(next)=" << next.size() << endl;
			  for (int k = 0; k < row; ++k) 
				  next->push_back(a->at(k));
			  next->push_back(q);
			  
			  //cout << "sizeof(next)=" << next.size() << endl; 
	  				
			  int y = nQueens(w, next);
			  
			  if (w->abortOnSteal(y)) return -1;
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
	  if(!w->cache->interrupted()) {
	        delete frame;
	  }
	  return sum;
	  
  }
  
  NQueensC(Frame *frame) : Closure(frame) {}
  // Slow path
  virtual void compute(Worker *w, Frame *frame)  {
	  
	  NFrame *f = (NFrame *) frame;
	  ARR *a = f->sofar;
	  int row = a->size();
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
	  				int p = a->at(i);
	  				attacked = (q == p || q == p - (row - i) || q == p + (row - i));
	  			}
	  			if (!attacked) {
	  				
	  				ARR *next = new vector<int>(0);
	  				for (int k = 0; k < row; ++k) 
	  					next->push_back(a->at(k));
	  				next->push_back(q);
	  					  				
	  				int y = nQueens(w, next);
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
	Closure *c = new NQueensC(this);
	assert(c != NULL);
	return c;
}

class anon_Job1 : public Job {
private:
  int result;
public:
  anon_Job1(Pool *g) : Job(g) {}
  virtual void setResultInt(int x) { result = x;}
  virtual int resultInt() { return result;}
  virtual int spawnTask(Worker *ws) { ARR *a = new vector<int>(0); return NQueensC::nQueens(ws, a); /* TODO delete a; */}
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

    
  for (int i = 1; i < 13; i++) {
	Pool *g = new Pool(procs);
	assert(g != NULL);
    boardSize = i;
    Job *job = new anon_Job1(g);
    assert(job != NULL);
    
    long long s = nanoTime();
    
    for(int j=0; j<nReps; j++) {
    	
        g->submit(job);
        result = job->getInt();
    }
    long long t = nanoTime();
    cout<<"NQueens("<<i<<")\t="<<result<<"\t"<<
    expectedSolutions[i]<<"\t Time="<<(t-s)/1000/nReps<<"us"<<endl;
    g->shutdown();
    delete g;
  }

  
}
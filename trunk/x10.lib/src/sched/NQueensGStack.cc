/**
 * A C++ recursive NQueens code with Global Quiescence. Note that this
 * is distinct from the Java NQueensG.java code in x10 cws.  
 * @author Sriram Krishnamoorthy
 */

#include <iostream>
#include <string>

#include "Cache.h"
#include "Frame.h"
#include "Worker.h"
#include "Job.h"
#include "Pool.h"
#include "Sys.h"
#include "Lock.h"
#include <assert.h>
#include <stdlib.h>
#include <vector>

using namespace std;
using namespace x10lib_cws;

static const int expectedSolutions[] = {
  0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
  73712, 365596, 2279184, 14772512};

static int boardSize;

class NFrame : public Frame {
protected:
  NFrame(const NFrame &f) 
  : sofar_size(f.sofar_size) {
    q = f.q;
    PC = f.PC;
    sum = f.sum;

    sofar = new int[sofar_size];
    assert(sofar != NULL);

    for(int i=0; i<sofar_size; i++)
      sofar[i] = f.sofar[i];
  }

  static int locAbortOnSteal(Worker *w, int y, NFrame *f) {
    if(w->cache->interrupted()) {
      if(!w->hasThrownException()) {
	//cout<<w->index<<":: aborting. len="<<f->sofar_size<<" q="<<f->q<<" sum="<<f->sum<<" y="<<y<<endl;
	w->throwException();
	accumulateResultInt(y, w);
      }
      else {
	assert(y == -1); /*we are rethrowing/unwinding. So the return value should be -1*/
	//cout<<w->index<<":: unwinding stack. len="<<f->sofar_size<<" q="<<f->q<<" sum="<<f->sum<<" y="<<y<<endl;
      }
      return 1;
    }
    return 0;
  }
  
  static void setupGQFrameReturn(Worker *w, int x) {
    w->lock(w);
    accumulateResultInt(x, w);
    w->popFrame();
    w->unlock();
  }

public: 
  int *sofar;
  const int sofar_size;
  volatile int q;
  volatile int sum;

  static const int LABEL_0=0, LABEL_1=1, LABEL_2=2, LABEL_3=3;
public:
  volatile int PC;

  NFrame(int *a, int length)
    : sofar(a), sofar_size(length), q(0), PC(LABEL_0), sum(0) {
    MEM_BARRIER();
  }

  NFrame *copy() {
    return new NFrame(*this);
  }

  static void accumulateResultInt(int x, Worker *w) {
    //cout<<w->index<<":: accumulating x="<<x<<endl;
    w->currentJob()->accumulateResultInt(x);
  }

  /*fast version*/
  static int nQueens(Worker *w, int *a, int length) {
    const int row = length;
    int result;

    if(row >= boardSize) {
      return 1;
    }
    NFrame f(a, length);
    NFrame *frame = &f;
    //NFrame *frame = new NFrame(a, length);
    frame->q = 0;
    frame->sum = 0;
    frame->PC = LABEL_1;
    w->pushFrame(frame);
    
    int q = 0;
    while(q<boardSize) {
      frame->q += 1;
      assert( q+1 == frame->q);
      bool attacked = false;
      for(int i=0; i<row && !attacked; i++) {
	int p = a[i];
	attacked = ((q==p) || (q==p-(row-i)) || (q==p+(row-i)));
      }
      if(!attacked) {
// 	int *next = new int[row+1];
	int next[row+1];
	assert(next != NULL);
	for(int k=0; k<row; ++k) {
	  next[k] = a[k];
	}
	next[row] = q;

	const int y = nQueens(w, next, row+1);
	/*no abort on steal. That messes with closures*/
	if(locAbortOnSteal(w, y, frame)) 
	  return -1;
	frame->sum += y;
      }
      q++;
      //frame->q = q+1;      
    }
    w->popFrame();
    if(w->cache->parentInterrupted()) {
      w->lock(w);
      w->unlock();
    }
    return frame->sum;
  }

  /*slow version*/
  virtual void compute(Worker *w) {
    const int * a = this->sofar;
    int row = this->sofar_size;
    int result;
    int q1 = this->q;

    //cout<<w->index<<":: compute .row="<<row<<" q="<<q<<" sum="<<sum<<endl;

    switch(PC) {
    case LABEL_0:
      this->PC = LABEL_1;
      if(row>= boardSize) {
	result = 1;
	setupGQFrameReturn(w, result);
	//accumulateResultInt(result, w);
	//w->cache->reset();
	return;
      }
      q1=0;
      assert(this->q==0);
      assert(this->sum==0);
    case LABEL_1:
      //int sum=0;
      assert(!w->hasThrownException());
      while (q1 < boardSize) {
	this->q += 1;
	assert(this->q == q1+1);
      
	bool attacked = false;
	for (int i = 0; i < row && ! attacked; i++) {
	  int p = a[i];
	  attacked = (q1 == p || q1 == p - (row - i) || q1 == p + (row - i));
	}
	if (!attacked) { 
	  int* next = new int[row+1];
	  assert(next != NULL);
	  for (int k = 0; k < row; ++k) 
	    next[k] = a[k];
	  next[row] = q1;
	  
	  int y = nQueens(w, next, row+1);
	  if(locAbortOnSteal(w, y, this)) {
	    return;
	  }

	  //sum += y;
	  // this cannot be f.sum=y. f.sum may have been updated by other
	  // joiners in the meantime.
	  //cout<<w->index<<":: add-ing to sum. row="<<row<<" q="<<q<<" y="<<y<<" sum="<<sum<<endl;
	  sum +=y;
	}
	q1++;
	//this->q = q1+1;
      }
      result=sum;
      //cout<<w->index<<":: compute acc-ing sum="<<sum<<endl;
      //accumulateResultInt(sum, w);
      setupGQFrameReturn(w, sum);
      //w->cache->reset();
      break;
    default: /*should not be here*/
      assert(0);
      abort();
    }
  }
};

class NQueensJ : public GloballyQuiescentVoidJob {
private:
  volatile int result;
public:
  NQueensJ(Pool *g)
    : GloballyQuiescentVoidJob(g, new NFrame(NULL, 0)), result(0) {
    MEM_BARRIER();
  }
  virtual void accumulateResultInt(int x) {
    atomic_add(&result, x);
  }
  virtual int resultInt() { return result; }
};


int main(int argc, char *argv[]) {
  if(argc < 4) {
    printf("Usage: %s <threads> <nRepetitions> <input-number> \n", argv[0]);
    exit(0);
  }

  const int procs = atoi(argv[1]);
  const int nReps = atoi(argv[2]);
  const int ni = atoi(argv[3]);

  Pool *g = new Pool(procs);
  assert(g != NULL);

  //for(int i=5; i<6; i++) {
  int i = ni;
    boardSize = i;
    MEM_BARRIER();

    long sc = 0, sa = 0;
    
    Job *job = new NQueensJ(g);
    assert(job != NULL);
    long long s = nanoTime();
    g->submit(job);
    int result = job->getInt();
    long long t = nanoTime();

    delete job;

    cout<<"NQueens("<<i<<")="<<result<<"\t"
	<<expectedSolutions[i]
	<<(result == expectedSolutions[i]?"ok":"fail")<<"\t"
	<<"\t Time="<<(t-s)/1000<<"us"
	<< "\t" << "steals="<< ((g->getStealCount()-sc)/nReps) 
	<< "\t"  << "stealAttempts=" << ((g->getStealAttempts()-sa)/nReps)
	<<endl;
    //}
  g->shutdown();
  delete g;
}


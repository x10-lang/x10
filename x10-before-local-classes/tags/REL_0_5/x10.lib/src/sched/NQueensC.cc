







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
  
  NFrame(const ARR *a) { sofar = a; }
  Closure *makeClosure();
  NFrame *copy() {
	  return new NFrame(*this);
  }
}

class NQueensC : public Closure {
	
private:
  friend class NFrame;
  friend class anon_Outlet1;
  
private:
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2, LABEL_3=3;

public:
	
  static int boardSize;
	
  // fast path execution
  static int nQueens(Worker *w, const ARR *a) {
	  int row = a->size();
	  if (row >= boardSize) {
	  			return 1;
	  }
	  
	  NFrame *frame = new NFrame(a);
	  frame->q=1; // TODO  RAJ Barrier needed as q is volatile
	  frame->sum=0;
	  frame->PC=LABEL_1; // TODO RAJ Barrier needed as PC is volatile
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
			  ARR next(row+1);
			  for (int k = 0; k < row; ++k) 
				  next[k] = a[k];
			  next[row] = q;
	  				
			  int y = nQueens(w, &next);
			  
			  w->abortOnSteal(y);
			  sum +=y;
			  frame->sum +=y;
		  }
		  q++; 
		  frame->q=q+1; // TODO RAJ Barrier needed as q is volatile
	  }
	  w->popFrame();
	  return sum;
	  
  }
  // Slow path
  void compute(Worker *w, Frame *frame)  {
	  NFrame *f = (NFrame) frame;
	  ARR *a = f->sofar;
	  int row = a->size();
	  		
	  switch (f->PC) {
	  	case LABEL_0:
	  		if (row >= boardSize) {
	  			result =1;
	  			setupReturn();
	  			return;
	  		}
	  	case LABEL_1: 
	  		int q=f->q;
	  		int sum=0;
	  		while (q < boardSize) {
	  			f->q =q+1;
	  			bool attacked = false;
	  			for (int i = 0; i < row && ! attacked; i++) {
	  				int p = a[i];
	  				attacked = (q == p || q == p - (row - i) || q == p + (row - i));
	  			}
	  			if (!attacked) {
	  				
	  				ARR next(row+1);
	  				for (int k = 0; k < row; ++k) 
	  					next[k] = a[k];
	  				next[row] = q;
	  					  				
	  				int y = nQueens(w, &next);
	  				w->abortOnSteal(y);
	  				sum += y;
	  				// this cannot be f.sum=y. f.sum may have been updated by other
	  				// joiners in the meantime.
	  				f->sum +=y;	
	  			}
	  			q++;
	  		}
	  		f->PC=LABEL_2;
	  		if (w->sync())
	  				return;
	  	case LABEL_2:
	  		result=f->sum;
	  		setupReturn();
	  }
	  return;
  }
protected:
  int result;
  
public:
  int resultInt() { return result;}
  void setResultInt(int x) { result=x;}
  
}



void anon_Outlet1::run() {
	NFrame *fr = (NFrame) c->parentFrame(); // should not f do instead of fr? TODO RAJ
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
  void setResultInt(int x) { result = x;}
  int resultInt() { return result;}
  int spawnTask(Worker *ws) { ARR a; return NQueensC::nQueens(ws, &a); }

protected:
  
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
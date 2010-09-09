/**
 * A C++ version of the recursive Fibonacci implemented in Java (in
 * x10 cws).  
 * @author Sriram
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

using namespace std;
using namespace x10lib_cws;

class FibFrame;
class FibC;

class anon_Outlet1 : public virtual Outlet {
private:
  FibFrame *f;
  Closure *c;
public:
  anon_Outlet1(FibFrame *f, Closure *c) {
    this->f = f;
    this->c = c;
  }
  void run();
};

class anon_Outlet2 : public virtual Outlet {
private:
  FibFrame *f;
  Closure *c;
public:
  anon_Outlet2(FibFrame *f, Closure *c) {
    this->f = f;
    this->c = c;
  }
  void run();
};



class FibFrame : public Frame {
private:
  friend class anon_Outlet1;
  friend class anon_Outlet2;
public: //instead of Java package access
  const int n;
  int x,y;
public:
  volatile int PC;

  FibFrame(int _n) : n(_n), x(0), y(0) {}
  void setOutletOn(Closure *c) {
    assert(PC==LABEL_1 || PC==LABEL_2);
    Outlet *o;
    if(PC==LABEL_1) 
      o = new anon_Outlet1(this, c);
    else
      o = new anon_Outlet2(this, c);
	assert(o != NULL);
    c->setOutlet(o);
  }

  Closure *makeClosure();
  FibFrame *copy() {
    return new FibFrame(*this);
  }

private:
  FibFrame(const FibFrame& f) 
    : Frame(f), n(f.n), x(f.x), y(f.y), PC(f.PC) {}
};

class FibC : public Closure {
private:
  friend class FibFrame;
  friend class anon_Outlet1;
  friend class anon_Outlet2;
private:
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2, LABEL_3=3;

public:
  static int realfib(int n) {
    if(n<2) return n;
    int y=0, x=1;

    for(int i=0; i<=n-2; i++) {
      int temp=x; x+=y; y=temp;
    }
    return x;
  }

  //fast mode
  static int fib(Worker *w, int n) {
    if(n<2) return n;
    FibFrame *frame = new FibFrame(n);
	assert(frame != NULL);
    frame->PC = LABEL_1;
    w->pushFrame(frame);

    // this thread will definitely execute fib(n-1), and
    // hence set the value in the frame.
    const int x = fib(w, n-1);

    // Now need to figure out who is doing fib(n-2).
    // If frame has been stolen, then this thread wont do fib(n-2).
    // it should just return, and subsequent work will be done
    // by others. 
    if(w->abortOnSteal(x)) {
      //      cerr<<w->index<<"::Aborting on steal"<<endl;
//      delete frame;
      return -1;
    }

    // Now we are back in the current frame, it has not been stolen. 
    // Execute the local code to the next spawn. 
    
    // Now at the next spawn, exactly as before, set up the 
    // continuation pointer. 
    frame->x = x;
    frame->PC = LABEL_2;

    const int y = fib(w, n-2);
    if(w->abortOnSteal(y)) {
      //      cerr<<w->index<<"::Aborting on steal"<<endl;
//       delete frame;
      return -1;
    }

    // Now there is nothing more to spawn -- so no need for the frame.
    // i.e. since the worker has made it so far, it is going to
    // complete 
    // execution of this procedure.
    
    // pop the task -- it is guaranteed to be garbage.
    w->popFrame();

//     delete frame;
    // the sync is a no-op.
    // return the computed value.
    int result = x+y;
    return result;
  }

  FibC(Frame *frame) : Closure(frame) {}

  /*The frame given to compute would have been copied to create a Closure. It will be deleted when the Closure is destroyed. */
  void compute(Worker *w, Frame *frame)  {
    // get the frame.
    // f must be a FibFrame.
    FibFrame * f = (FibFrame *) frame;
    //    cerr<<"FibC::compute. n="<<f->n<<"PC="<<f->PC<<endl;
    int x,y;
    const int n = f->n;
    switch (f->PC) {
    case ENTRY: 
      if (n < 2) {
	result = n;
	setupReturn(w);
	return;
      }
      f->PC=LABEL_1;
      x = fib(w, n-1);
      if(w->abortOnSteal(x)) {
	return;
      }
      f->x=x;

    case LABEL_1: 
      f->PC=LABEL_2;
      y=fib(w,n-2);
      if(w->abortOnSteal(y)) {
	return;
      }
      f->y=y;

    case LABEL_2: 
      f->PC=LABEL_3;
      if (sync(w)) {
	return;
      }
    case LABEL_3:
      result=f->x+f->y;
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
  int resultInt() { return result;}
  void setResultInt(int x) { result=x;}
};

/*----Some delayed definitions to pacify the C++ compiler---*/

void anon_Outlet1::run() {
  f->x = c->resultInt();
}

void anon_Outlet2::run() {
  f->y = c->resultInt();
}

Closure *FibFrame::makeClosure() {
	Closure *c = new FibC(this);
	assert(c != NULL);
	return c;
}


class anon_Job1 : public Job {
private:
  int result;
  const int n;
public:
  anon_Job1(Pool *g, int _n) : Job(g), n(_n) {}
  void setResultInt(int x) { result = x;}
  int resultInt() { return result;}
  int spawnTask(Worker *ws) { return FibC::fib(ws, n);}

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
  int points[] = { 1, 5, 10, 15, 20, 25, 30, 35};
    
  for (int i = 0; i < sizeof(points)/sizeof(int); i++) {
    int n = points[i];
    Job *job = new anon_Job1(g, n);
    assert(job != NULL);
      
//     long s = System.nanoTime();

    g->submit(job);
    int result = job->getInt();

    cout<<"Fib("<<points[i]<<")\t="<<result<<"\t"<<FibC::realfib(points[i])<<endl;
      
//     long t = System.nanoTime();
//     System.out.println(points[i] + " " + (t-s)/1000000 
// 		       + " " + result + " " + (result==realfib(n)?"ok" : "fail") );
  }

  g->shutdown();
  delete g;
}


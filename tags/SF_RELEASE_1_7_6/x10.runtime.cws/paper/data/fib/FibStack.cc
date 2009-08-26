/**
 * A C++ version of the recursive Fibonacci implemented in Java (in
 * x10 cws) with stack-allocated frames.
 * @author Sriram Krishnamoorthy
 */

#include <iostream>
#include <string>

#include "Closure.h"
#include "Cache.h"
#include "Frame.h"
#include "Worker.h"
#include "Job.h"
#include "Pool.h"
#include "Sys.h"
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

class anon_Outlet2 : public Outlet {
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
  Closure *ownerClosure;
  //bool valid;
public:
  volatile int PC;

  FibFrame(int _n, Closure* cl) : n(_n), x(-3), y(-4), ownerClosure(cl){ }
  virtual void setOutletOn(Closure *c) {
    assert(PC==LABEL_1 || PC==LABEL_2);
    Outlet *o;
    if(PC==LABEL_1) 
      o = new anon_Outlet1(this, c);
    else if(PC==LABEL_2)
      o = new anon_Outlet2(this, c);
    else
      assert(0);
    assert(o != NULL);
    c->setOutlet(o);
  }

  virtual Closure *makeClosure();
  virtual FibFrame *copy() {
    FibFrame *f = new FibFrame(*this);
    assert(f != NULL);
	//FibFrame f(*this);
    return f;
  }

private:
  FibFrame(const FibFrame& f) 
    : Frame(f), n(f.n), x(f.x), y(f.y), PC(f.PC), ownerClosure(f.ownerClosure) { }
};

class FibC : public virtual Closure {
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
  static int fib(Worker *w, int n, Closure *cl) {
    if(n<2) return n;
    //FibFrame *frame = new FibFrame(n, cl);
    FibFrame sFrame(n, cl);
    FibFrame *frame = &sFrame;
	assert(frame != NULL);
    frame->PC = LABEL_1;
    w->pushFrame(frame);

    // this thread will definitely execute fib(n-1), and
    // hence set the value in the frame.
    const int x = fib(w, n-1, cl);

    // Now need to figure out who is doing fib(n-2).
    // If frame has been stolen, then this thread wont do fib(n-2).
    // it should just return, and subsequent work will be done
    // by others. 
    if(w->abortOnSteal(x)) {
      //cerr<<w->index<<"::Aborting on steal. x="<<x<<endl;
      //delete frame;
      return -1;
    }

    if(w->cache->parentInterrupted()) {
      w->lock(w);
      FibC *ocl = dynamic_cast<FibC*>(frame->ownerClosure);
      if(ocl) 
	frame = dynamic_cast<FibFrame *>(ocl->frame);
      w->unlock();
      
      assert(frame != NULL);
    }

    // Now we are back in the current frame, it has not been stolen. 
    // Execute the local code to the next spawn. 
    
    // Now at the next spawn, exactly as before, set up the 
    // continuation pointer. 
    frame->x = x;
    frame->PC = LABEL_2;

    const int y = fib(w, n-2, cl);
    if(w->abortOnSteal(y)) {
      //cerr<<w->index<<"::Aborting on steal. x="<<x<<" y="<<y<<endl;
      //delete frame;
      return -1;
    }

    // Now there is nothing more to spawn -- so no need for the frame.
    // i.e. since the worker has made it so far, it is going to
    // complete 
    // execution of this procedure.

    //    assert(w->cache->currentFrame() == frame);
    
    // pop the task -- it is guaranteed to be garbage.
    w->popFrame();

    //    if(w->index==1)cerr<<w->index<<":: Deleting frame "<<frame<<endl;


    if(w->cache->interrupted()) {
      w->lock(w);
      //wait for promotion of child, before the stacked frame is deleted
      w->unlock();
    }

    // the sync is a no-op.
    // return the computed value.
    int result = x+y;
    return result;
  }

  FibC(FibFrame *frame) : Closure(frame) { 
    frame->ownerClosure = this; 
  }
  ~FibC() {    delete frame;   }


  /*The frame given to compute would have been copied to create a Closure. It will be deleted when the Closure is destroyed. */
  virtual void compute(Worker *w, Frame *frame)  {
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
      x = fib(w, n-1, this);
      if(w->abortOnSteal(x)) {
	return;
      }
      f->x=x;

    case LABEL_1: 
      f->PC=LABEL_2;
      y=fib(w,n-2, this);
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
      //cerr<<w->index<<"::n="<<n<<". x="<<f->x<<". y="<<f->y<<endl;
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
  virtual int resultInt() { return result;}
  virtual void setResultInt(int x) { result=x;}
};

/*----Some delayed definitions to pacify the C++ compiler---*/

void anon_Outlet1::run() {
  //assert(f->valid == true);
  f->x = c->resultInt();
}

void anon_Outlet2::run() {
  //assert(f->valid == true);
  f->y = c->resultInt();
}

Closure *FibFrame::makeClosure() {
  //assert(valid == true);
  	//valid = false;
	//cout << "S" << endl;
	FibC *c = new FibC(copy());
	assert(c != NULL);
	//cout << "E" << endl;
	ownerClosure = c;
	return c;
}


class anon_Job1 : public Job {
private:
  int result;
  const int n;
public:
  anon_Job1(Pool *g, int _n) : Job(g), n(_n) {}
  virtual void setResultInt(int x) { result = x;}
  virtual int resultInt() { return result;}
  virtual int spawnTask(Worker *ws) { return FibC::fib(ws, n, this);}

  void jobCompleted() { 
    //cerr<<"Job::result="<<result<<endl; 
    Job::jobCompleted(); }

  virtual ~anon_Job1() {}
protected:
  
};

int main(int argc, char *argv[]) {
  int result;

  if(argc < 4) {
    printf("Usage: %s <threads> <nReps> <input-number>\n", argv[0]);
    exit(0);
  }

  const int procs = atoi(argv[1]);
  const int nReps = atoi(argv[2]);
  const int ni = atoi(argv[3]);
  //cout<<"Number of procs=" << procs <<" nReps="<<nReps<<endl;
//   if (argc > 2) 
//     Worker::reporting = true;

//   int points[] = { 1, 5, 10, 15, 20, 25, 30, 35, 40};
    
//   for (int i = 0; i < sizeof(points)/sizeof(int); i++) {
  Pool *g = new Pool(procs);
  assert(g != NULL);

  long sc = 0, sa = 0;
  //for (int n = 30; n <= 45; n+=5) {
  	int n = ni;
	long long minT;
    for(int j=0; j<nReps; j++) {
      anon_Job1 job(g, n);
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

//     cout<<"Fib("<<points[i]<<")\t="<<result<<"\t"<<
//       FibC::realfib(points[i])<<"\t Time="<<(t-s)/1000000<<"ms"<<endl;
    /*cout<<"Fib("<<n<<")\t="<<result<<"\t"<<
      FibC::realfib(n)<<"\t Time="<<(t-s)/1000/nReps<<"us"<<endl;*/
    
    cout<<"nprocs="<<procs
	<<" FibStack("<<n<<")" << "\t" <<minT/1000<<" us" << "\t"
    	<< ((result == FibC::realfib(n)) ? "ok" : "fail" )
    	<< "\t" << " steals="<< sc
        << "\t" << "stealAttemps=" << sa
	<<endl;

//         sc=g->getStealCount();
//         sa=g->getStealAttempts();



      
//     long t = System.nanoTime();
//     System.out.println(points[i] + " " + (t-s)/1000000 
// 		       + " " + result + " " + (result==realfib(n)?"ok" : "fail") );
  //}
  g->shutdown();
  delete g;
  
#if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
  cerr<<"Frame. nCons = "<<Frame::nCons<<" nDestruct="<<Frame::nDestruct<<endl;
  cerr<<"Closure. nCons = "<<Closure::nCons<<" nDestruct="<<Closure::nDestruct<<endl;
  cerr<<"Outlet. nCons ="<<Outlet::nCons<<" nDestruct="<<Outlet::nDestruct<<endl;
#endif
}


  

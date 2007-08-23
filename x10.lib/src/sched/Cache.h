/*
============================================================================
 Name        : Cache.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Cache_h
#define x10lib_Cache_h
#include <limits.h>
#include <vector>

#include "Sys.h"
#include <assert.h>

using namespace std;

#define MEM_BAR  1

namespace x10lib_cws {

class Worker;
class Frame;
class Pool;
	
const int MAXIMUM_CAPACITY = 1 << 30;
const int INITIAL_CAPACITY = 1 << 13;
const int EXCEPTION_INFINITY = INT_MAX;

	
class Cache {	
 private:
  volatile int head, tail, exception; 
  void growAndPushFrame (Frame *);

 protected:
  /* 		unsigned int lastException;  */
  Worker *owner;
  //		vector<Frame *> *stack; /* using vector as resizing would be lot easier */
  Frame** stack; /*Trying a stack. vector was a bit slower*/
  int stack_size;

  void incrementExceptionPointer ();
  void decrementExceptionPointer ();
	
 public:
  Cache (Worker *w);
  ~Cache (); 
				
  Frame *&childFrame () ;
  Frame *topFrame () ;
  void setTopFrame(Frame *);
  bool headAheadOfTail () ;
  bool headGeqTail () ;
  bool exceptionOutstanding () ;
  void incHead ();
  int gethead () ;
  int gettail () ;
  int getexception() ;
  void resetExceptionPointer (Worker *w);
  void signalImmediateException ();
  bool empty () ;
  bool atTopOfStack () ;
  inline void pushFrame (Frame *x) {
    assert(x != NULL);
    assert(stack != NULL);

    if (tail < stack_size - 1) {
      stack[tail] = x;
      //MEM_BARRIER();
      WRITE_BARRIER();
      ++tail;
      return;
    }
    growAndPushFrame(x);
  }
  Frame *currentFrame()  ;
  void reset ();
  inline void popFrame () { 
    assert(tail >= 1);
    --tail;
    //READ_BARRIER();
    //MEM_BARRIER(); 
  }
  bool interrupted() volatile { 
    MEM_BARRIER(); // TODO SRIRAM -- You have added this.. I am removing it.. Please check
    return exception>=tail; 
  }
  bool parentInterrupted() volatile { return exception+1>=tail;}
  void pushIntUpdatingInPlace(Pool *pool, int tid, int x);

  inline bool dekker(Worker *thief) {
    assert(thief != owner);
    // if (exception != EXCEPTION_INFINITY)
    //{
    ++exception;
    //}
    MEM_BARRIER();
    if ((head + 1) >= tail) {
      //     if (exception != EXCEPTION_INFINITY)
      --exception;
      //MEM_BARRIER();
      return false;
    }
    // so there must be at least two elements in the framestack for a theft.
    /*if ( Worker.reporting) {
      System.out.println(thief + " has found victim " + owner);
      }*/
    //   cerr<<"Found victim. head="<<head<<" tail="<<tail<<endl;
    return true;
  }

  Frame *popAndReturnFrame(Worker *w);
	
};
}
#endif

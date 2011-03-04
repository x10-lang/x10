/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Cache.h,v 1.17 2007-12-26 07:57:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XWS_CACHE_H
#define __X10_XWS_CACHE_H


#include <x10/xws/Sys.h>
#include <x10/xassert.h>
#include <limits.h>
#include <vector>

using namespace std;

#define MEM_BAR  1

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib_xws {

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

} /* closing brace for namespace x10lib_xws */
#endif

#endif /* __X10_XWS_CACHE_H */

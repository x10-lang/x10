/*
============================================================================
 Name        : Cache.cpp
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/


#include "Cache.h"
#include "Worker.h"
#include "Pool.h"
#include "Sys.h"
#include "Frame.h"
#include <assert.h>

#include <cstdlib>
#include <iostream>

using namespace x10lib_cws;
using namespace std;

Cache::Cache(Worker *w) 
{
  assert(w != NULL); //always tied to a valid worker
 owner=w; 
 // stack.resize(INITIAL_CAPACITY);  // TODO verify
 stack = new vector<Frame *>(INITIAL_CAPACITY);
 assert(stack != NULL);
 head = tail = exception = 0;
}

Cache::~Cache() { delete stack;}

void Cache::pushFrame(Frame *x) {
  assert(x != NULL);
  assert(stack != NULL);

  if (tail < stack->size() - 1) {
    stack->at(tail) = x;
    MEM_BARRIER();
    ++tail;
    //WRITE_BARRIER();
    return;
  }
  growAndPushFrame(x);
}

void Cache::pushIntUpdatingInPlace(Pool *pool, int tid, int x) {

  if (/*stack != NULL &&*/ tail < stack->size() - 1) {
    if (stack->at(tail) != NULL) {
      (*stack)[tail]->setInt(x);
    } else {
      Worker *w = pool->workers[tid];
      Frame *f = w->fg->make();
      f->setInt(x);
      stack->at(tail) = f;
      
    }
    ++tail;
    WRITE_BARRIER();
    return;
  }
  Worker* w = pool->workers[tid];
  Frame *f = w->fg->make();
  f->setInt(x);
  growAndPushFrame(f);
}

/*
 * Handles resizing and reinitialization cases for pushFrame
 * @param x the task
 */
void Cache::growAndPushFrame(Frame *x) {
	int oldSize = 0;
    int newSize = 0;
    
    if (!stack->empty()) {
    	oldSize = stack->size();
        newSize = oldSize << 1;
    }
    
    if (newSize < INITIAL_CAPACITY)
        newSize = INITIAL_CAPACITY;
    if (newSize > MAXIMUM_CAPACITY) {
      assert(0);
      abort();
    }
    
    vector<Frame *> *newStack = new vector<Frame*>(newSize);
    assert(stack->size() == tail);
    for(int i=0; i<stack->size(); i++) {
      newStack->at(i) = stack->at(i);
    }


    //    owner->lock(owner);
    stack->resize(newSize);
    delete stack;
    stack = newStack;
    owner->unlock();

    stack->at(tail) = x;
    MEM_BARRIER();
    ++tail;
    //    MEM_BARRIER();
}

void Cache::resetExceptionPointer(Worker *w) {
  assert (w==owner);
  exception = head;
  //    MEM_BARRIER();
}
    
void Cache::incrementExceptionPointer() {
  //  MEM_BARRIER();
    if (exception != EXCEPTION_INFINITY) {
    	++exception;
    	MEM_BARRIER();
    }
    	
}
void Cache::decrementExceptionPointer() {
    if (exception != EXCEPTION_INFINITY) {
    	--exception;
	MEM_BARRIER();
    }
    	
}
void Cache::signalImmediateException() {
  assert(0);
    exception = EXCEPTION_INFINITY;
    MEM_BARRIER();   
}

bool Cache::atTopOfStack() {
    return head+1 == tail;
}
   
Frame *Cache::childFrame() {
  assert(stack->at(head+1) != NULL);
  return stack->at(head+1);
}
Frame *Cache::topFrame() {
    return stack->at(head);
}
Frame *Cache::currentFrame() {
    	return stack->at(tail-1);
}
void Cache::popFrame() {
  --tail;
  WRITE_BARRIER();
}

bool Cache::interrupted() {
  //  MEM_BARRIER();
  return exception >= tail;
}
/*
bool Cache::popCheck() {
	int t = tail;
	int e = exception;
// 	lastException = e;
	return e >= t;
}
*/

bool Cache::empty() {
	return head>=tail;
}
bool Cache::headAheadOfTail() {
	return head==tail+1;
}
bool Cache::headGeqTail() {
	return head >= tail;
}
void Cache::reset() {
  tail=0;
  head=0;
  exception=0;
  //  WRITE_BARRIER();
}
void Cache::incHead() {
	++head;
	//	MEM_BARRIER();
}
bool Cache::exceptionOutstanding() {
	return head <= exception;
}
int Cache::gethead() { return head;}
int Cache::gettail() { return tail;}
int Cache::getexception() { return exception;}

bool Cache::dekker(Worker *thief) {
  assert(thief != owner);
  // if (exception != EXCEPTION_INFINITY)
    {
    ++exception;
  }
  MEM_BARRIER();
  if ((head + 1) >= tail) {
//     if (exception != EXCEPTION_INFINITY)
      --exception;
    MEM_BARRIER();
    return false;
  }
  // so there must be at least two elements in the framestack for a theft.
  /*if ( Worker.reporting) {
    System.out.println(thief + " has found victim " + owner);
    }*/
//   cerr<<"Found victim. head="<<head<<" tail="<<tail<<endl;
  return true;
}


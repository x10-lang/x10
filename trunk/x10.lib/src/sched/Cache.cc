/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Cache.cc,v 1.15 2007-12-26 07:57:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/* Used by Worker and Closure to cache the frames for
 * the bottom most closure in a worker's ready deque.
 */

#include <x10/xws/Cache.h>
#include <x10/xws/Worker.h>
#include <x10/xws/Pool.h>
#include <x10/xws/Sys.h>
#include <x10/xws/Frame.h>
#include <x10/xassert.h>

#include <cstdlib>
#include <iostream>

using namespace x10lib_xws;
using namespace std;

Cache::Cache(Worker *w) 
{
  assert(w != NULL); //always tied to a valid worker
 owner=w; 
 // stack.resize(INITIAL_CAPACITY);  // TODO verify
 // stack = new vector<Frame *>(INITIAL_CAPACITY);
 stack = new Frame*[INITIAL_CAPACITY];
 assert(stack != NULL);
 stack_size = INITIAL_CAPACITY;
 head = tail = exception = 0;
}

Cache::~Cache() { delete stack;}

/*
void Cache::pushFrame(Frame *x) {
  assert(x != NULL);
  assert(stack != NULL);

//   if (tail < stack->size() - 1) {
  if (tail < stack_size - 1) {
//     stack->at(tail) = x;
    stack[tail] = x;
    MEM_BARRIER();
    ++tail;
    //WRITE_BARRIER();
    return;
  }
  growAndPushFrame(x);
}
*/

void Cache::pushIntUpdatingInPlace(Pool *pool, int tid, int x) {
  assert(stack != NULL);

//   if (tail < stack->size() - 1) {
//     if (stack->at(tail) != NULL) {
//       (*stack)[tail]->setInt(x);
//     } else {
  if (tail < stack_size - 1) {
    if (stack[tail] != NULL) {
      stack[tail]->setInt(x);
    } else {
      Worker *w = pool->workers[tid];
      Frame *f = w->fg->make();
      f->setInt(x);
//       stack->at(tail) = f;
      stack[tail] = f;     
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
  
//   if (!stack->empty()) {
//     oldSize = stack->size();
  if (stack_size>0) {
    oldSize = stack_size;
    newSize = oldSize << 1;
  }
  
  if (newSize < INITIAL_CAPACITY)
    newSize = INITIAL_CAPACITY;
  if (newSize > MAXIMUM_CAPACITY) {
      assert(0);
      abort();
  }
  
//   vector<Frame *> *newStack = new vector<Frame*>(newSize);
  Frame** newStack = new Frame*[newSize];
//   assert(stack->size() == tail);
  assert(stack_size == tail);
//   for(int i=0; i<stack->size(); i++) {
//     newStack->at(i) = stack->at(i);
//   }
  for(int i=0; i<stack_size; i++) {
    newStack[i] = stack[i];
  }
  
  
  Frame **tmp=stack;
  owner->lock(owner);
  //stack->resize(newSize);
  stack = newStack;
  owner->unlock();
  delete tmp;  

//   stack->at(tail) = x;
  stack[tail] = x;
  MEM_BARRIER();
  ++tail;
  //MEM_BARRIER();
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
	//MEM_BARRIER();
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
   
Frame *&Cache::childFrame() {
//   assert(stack->at(head+1) != NULL);
//   return stack->at(head+1);
  assert(stack[head+1] != NULL);
  return stack[head+1];
}
Frame *Cache::topFrame() {
//     return stack->at(head);
    return stack[head];
}
void Cache::setTopFrame(Frame *frame) {
    stack[head] = frame;
}

Frame *Cache::currentFrame() {
//     	return stack->at(tail-1);
    	return stack[tail-1];
}
// void Cache::popFrame() {
//   --tail;
//   WRITE_BARRIER();
// }

// bool Cache::interrupted() {
//   MEM_BARRIER();
//   return exception >= tail;
// }
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
  MEM_BARRIER();
  head=0;
  exception=0;
  //  WRITE_BARRIER();
}
void Cache::incHead() {
	++head;
	//MEM_BARRIER();
}
bool Cache::exceptionOutstanding() {
	return head <= exception;
}
int Cache::gethead() { return head;}
int Cache::gettail() { return tail;}
int Cache::getexception() { return exception;}

/**
 * A fast way of determining whether the worker has been interrupted.
 * @param w
 * @return
 */
Frame *Cache::popAndReturnFrame(Worker *w) {
  assert(w==owner);
  Frame *rval;

  if (head >= tail) {
    rval = NULL;
  } else {
    tail--;
    if (interrupted()) { // there has been a theft -- rare case.
      w->lock(w);
      // need to lock to ensure that we get the right value for head.
      // have to set exception so that the interrupt is acknowledged.
      exception=head;
      w->unlock();
    } 
    
    Frame *f = stack[tail];
    stack[tail]=NULL;
    rval = f;
  }
  assert (head >=0);
  assert (tail >= 0);
  return rval;
}




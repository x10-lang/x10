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

#include <iostream>

using namespace x10lib_cws;
using namespace std;

Cache::Cache(Worker *w) 
{
	assert(w != NULL); //always tied to a valid worker
 owner=w; 
 stack.resize(INITIAL_CAPACITY);  // TODO verify
 head = tail = exception = 0;
}
Cache::~Cache() {stack.clear();}

void Cache::pushFrame(Frame *x) {
    	assert(x != NULL);

	// 	if(owner->index==0)cerr<<owner->index<<"::pushFrame entered.  H="<<head<<"T="<<tail<<" E="<<exception<<endl;
    	
    	if (/*stack != NULL &&*/ tail < stack.size() - 1) {
	  stack[tail] = x;
//     		stack.push_back(x);
    		++tail;
    		WRITE_BARRIER();
    		return;
    	}
    	growAndPushFrame(x);
}

void Cache::pushIntUpdatingInPlace(Pool *pool, int tid, int x) {

    	if (/*stack != NULL &&*/ tail < stack.size() - 1) {
    		if (stack[tail] != NULL) {
    			stack[tail]->setInt(x);
    		} else {
    			Worker *w = pool->workers[tid];
    			Frame *f = w->fg->make();
    			f->setInt(x);
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
    
    if (!stack.empty()) {
    	oldSize = stack.size();
        newSize = oldSize << 1;
    }
    
    if (newSize < INITIAL_CAPACITY)
        newSize = INITIAL_CAPACITY;
    if (newSize > MAXIMUM_CAPACITY) {
      assert(0);
      abort();
    }
    
    owner->lock(owner);
    stack.resize(newSize);
    owner->unlock();

//     stack.push_back(x);
    stack[tail] = x;
    ++tail;
    MEM_BARRIER();
}

void Cache::resetExceptionPointer(Worker *w) {
		assert (w==owner);
    exception = head;
}
    
void Cache::incrementExceptionPointer() {
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
    exception = EXCEPTION_INFINITY;
    MEM_BARRIER();   
}

bool Cache::atTopOfStack() const {
    return head+1 == tail;
}
   
Frame *Cache::childFrame() const {
//   cerr<<owner->index<<"::Cache::childFrame. H="<<head<<" T="<<tail<<" E="<<exception<<endl;
  assert(stack.at(head+1) != NULL);
    return stack.at(head+1);
}
Frame *Cache::topFrame() const {
    return stack.at(head);
}
Frame *Cache::currentFrame() const {
    	return stack.at(tail-1);
}
void Cache::popFrame() {
  //  if(owner->index==0)cerr<<owner->index<<"::popFrame entered.  H="<<head<<"T="<<tail<<" E="<<exception<<endl;
  --tail;
  WRITE_BARRIER();
}

bool Cache::interrupted() const {
  MEM_BARRIER();
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

bool Cache::empty() const {
	return head>=tail;
}
bool Cache::headAheadOfTail() const {
	return head==tail+1;
}
bool Cache::headGeqTail() const {
	return head >= tail;
}
void Cache::reset() {
  tail=0;
  head=0;
  exception=0;
  WRITE_BARRIER();
}
void Cache::incHead() {
	++head;
	WRITE_BARRIER();
}
bool Cache::exceptionOutstanding() const {
	return head <= exception;
}
int Cache::gethead() const { return head;}
int Cache::gettail() const { return tail;}
int Cache::getexception() const { return exception;}

bool Cache::dekker(Worker *thief) {
  assert(thief != owner);
  if (exception != EXCEPTION_INFINITY) {
    ++exception;
  }
  MEM_BARRIER();
  if ((head + 1) >= tail) {
    if (exception != EXCEPTION_INFINITY)
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


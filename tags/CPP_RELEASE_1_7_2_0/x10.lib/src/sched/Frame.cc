/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Frame.cc,v 1.14 2007-12-26 07:57:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/xws/Frame.h>
#include <x10/xws/Closure.h>
#include <x10/xws/Sys.h>
#include <x10/xws/Worker.h>
#include <x10/xws/Cache.h>
#include <x10/xassert.h>

using namespace std;
using namespace x10lib_xws;

#if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
volatile int Frame::nCons=0;
volatile int Frame::nDestruct=0;
#endif


		/*
		 * Returns a closure that can contain a pointer to this frame.
		 * e.g. a FibFrame will return a FibClosure. The executeInlet
		 * method of this closure must know where in the frame to 
		 * place its result.
		 * @return -- a closure for executing the code in this method
		 * instance from the position specified by this frame.
		 * Should be overridden by subclasses.
		 */
Closure *Frame::makeClosure() { assert(0); abort(); return NULL; }

/**
 * Set the Outlet object on c so that it can supply
 * its result into the right field of the given frame.
 * Should be overridden by subclasses.
 * @param c -- The closure whose outlet must be set.
 */
void Frame::setOutletOn(Closure *c) { assert(0); abort(); }

	/**
	 * To be implemented in subclasses to support in place update of frames
	 * on the frame stack.
	 * @param x -- the new value for the distinguished slot in the frame.
	 */
void Frame::setInt(int x) { assert(0); abort();}

/*Frame *Frame::copy() { 
	Frame * f = new Frame(*this); 
	assert(f !=  NULL);
	return f;	
	}*/


void Frame::compute(Worker *w) {
  /*if called, it should be implemented by sub-class*/
  assert(0);
  abort();
}

Executable *Frame::execute(Worker *w) {
  Cache *c = w->cache;
  c->pushFrame(this);
  c->resetExceptionPointer(w);
  compute(w);
  w->catchAllException();
  delete this;
  return NULL;
}


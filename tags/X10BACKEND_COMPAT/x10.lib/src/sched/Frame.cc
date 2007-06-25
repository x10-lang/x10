/*
============================================================================
 Name        : Frame.cpp
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/

#include "Frame.h"
#include "Closure.h"
#include <assert.h>

using namespace std;
using namespace x10lib_cws;


Frame::Frame() {}
Frame::Frame(const Frame &f) {}
Frame::~Frame() {}

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



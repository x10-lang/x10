/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/** Thread Runnbale Interface **/

#ifndef __XRX_RUNNABLE_H
#define __XRX_RUNNABLE_H

namespace xrx_runtime {

class Runnable {
public:
	// constructor
	//Runnable() {};

	// destructor
	//~Runnable() {};

	// any Runnable subclass should implement this
	virtual void run() = 0;
};

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_RUNNABLE_H */
// vim:tabstop=4:shiftwidth=4:expandtab

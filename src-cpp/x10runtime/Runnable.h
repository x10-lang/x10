/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** Thread Runnbale interface. **/

#ifndef __XRX_KERNEL_RUNNABLE_H
#define __XRX_KERNEL_RUNNABLE_H

namespace xrx_kernel {

class Runnable {
public:

	// no constructors & destructor
	// Runnable(void);
	// ~Runnable(void);

	// any subclass should implement this
	virtual void run(void);

};

} /* closing brace for namespace xrx_kernel */

#endif /* __XRX_KERNEL_RUNNABLE_H */

/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** InterruptedException that may be thrown by Thread.sleep(). **/

#ifndef __XRX_KERNEL_INTERRUPTED_EXCEPTION_H
#define __XRX_KERNEL_INTERRUPTED_EXCEPTION_H
#include <exception>

namespace xrx_kernel {

class InterruptedException : public exception {
	// to do
};

} /* closing brace for namespace xrx_kernel */

#endif /* __XRX_KERNEL_INTERRUPTED_EXCEPTION_H */

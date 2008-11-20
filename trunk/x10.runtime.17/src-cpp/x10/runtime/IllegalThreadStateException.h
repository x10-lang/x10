/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/**
 * IllegalThreadStateException that may be thrown by Thread.start()
 * when the calling thread is already started.
 */

#ifndef __XRX_ILLEGAL_THREAD_STATE_EXCEPTION_H
#define __XRX_ILLEGAL_THREAD_STATE_EXCEPTION_H

#include <Exception.h>

namespace xrx_runtime {

class IllegalThreadStateException : public Exception {
	// to do
};

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_ILLEGAL_THREAD_STATE_EXCEPTION_H */
// vim:tabstop=4:shiftwidth=4:expandtab

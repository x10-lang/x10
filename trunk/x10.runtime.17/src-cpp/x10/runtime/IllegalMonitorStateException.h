/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/**
 * IllegalMonitorStateException that may be thrown by
 * Lock.unlock().
 */

#ifndef __XRX_ILLEGAL_MONITOR_STATE_EXCEPTION_H
#define __XRX_ILLEGAL_MONITOR_STATE_EXCEPTION_H

#include <Exception.h>

namespace xrx_runtime {

class IllegalMonitorStateException : public Exception {
	// to do
};

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_ILLEGAL_MONITOR_STATE_EXCEPTION_H */

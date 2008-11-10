/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/** Xrx Library Startup Interface **/

#ifndef __XRX_XRX_H
#define __XRX_XRX_H

#include <pthread.h>
#include <sys/pthdebug.h>
#include <Types.h>

namespace xrx_runtime {

class Xrx {
public:
	// constructor
	Xrx();

	// destructor
	~Xrx();

protected:
	// unique session handle
	static pthdb_session_t __xrx_session;

	// session validity flag
	static boolean __xrx_session_valid;

	// one time initialization variable
	static boolean __xrx_already_inited;

private:
	/**
	 * these are callback functions necessary to start a
	 * pthread debug session
	 */

	// read data from a process address space
	static int read_data(pthdb_user_t user, void *buf,
					pthdb_addr_t addr, size_t len);

	// memory allocation function
	static int alloc(pthdb_user_t user, size_t len, void **bufp);

	// memory reallocation function
	static int realloc(pthdb_user_t user, void *buf,
					size_t len, void **bufp);

	// memory deallocation function
	static int dealloc(pthdb_user_t user, void *buf);

	// unique user handle
	static pthdb_user_t __xrx_user;

	// user callback functions
	static pthdb_callbacks_t __xrx_callbacks;

	// execution mode
	static pthdb_exec_mode_t __xrx_mode;

	// session flags
	static unsigned long long __xrx_flags;
};

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_XRX_H */

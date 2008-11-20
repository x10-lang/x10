/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/** Xrx Library Startup Implementation **/

#include <Xrx.h>
#include <Debug.h>
#include <iostream>
#include <cstdlib>
#include <cstring>

namespace xrx_runtime {

// initialize static data members
boolean Xrx::__xrx_already_inited = false;
// somehow we need to generate this uniquely
pthdb_user_t Xrx::__xrx_user = 0x1001;
pthdb_callbacks_t Xrx::__xrx_callbacks = {
	/* symbol_addrs */ NULL,
	/* read_data */ Xrx::read_data,
	/* write_data */ NULL,
	/* read_regs */ NULL,
	/* write_regs */ NULL,
	/* alloc */ Xrx::alloc,
	/* realloc */ Xrx::realloc,
	/* dealloc */ Xrx::dealloc,
	/* print */ NULL
};
// need to set this from environment
pthdb_exec_mode_t Xrx::__xrx_mode = PEM_64BIT;
unsigned long long Xrx::__xrx_flags =
		PTHDB_FLAG_SUSPEND|PTHDB_FLAG_REGS;
boolean Xrx::__xrx_session_valid = false;
pthdb_session_t Xrx::__xrx_session = NULL;

// constructor
Xrx::Xrx()
{
	__xrxDPrStart();
	// initialization must be done only once per process
	if (!Xrx::__xrx_already_inited) {
		int rc;
		rc = pthdb_session_init(Xrx::__xrx_user, Xrx::__xrx_mode,
						Xrx::__xrx_flags,
						&Xrx::__xrx_callbacks, &Xrx::__xrx_session);
		if (rc != PTHDB_SUCCESS) {
			// ??temporary hack??
			std::cerr << "Pthread debug library initialization failed!!"
						<< std::endl;
			Xrx::__xrx_session_valid = false;
		} else {
			//std::cerr << "Pthread debug library initialization succeeded!!"
			//			<< std::endl;
			Xrx::__xrx_session_valid = true;
			// update internal information
			(void)pthdb_session_update(Xrx::__xrx_session);
		}
		Xrx::__xrx_already_inited = true;
	}
	__xrxDPrEnd();
}

// destructor
Xrx::~Xrx()
{
	__xrxDPrStart();
	// notify the library that session is over
	if (Xrx::__xrx_already_inited && Xrx::__xrx_session_valid) {
		(void)pthdb_session_destroy(Xrx::__xrx_session);
	}
	__xrxDPrEnd();
}

// read data from a process address space
int
Xrx::read_data(pthdb_user_t user, void *buf,
					pthdb_addr_t addr, size_t len)
{
	if (std::memcpy(buf, (const void *)addr, len) == NULL) {
		return 1;
	}
	return 0;
}

// memory allocation function
int
Xrx::alloc(pthdb_user_t user, size_t len, void **bufp)
{
	if ((*bufp = std::malloc(len)) == NULL) {
		return 1;
	}
	return 0;
}

// memory reallocation function
int
Xrx::realloc(pthdb_user_t user, void *buf,
					size_t len, void **bufp)
{
	if ((*bufp = std::realloc(buf, len)) == NULL) {
		return 1;
	}
	return 0;
}

// memory deallocation function
int
Xrx::dealloc(pthdb_user_t user, void *buf)
{
	std::free(buf);
	return 0;
}

} /* closing brace for namespace xrx_runtime */
// vim:tabstop=4:shiftwidth=4:expandtab

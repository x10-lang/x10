/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/** Wrappers to maintain certain type equalities. **/

#ifndef __XRX_TYPES_H
#define __XRX_TYPES_H

#include <pthread.h>
#include <string>

namespace xrx_runtime {

	typedef bool boolean;

	// until we implement full-fledged primitive classes
	typedef std::string String;
	typedef int Int;
	typedef long Long;

	// execution thread condition & associated lock pair
	typedef struct {
		pthread_cond_t cond;
		pthread_mutex_t mutex;
	} cond_mutex_t;

	// thread permit type
	typedef struct {
		pthread_cond_t cond;
		pthread_mutex_t mutex;
		boolean permit;
	} permit_t;

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_TYPES_H */

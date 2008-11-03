/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** Wrappers to maintain certain type equalities. **/

#ifndef __XRX_KERNEL_TYPES_H
#define __XRX_KERNEL_TYPES_H

#include <pthread.h>
#include <iostream>
using namespace std;

namespace xrx_kernel {
	typedef bool boolean;

	// until we have full-fledged primitive classes
	typedef long Long;
	typedef int Int;
	typedef string String;
} /* closing brace for namespace xrx_kernel */

#endif /* __XRX_KERNEL_TYPES_H */

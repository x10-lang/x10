/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: broadcast.h,v 1.4 2008-01-06 03:28:51 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_BROADCAST_H
#define __X10_BROADCAST_H


#include <x10/types.h>
#include <x10/err.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* Brodcast args from root to other places. */
	x10_err_t Broadcast(void *buffer, size_t nbytes);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Broadcast args from root to other places. */
x10_err_t x10_broadcast(void *buffer, size_t nbytes);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_BROADCAST_H */

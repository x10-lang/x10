/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: utility.h,v 1.1 2007-08-02 11:22:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Set of utilities to export some globals. **/

#ifndef __X10_UTILITY_H
#define __X10_UTILITY_H

#include <x10/types.h>
#include <lapi.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* Retrieve number of places. */
	int numPlaces(void);

	/* Retrieve the current place id. */
	x10_place_t here(void);

	/* Retrieve the current communication (LAPI) handle.
	 * This will go away once AM functionality is available
	 * in X10Lib.
	 */
	lapi_handle_t GetHandle(void);

} /* closing brace for namespace x10lib */
#endif


/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Retrieve number of places. */
int x10_num_places(void);

/* Retrieve the current place id. */
x10_place_t x10_here(void);

/* Retrieve the current communication (LAPI) handle. */
lapi_handle_t x10_get_handle(void);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_ORDER_H */

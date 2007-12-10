/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: broadcast.cc,v 1.4 2007-12-10 12:12:04 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for broadcasting of args. **/

#include <x10/broadcast.h>
#include <x10/xmacros.h>
#include <lapi.h>
#include "__x10lib.h__"

namespace x10lib {

/* Broadcast args from root to other places. */
x10_err_t Broadcast(void *buffer, size_t nbytes)
{
	lapi_long_t remoteAddresses[__x10_num_places];

	LRC(LAPI_Address_init64(__x10_hndl, (lapi_long_t)buffer,
			remoteAddresses));

	if (__x10_my_place == 0) {
		for (x10_place_t p = 0; p < __x10_num_places; p++) {
			if (p == __x10_my_place) continue;
			LAPI_Put(__x10_hndl, p, nbytes,
				(void *)remoteAddresses[p], buffer,
				NULL, NULL, NULL);
		}
	}

	// TODO: replace this with a counter
	LAPI_Gfence(__x10_hndl);
	return X10_OK;
}

} /* closing brace for namepsace x10lib */

/* Broadcast args from root to other places. */
extern "C"
x10_err_t x10_broadcast(void *buffer, size_t nbytes)
{
	return x10lib::Broadcast(buffer, nbytes);
}

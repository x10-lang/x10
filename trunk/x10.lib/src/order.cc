/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: order.cc,v 1.2 2007-12-10 12:12:05 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Message ordering interface. **/

#include <x10/order.h>
#include <lapi.h>
#include "__x10lib.h__"

namespace x10lib {

/* Local data synchronization. */
void SyncLocal(void)
{
	if (__x10_inited)
		(void)LAPI_Fence(__x10_hndl);
}

/* Collective data synchronization. */
void SyncGlobal(void)
{
	if (__x10_inited)
		(void)LAPI_Gfence(__x10_hndl);
}

} /* closing brace for namespace x10lib */


/* Local data synchronization. */
extern "C"
void x10_sync_local(void)
{
	return x10lib::SyncLocal();
}

/* Collective data synchronization. */
extern "C"
void x10_sync_global(void)
{
	return x10lib::SyncGlobal();
}

/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: order.cc,v 1.3 2008-01-06 03:28:51 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Message ordering interface. **/

#include <lapi.h>

#include <x10/order.h>
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

/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: order.cc,v 1.1 2007-06-25 16:07:36 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Message ordering interface. **/

#include <x10/order.h>
#include <lapi.h>

namespace x10lib {

/* Local data synchronization. */
void SyncLocal(void)
{
	extern lapi_handle_t __x10_hndl;
	extern int __x10_inited;

	if (__x10_inited)
		(void)LAPI_Fence(__x10_hndl);
}

/* Collective data synchronization. */
void SyncGlobal(void)
{
	extern lapi_handle_t __x10_hndl;
	extern int __x10_inited;

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

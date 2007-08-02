/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: order.h,v 1.1 2007-08-02 11:22:43 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Message ordering interface. **/

#ifndef __X10_ORDER_H
#define __X10_ORDER_H

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* Local data synchronization. */
	void SyncLocal(void);

	/* Collective data synchronization. */
	void SyncGlobal(void);

} /* closing brace for namespace x10lib */
#endif


/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Local data synchronization. */
void x10_sync_local(void);

/* Collective data synchronization. */
void x10_sync_global(void);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_ORDER_H */

/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: am.h,v 1.1 2007-12-08 10:20:26 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Active Messaging Interface. **/

#ifndef __X10_AM_H
#define __X10_AM_H

/* X10Lib Internal Handler Index Table */
enum {
	ASYNC_SPAWN_HANDLER = 1, /* 1 - General Async Spawn */
	ASYNC_SPAWN_HANDLER_AGG, /* 2 - Async with Aggregation */
	EXCEPTION_HEADER_HANDLER, /* 3 - Finish Logic Exceptions */
	CONTINUE_HEADER_HANDLER, /* 4 - Finish Logic Continuations */
	NUM_CHILD_HEADER_HANDLER, /* 5 - Finish Logic Process 0 Children */

	/* Array Operation Handlers */
	ARRAY_COPY_HANDLER, /* 6 */
	ARRAY_CONSTRUCTION_HANDLER, /* 7 */
	ARRAY_DELETION_HANDLER, /* 8 */
	ARRAY_ELEMENT_UPDATE_HANDLER, /* 9 */
	ASYNC_ARRAY_COPY_HANDLER, /* 10 */
	ASYNC_GEN_ARRAY_COPY_HANDLER, /* 11 */

	/* ... */

	X10_INVALID_HANDLER /* ??? */
	/*** Do not add any thing below ***/
};

#endif /* __X10_AM_H */

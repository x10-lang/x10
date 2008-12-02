/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: am.h,v 1.3 2008-01-21 11:31:15 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Active Messaging Interface. **/

#ifndef __X10_AM_H
#define __X10_AM_H

/* Max number of aggregate handlers */
#define X10_MAX_AGG_HANDLERS 2

/* X10Lib Internal Handler Index Table */
enum {
	ASYNC_SPAWN_HANDLER = 1, /* 1 - General Async Spawn */
	ASYNC_SPAWN_HANDLER_AGG, /* 2 - Async with Aggregation */
	ASYNC_SPAWN_HANDLER_AGG2, /* 3 - Async with Aggregation (Special case) */
	EXCEPTION_HEADER_HANDLER, /* 4 - Finish Logic Exceptions */
	CONTINUE_HEADER_HANDLER, /*  5 - Finish Logic Continuations */
	NUM_CHILD_HEADER_HANDLER, /* 6 - Finish Logic Process 0 Children */

	/* Array Operation Handlers */
	ARRAY_COPY_HANDLER, /* 7 */
	ARRAY_CONSTRUCTION_HANDLER, /* 8 */
	ARRAY_DELETION_HANDLER, /* 9 */
	ARRAY_ELEMENT_UPDATE_HANDLER, /* 10 */
	ASYNC_ARRAY_COPY_HANDLER, /* 11 */
	ASYNC_GEN_ARRAY_COPY_HANDLER, /* 12 */

	/* ... */

	X10_INVALID_HANDLER /* ??? */
	/*** Do not add any thing below ***/
};

#endif /* __X10_AM_H */

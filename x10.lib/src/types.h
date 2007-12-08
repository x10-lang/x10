/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: types.h,v 1.21 2007-12-08 09:49:46 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Primitive Types. **/

#ifndef __X10_TYPES_H__
#define __X10_TYPES_H__

#include <sys/types.h>

/* X10Lang Types
 * Previously part of x10lang.h
 */
typedef bool x10_boolean;
typedef int8_t x10_byte;
typedef uint16_t x10_char;
typedef int16_t x10_short;
typedef int32_t x10_int;
typedef int64_t x10_long;
typedef float x10_float;
typedef double x10_double;

/* X10Lang Array Index */
#ifdef USE_LONG_ARRAYS
typedef x10_long x10_index_t;
#else
typedef x10_int x10_index_t;
#endif

/* Async Handler Types */
typedef long x10_async_arg_t;
typedef int x10_async_handler_t;

/* X10 Place */
typedef int x10_place_t;


/* Completion Handler */
typedef void (x10_compl_hndlr_t) (void *uinfo);

/* Send Completion Handler */
/* Return Info Struct */
typedef struct {
	unsigned int src;
	unsigned int reason;
} x10_sh_info_t;

typedef void (x10_scompl_hndlr_t) (void *cparam, x10_sh_info_t *xinfo);

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

#endif /* __X10_TYPES_H */

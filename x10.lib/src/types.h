/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: types.h,v 1.20 2007-12-07 15:54:19 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Primitive Types. **/

#ifndef __X10_TYPES_H__
#define __X10_TYPES_H__

#include <sys/types.h>

/* x10lang types
 * previously used to be part of x10lang.h
 */
typedef bool x10_boolean;
typedef int8_t x10_byte;
typedef uint16_t x10_char;
typedef int16_t x10_short;
typedef int32_t x10_int;
typedef int64_t x10_long;
typedef float x10_float;
typedef double x10_double;

/* array index type */
#ifdef USE_LONG_ARRAYS
typedef x10_long x10_index_t;
#else
typedef x10_int x10_index_t;
#endif

/* async handler */
typedef long x10_async_arg_t;
typedef int x10_async_handler_t;
typedef int x10_place_t;


/* completion handler */
typedef void (x10_compl_hndlr_t) (void *uinfo);

/* send completion handler */
/* data structure for return info */
typedef struct {
	unsigned int src;
	unsigned int reason;
} x10_sh_info_t;

typedef void (x10_scompl_hndlr_t) (void *cparam, x10_sh_info_t *xinfo);

enum internal_handlers
  {
    ASYNC_SPAWN_HANDLER = 1,
    ASYNC_SPAWN_HANDLER_AGG,
    ASYNC_SPAWN_HANDLER_AGG_HYPER,
    ASYNC_SPAWN_HANDLER_AGG_RA,
    
    EXCEPTION_HEADER_HANDLER,
    CONTINUE_HEADER_HANDLER,
    NUM_CHILD_HEADER_HANDLER,
    
    ARRAY_COPY_HANDLER,
    ARRAY_CONSTRUCTION_HANDLER,
    ARRAY_DELETION_HANDLER,
    ARRAY_ELEMENT_UPDATE_HANDLER,
    ASYNC_ARRAY_COPY_HANDLER,
    ASYNC_GEN_ARRAY_COPY_HANDLER,
    
    __x10_num_handlers 
    
  };


#endif /* __X10_TYPES_H */

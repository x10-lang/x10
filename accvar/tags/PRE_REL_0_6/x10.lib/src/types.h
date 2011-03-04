/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: types.h,v 1.18 2007-11-12 07:56:58 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Primitive Types. **/

#ifndef __X10_TYPES_H__
#define __X10_TYPES_H__

#include <sys/types.h>

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
    ARRAY_ELEMENT_UPDATE_HANDLER,
    ASYNC_ARRAY_COPY_HANDLER,
    ASYNC_GEN_ARRAY_COPY_HANDLER,
    
    __x10_num_handlers 
    
  };


#endif /* __X10_TYPES_H */

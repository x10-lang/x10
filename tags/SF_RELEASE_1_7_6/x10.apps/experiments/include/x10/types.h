/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: types.h,v 1.1 2007-08-02 11:22:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Primitive Types. **/

#ifndef __X10_TYPES_H
#define __X10_TYPES_H

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

#endif /* __X10_TYPES_H */

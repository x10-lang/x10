/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: xmacros.h,v 1.6 2007-06-26 16:05:57 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XMACROS_H
#define __X10_XMACROS_H


/* macro for executing the specified LAPI statement and 
 * returning any error associated with it
 */
#define LRC(statement) \
do { \
	int rc = statement; \
	if (rc != LAPI_SUCCESS) { \
		__x10_errno = rc; \
		return X10_ERR_LAPI; \
	} \
} while (0)

#endif /* __X10_XMACROS_H */

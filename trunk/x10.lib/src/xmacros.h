/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: xmacros.h,v 1.11 2007-10-25 11:04:30 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XMACROS_H
#define __X10_XMACROS_H

#include <iostream>

/* macro for executing the specified LAPI statement and 
 * returning any error associated with it
 */
#define LRC(statement) \
do { \
	int rc = statement; \
	if (rc != LAPI_SUCCESS) { \
		__x10_errno = rc; \
		return X10_ERR_COM; \
	} \
} while (0)

/* debugging */

/* Originally developed by Christoph von Praun */

#ifdef X10_DLEVEL
#if (X10_DLEVEL >= 0)
#define X10_DEBUG(L,X)    { if (L <= X10_DLEVEL) { std::cout << "DEBG[" << L << ", " << __func__ << ", " << __x10_my_place << "]  " << X << std::endl << std::flush; } }
#else
#define X10_DEBUG(L,X)    { ; }
#endif /* X10_DLEVEL */
#else
#define X10_DEBUG(L,X) ;
#endif /* X10_DLEVEL */

/* deprecation */
#ifdef X10_REPORT_DEPRECATION
#define X10_DEPRECATED(X)    { std::cout << "DEPRECATED: " << X << std::endl << std::flush; }
#else
#define X10_DEPRECATED(X)    { ; }
#endif /* X10_REPORT_DEPRECATION */

#endif /* __X10_XMACROS_H */

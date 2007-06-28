/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: xmacros.h,v 1.8 2007-06-28 16:42:58 ganeshvb Exp $
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
		return X10_ERR_COM; \
	} \
} while (0)

namespace Tracing{
  static int dlevel_ = X10_DLEVEL;
}; 

/* debugging */
#if (X10_DLEVEL >= 0)
#define X10_DEBUG(L,X)    { if (L <= Tracing::dlevel_) { std::cout << "DEBG[" << L << ", " << here() << "]  " << X << std::endl << std::flush; } }
#else
#define X10_DEBUG(L,X)    { ; }
#endif /* X10_DLEVEL */

/* deprecation */
#ifdef X10_REPORT_DEPRECATION
#define X10_DEPRECATED(X)    { std::cout << "DEPRECATED: " << X << std::endl << std::flush; }
#else
#define X10_DEPRECATED(X)    { ; }
#endif /* X10_REPORT_DEPRECATION */

#endif /* __X10_XMACROS_H */

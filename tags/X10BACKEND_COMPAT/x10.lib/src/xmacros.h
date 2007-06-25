/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: xmacros.h,v 1.3 2007-06-25 12:06:29 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/* $Id: xmacros.h,v 1.3 2007-06-25 12:06:29 ganeshvb Exp $ */

#ifndef __X10__MACROS__H__
#define __X10__MACROS__H__

#ifdef DEBUG
#define LRC(statement) \
do { \
        int rc = statement; \
        if (rc != LAPI_SUCCESS) { \
                return X10_ERR_LAPI; \
        } \
} while (0)
#else 
#define LRC(statement) \
statement;
#endif

namespace Tracing{
static int dlevel_ = X10_DLEVEL;
static int wlevel_ = X10_WLEVEL;

static void dlevel (int l) 
{
  dlevel_ = l;
}

static void wlevel (int l) 
{
  wlevel_ = l;
}
}; 

/* debugging */
#if (X10_DLEVEL >= 0)
#define X10_DEBUG(L,X)    { if (L <= Tracing::dlevel_) { std::cout << "DEBG[" << L << ", " << here() << "]  " << X << std::endl << std::flush; } }
#else
#define X10_DEBUG(L,X)    { ; }
#endif /* X10_DLEVEL */


/* warnings */
#if (X10_WLEVEL >= 0)
#define X10_WARN(L,X)    { if (L <=  Tracing::wlevel_) { std::cout << "WARN[" << L << ", " << here() << "]  " << X << std::endl << std::flush; } }
#else
#define X10_WARN(L,X)    { ; }
#endif /* X10_WLEVEL */


/* deprecation */
#ifdef X10_REPORT_DEPRECATION
#define X10_DEPRECATED(X)    { std::cout << "DEPRECATED: " << X << std::endl << std::flush; }
#else
#define X10_DEPRECATED(X)    { ; }
#endif /* X10_REPORT_DEPRECATION */

#endif /*__X10__MACROS__H*/

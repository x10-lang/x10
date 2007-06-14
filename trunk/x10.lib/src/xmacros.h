/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: xmacros.h,v 1.2 2007-06-14 13:59:53 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/* $Id: xmacros.h,v 1.2 2007-06-14 13:59:53 ganeshvb Exp $ */

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

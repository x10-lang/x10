/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: xmacros.h,v 1.1 2007-06-07 14:14:39 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/* $Id: xmacros.h,v 1.1 2007-06-07 14:14:39 ganeshvb Exp $ */

#define LRC(statement) \
do { \
        int rc = statement; \
        if (rc != LAPI_SUCCESS) { \
                return X10_ERR_LAPI; \
        } \
} while (0)

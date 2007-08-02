/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: env.h,v 1.1 2007-08-02 11:22:42 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/* Interface for interacting with the X10Lib's runtime behavior */

#ifndef __X10_ENV_H
#define __X10_ENV_H

#include <x10/err.h>

typedef enum {
	PLACE_ID = 0, /* Query the place id */
	NUM_PLACES, /* Query the number of places */
	POLL_SET, /* Query and set the polling mode */
	NODE_NAME, /* Query the node name */
	NODE_ADDR, /* Query the node address */
	NODE_ID, /* Query the node id */
} x10_query_t;

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* Query X10Lib's runtime information */
	x10_err_t Getenv(x10_query_t query, int *ret_val);

	/* Set X10Lib's runtime variables */
	x10_err_t Setenv(x10_query_t query, int set_val);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Query X10Lib's runtime information */
x10_err_t x10_getenv(x10_query_t query, int *ret_val);

/* Set X10Lib's runtime variables */
x10_err_t x10_setenv(x10_query_t query, int set_val);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_ENV_H */

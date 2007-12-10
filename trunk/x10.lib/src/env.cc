/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: env.cc,v 1.2 2007-12-10 12:12:04 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/* Implementation file for X10Lib's runtime interaction */

#include <x10/env.h>
#include <x10/err.h>
#include <x10/xmacros.h>
#include <unistd.h>
#include <sys/param.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <lapi.h>
#include <cstring>
#include "__x10lib.h__"

namespace x10lib {

/* Query X10Lib's runtime information */
x10_err_t Getenv(x10_query_t query, int *ret_val)
{
	if (!__x10_inited)
		return X10_ERR_INIT;

	if (ret_val == NULL)
		return X10_ERR_RET_PTR_NULL;

	switch (query) {
		case PLACE_ID:
			(void)LAPI_Qenv(__x10_hndl, TASK_ID, ret_val);
			break;
		case NUM_PLACES:
			(void)LAPI_Qenv(__x10_hndl, NUM_TASKS, ret_val);
			break;
		case POLL_SET:
			{
				int mode;

				(void)LAPI_Qenv(__x10_hndl, INTERRUPT_SET, &mode);
				*ret_val = (mode == 0) ? 1 : 0;
			}
			break;
		case NODE_NAME:
			{
				int rc;

				rc = gethostname((char *)ret_val, MAXHOSTNAMELEN);
				if (rc < 0) {
					return X10_ERR_RET_VAL;
				}
			}
			break;
		case NODE_ADDR:
			{
				struct in_addr in;

				in.s_addr = gethostid();
				if (strcpy((char *)ret_val, inet_ntoa(in)) == NULL)
					return X10_ERR_RET_VAL;
			}
			break;
		case NODE_ID:
			*ret_val = gethostid();
			break;
		default:
			return X10_ERR_QUERY_TYPE;
			break;
	}
	return X10_OK;
}

/* Set X10Lib's runtime variables */
x10_err_t Setenv(x10_query_t query, int set_val)
{
	if (!__x10_inited)
		return X10_ERR_INIT;

	switch (query) {
		case POLL_SET:
			if (set_val < 0)
				return X10_ERR_SET_VAL;
			(void)LAPI_Senv(__x10_hndl, INTERRUPT_SET,
					(set_val == 0) ? 1 : 0);
			break;
		default:
			return X10_ERR_QUERY_TYPE;
	}
	return X10_OK;
}

} /* closing brace for namespace x10lib */

/* Query X10Lib's runtime information */
extern "C"
x10_err_t x10_getenv(x10_query_t query, int *ret_val)
{
	return x10lib::Getenv(query, ret_val);
}

/* Set X10Lib's runtime variables */
extern "C"
x10_err_t x10_setenv(x10_query_t query, int set_val)
{
	return x10lib::Setenv(query, set_val);
}

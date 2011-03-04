/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: err.cc,v 1.2 2007-06-27 17:23:57 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/err.h>
#include <cstring>
#include <lapi.h>

const char *__x10_err_msg[] = {
	/* X10_OK */
	"The X10 call completed successfully.",
	/* X10_ERR_INIT */
	"The X10Lib is not initalized.",
	/* X10_ERR_RET_PTR_NULL */
	"The specified return value pointer is NULL.",
	/* X10_ERR_RET_VAL */
	"The specified return value pointer is not valid.",
	/* X10_ERR_QUERY_TYPE */
	"The specified query type is invalid.",
	/* X10_ERR_SET_VAL */
	"The specified value is not with in valid range.",
	/* X10_ERR_PLACE */
	"The specified place is outside the range.",
	/* X10_ERR_REM_ADDR_NULL */
	"The specified remote address is NULL.",
	/* X10_ERR_LOC_ADDR_NULL */
	"The specified local address is NULL.",
	/* X10_ERR_DATA_LEN */
	"The specified length is outside the range.",
	/* X10_ERR_PARAM_INVALID */
	"The specified parameter is not valid.",
	/* X10_ERR_ADDR_HNDL_RANGE */
	"The specified address handle is outside the range.",
	/* X10_ERR_HNDLR_NULL */
	"The specified handler is NULL.",
	/* X10_ERR_CODE_UNKNOWN */
	"The specified error code is outside the range.",
};

/* global var for holding the error code for the operation
 * that just failed
 */
int __x10_errno;

namespace x10lib {

/* Retrieve the message associated with the specified error code */
x10_err_t ErrMsg(x10_err_t err_code, char *buf, int len)
{
	int nbytes;

	if (buf == NULL || len <= 0)
		return X10_ERR_RET_PTR_NULL;

	if (err_code < 0 || err_code >= X10_ERR_CODE_MAX)
		return X10_ERR_CODE_UNKNOWN;

	if (err_code == X10_ERR_COM)
		(void)LAPI_Msg_string(__x10_errno, buf);

	nbytes = (len < X10_MAX_ERR_STRING) ? (len - 1) :
				(X10_MAX_ERR_STRING - 1);

	(void)strncpy(buf, __x10_err_msg[err_code], nbytes);
	buf[nbytes] = '\0';

	return X10_OK;
}

} /* closing brace for namespace x10lib */


/* Retrieve the message associated with the specified error code */
extern "C"
x10_err_t x10_err_msg(x10_err_t err_code, char *buf, int len)
{
	return x10lib::ErrMsg(err_code, buf, len);
}

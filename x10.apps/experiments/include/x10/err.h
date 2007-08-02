/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: err.h,v 1.1 2007-08-02 11:22:42 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ERR_H
#define __X10_ERR_H

#define X10_MAX_ERR_STRING 256

typedef enum {
	X10_OK = 0,
	X10_ERR_INIT, /* X10Lib not initialized */
	X10_ERR_RET_PTR_NULL, /* Return value ptr is NULL */
	X10_ERR_RET_VAL, /* Return value ptr is not valid */
	X10_ERR_QUERY_TYPE, /* Not a vaild query */
	X10_ERR_SET_VAL, /* Not within valid range */
	X10_ERR_PLACE, /* Not a valid place */
	X10_ERR_REM_ADDR_NULL, /* Remote address is NULL */
	X10_ERR_LOC_ADDR_NULL, /* Local address is NULL */
	X10_ERR_DATA_LEN, /* Data length exceeds the limit */
	X10_ERR_PARAM_INVALID, /* Parameter is invalid */
	X10_ERR_ADDR_HNDL_RANGE, /* Address handle is out of range */
	X10_ERR_HNDLR_NULL, /* Handler is NULL */
	X10_ERR_COM, /* Underlying messaging operation failed */
	X10_ERR_CODE_UNKNOWN, /* Out of range error code */
	X10_ERR_CODE_MAX /* Maximum error code */
} x10_err_t;

/* x10 error number for the operation that just failed */
extern int __x10_errno;

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
	
	/* Retrieve the message associated with the specified error code */
	x10_err_t ErrMsg(x10_err_t err_code, char *buf, int len);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Retrieve the message associated with the specified error code */
x10_err_t x10_err_msg(x10_err_t err_code, char *buf, int len);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif


#endif /* __X10_ERR_H */

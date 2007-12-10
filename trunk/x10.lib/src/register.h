/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: register.h,v 1.3 2007-12-10 10:36:40 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Registration interface for handlers. **/

#ifndef __X10_REGISTER_H
#define __X10_REGISTER_H

#include <x10/err.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
	
	/* Register the address of a function handler. */
	int RegHandlerAddr(void *addr);

	/* Get the function address of a specified handler. */
	x10_err_t GetHandlerAddr(void **addr, int ah);

	/* Create a remote address table. */
	x10_err_t AddressInit (void *addr, void **table);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Register the address of a function handler. */
int x10_reg_handler_addr(void *addr);

/* Get the function address of a specified handler. */
x10_err_t x10_get_handler_addr(void **addr, int ah);

/* Create a remote address table. */
x10_err_t x10_address_init(void *addr, void **table);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_REGISTER_H */

/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: register.cc,v 1.2 2007-10-19 16:04:29 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Registration interface. **/

#include <x10/register.h>
#include <lapi.h>

namespace x10lib {
	
/* Register the address of a function handler. */
int RegHandlerAddr(void *addr)
{
	extern lapi_handle_t __x10_hndl;
	extern int __x10_addr_hndl;
	extern int __x10_addrtbl_sz;
	extern int __x10_inited;

	if (!__x10_inited)
		return -1;

	if ((__x10_addr_hndl + 1) >= __x10_addrtbl_sz)
		return -1;
	__x10_addr_hndl += 1;
	(void)LAPI_Addr_set(__x10_hndl, addr, __x10_addr_hndl);
	return __x10_addr_hndl;
}

/* Get the function address of a specified handler. */
x10_err_t GetHandlerAddr(void **addr, int ah)
{
	extern lapi_handle_t __x10_hndl;
	extern int __x10_addr_hndl;
	extern int __x10_inited;

	if (!__x10_inited)
		return X10_ERR_INIT;

	if (ah < 0 || ah > __x10_addr_hndl)
		return X10_ERR_ADDR_HNDL_RANGE;

	if (addr == NULL)
		return X10_ERR_RET_PTR_NULL;

	(void)LAPI_Addr_get(__x10_hndl, addr, ah);
	return X10_OK;
}
	

/* get the remote address in to a local table */
x10_err_t AddressInit (void* addr, void** table)
{
  extern lapi_handle_t __x10_hndl;
  (void) LAPI_Address_init (__x10_hndl, addr, table);
  return X10_OK;
}

} /* closing brace for namespace x10lib */

/* Register the address of a function handler. */
extern "C"
int x10_reg_handler_addr(void *addr)
{
	return x10lib::RegHandlerAddr(addr);
}

/* Get the function address of a specified handler. */
extern "C"
x10_err_t x10_get_handler_addr(void **addr, int ah)
{
	return x10lib::GetHandlerAddr(addr, ah);
}

/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: register.cc,v 1.4 2007-12-10 12:12:05 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Registration interface. **/

#include <x10/register.h>
#include <lapi.h>
#include "__x10lib.h__"

namespace x10lib {
	
/* Register the address of a function handler. */
int RegHandlerAddr(void *addr)
{
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
	if (!__x10_inited)
		return X10_ERR_INIT;

	if (ah < 0 || ah > __x10_addr_hndl)
		return X10_ERR_ADDR_HNDL_RANGE;

	if (addr == NULL)
		return X10_ERR_RET_PTR_NULL;

	(void)LAPI_Addr_get(__x10_hndl, addr, ah);
	return X10_OK;
}
	

/* Collects remote address into a local table. */
x10_err_t AddressInit (void *addr, void **table)
{
  (void)LAPI_Address_init(__x10_hndl, addr, table);
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

/* Collect remote addresses into a local table. */
extern "C"
x10_err_t x10_address_init(void *addr, void **table)
{
	return x10lib::AddressInit(addr, table);
}

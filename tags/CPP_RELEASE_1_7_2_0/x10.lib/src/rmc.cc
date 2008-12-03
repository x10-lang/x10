/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: rmc.cc,v 1.5 2007-12-10 12:12:05 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Remote memory copy interface. **/
#include <x10/rmc.h>
#include <x10/xmacros.h>
#include <x10/lock.h>
#include <x10/x10lib.h>
#include <lapi.h>
#include "__x10lib.h__"

namespace x10lib {

/* internal routine to actually perform the get operation */
static
x10_err_t __get(x10_gas_ref_t src, void *dest, int n,
				x10_rmc_type_t type, x10_switch_t sw)
{
	int val, orig_val, cur_val;
	int place;
	void *addr;

	if (!__x10_inited)
		return X10_ERR_INIT;

	place = GAS2Place(src);
	/* sanity check; is place valid? */
	if (place < 0 || place > (__x10_num_places - 1))
		return X10_ERR_PLACE;

	addr = GAS2Addr(src);
	/* sanity check; is addr NULL? */
	if (addr == NULL)
		return X10_ERR_REM_ADDR_NULL;

	/* sanity check; is dest NULL? */
	if (dest == NULL)
		return X10_ERR_LOC_ADDR_NULL;

	/* sanity check; is n within range? */
	if (n < 0 || n > X10_MAX_MSG_SIZE)
		return X10_ERR_DATA_LEN;

	switch (type) {
		case RMC_NON_BLOCK:
			/* non-blocking operation */
			lapi_cntr_t *lcp;

			/* sanity check; is switch non-null? */
			if (sw == NULL)
				return X10_ERR_PARAM_INVALID;
			
			/* retrieve and init the provided handle */
			lcp = (lapi_cntr_t*) sw->get_handle();
			sw->decrement();

			LRC(LAPI_Get(__x10_hndl, place, n, addr, dest, NULL, lcp));
			break;
		case RMC_BLOCK:
			/* blocking operation */
			/* get the current value of the global wait counter;
		 	 * wait on it to ensure that data transfer is complete;
		 	 * and restore it to its original value.
		 	 */
			Lock();
			(void)LAPI_Getcntr(__x10_hndl, &__x10_wait_cntr, &orig_val);
			val = orig_val + 1;
			LRC(LAPI_Get(__x10_hndl, place, n, addr, dest, NULL, &__x10_wait_cntr));
			(void)LAPI_Waitcntr(__x10_hndl, &__x10_wait_cntr, val, &cur_val); 
			(void)LAPI_Setcntr(__x10_hndl, &__x10_wait_cntr, orig_val);
			Unlock();
			break;
		default:
			/* we shouldn't be here */
			break;
	}
	return X10_OK;
}

/* Fetch n contiguous bytes from src addr on a remote node
 * to the dest addr on the local node.
 * [Blocking Operation]
 */
x10_err_t Get(x10_gas_ref_t src, void *dest, int n)
{
	return __get(src, dest, n, RMC_BLOCK, 0);
}

/* Non-blocking remote get operation for contiguous bytes. */
x10_err_t NbGet(x10_gas_ref_t src, void *dest, int n,
				x10_switch_t sw)
{
	return __get(src, dest, n, RMC_NON_BLOCK, sw);
}

/* internal operation to actually perform put operation */
x10_err_t __put(void *src, x10_gas_ref_t dest, int n,
				x10_rmc_type_t type, x10_switch_t sw)
{
	int val, orig_val;
	int place;
	void *addr;

	if (!__x10_inited)
		return X10_ERR_INIT;

	place = GAS2Place(dest);
	/* sanity check; is place valid? */
	if (place < 0 || place > (__x10_num_places - 1))
		return X10_ERR_PLACE;

	addr = GAS2Addr(dest);
	/* sanity check; is addr NULL? */
	if (addr == NULL)
		return X10_ERR_REM_ADDR_NULL;

	/* sanity check; is src NULL? */
	if (src == NULL)
		return X10_ERR_LOC_ADDR_NULL;

	/* sanity check; is n within range? */
	if (n < 0 || n > X10_MAX_MSG_SIZE)
		return X10_ERR_DATA_LEN;

	switch (type) {
		case RMC_NON_BLOCK:
			/* non-blocking operation */
			lapi_cntr_t *lcp;
	
			/* sanity check; is switch non-null? */
			if (sw == NULL)
				return X10_ERR_PARAM_INVALID;
				
			/* retrieve and initialize the provided handle */
			lcp = (lapi_cntr_t*) sw->get_handle();
			sw->decrement();

			LRC(LAPI_Put(__x10_hndl, place, n, addr, src, NULL, NULL, lcp));
			break;
		case RMC_BLOCK:
			/* blocking operation */
			Lock();
			/* get the current value of the global wait counter;
		 	 * wait on it to ensure that data transfer is complete;
		 	 * and restore it to its original value.
		 	 */
			(void)LAPI_Getcntr(__x10_hndl, &__x10_wait_cntr, &orig_val);
			val = orig_val + 1;
			LRC(LAPI_Put(__x10_hndl, place, n, addr, src, NULL, NULL,
						&__x10_wait_cntr));
			(void)LAPI_Waitcntr(__x10_hndl, &__x10_wait_cntr, val, NULL); 
			(void)LAPI_Setcntr(__x10_hndl, &__x10_wait_cntr, orig_val);
			Unlock();
			break;
		default:
			/* we shouldn't be here */
			break;
	}
	return X10_OK;
}
	
/* Write n contiguous bytes from src addr on the local node
 * to the dest addr on a remote node.
 * [Blocking Operation]
 */
x10_err_t Put(void *src, x10_gas_ref_t dest, int n)
{
	return __put(src, dest, n, RMC_BLOCK, 0);
}

/* Non-blocking remote put operation for contiguous bytes. */
x10_err_t NbPut(void *src, x10_gas_ref_t dest, int n,
				x10_switch_t sw)
{
	return __put(src, dest, n, RMC_NON_BLOCK, sw);
}

/* Equivalents of Waitcntr and the LAPI_Put 
 * This is for use by reduction operations.
 * This needs to be merged with exisiting functionaliy.
 */

x10_err_t LAPIStylePut(int destPlace, size_t size,
		void* dest, void* src,
		void* target_cntr, void* origin_cntr,
		void* compl_cntr)
{
	LRC(LAPI_Put(__x10_hndl, destPlace, size, dest, src,
			(lapi_cntr_t *)target_cntr,
			(lapi_cntr_t *)origin_cntr,
			(lapi_cntr_t *)compl_cntr));
	return X10_OK;
}

x10_err_t LAPIStyleWaitcntr (void* cntr, int value, int* ret)
{
	LRC(LAPI_Waitcntr(__x10_hndl, (lapi_cntr_t *)cntr, value, ret));
	return X10_OK;
}

x10_err_t LAPIStyleSetcntr (void* cntr, int value)
{
	LRC(LAPI_Setcntr(__x10_hndl, (lapi_cntr_t *)cntr, 0));
	return X10_OK;
}

/* Remote get operation(s) for int/long/double/float. */

template <class Type>
Type __GetXXX(x10_gas_ref_t src, Type not_used)
{
	static Type val;
	x10_err_t rc;

	rc = Get(src, (void *)&val, sizeof(val));
	if (rc != X10_OK)
		return ((Type)-1);
	return val;
}

// int
int GetInt(x10_gas_ref_t src)
{
	return __GetXXX<int>(src, (int)0);
}

// long
long GetLong(x10_gas_ref_t src)
{
	return __GetXXX<long>(src, (long)0);
}

// double
double GetDouble(x10_gas_ref_t src)
{
	return __GetXXX<double>(src, (double)0);
}

// float
float GetFloat(x10_gas_ref_t src)
{
	return __GetXXX<float>(src, (float)0);
}

/* Remote put operation(s) for int/long/double/float.
 * [Blocking Operation(s)]
 */
template <class Type>
x10_err_t __PutXXX(Type val, x10_gas_ref_t dest)
{
	static Type lval = val;

	return Put((void *)&lval, dest, sizeof(lval));
}

// int
x10_err_t PutInt(int val, x10_gas_ref_t dest)
{
	return __PutXXX<int>(val, dest);
}

// long
x10_err_t PutLong(long val, x10_gas_ref_t dest)
{
	return __PutXXX<long>(val, dest);
}

// double
x10_err_t PutDouble(double val, x10_gas_ref_t dest)
{
	return __PutXXX<double>(val, dest);
}

// float
x10_err_t PutFloat(float val, x10_gas_ref_t dest)
{
	return __PutXXX<float>(val, dest);
}

/* Non-blocking remote put operation(s) for int/long/double/float. */

template <class Type>
x10_err_t __NbPutXXX(Type val, x10_gas_ref_t dest, x10_switch_t sw)
{
	static Type lval = val;

	return NbPut((void *)&lval, dest, sizeof(lval), sw);
}

// int
x10_err_t NbPutInt(int val, x10_gas_ref_t dest, x10_switch_t sw)
{
	return __NbPutXXX<int>(val, dest, sw);
}

// long
x10_err_t NbPutLong(long val, x10_gas_ref_t dest,
				x10_switch_t sw)
{
	return __NbPutXXX<long>(val, dest, sw);
}

// double
x10_err_t NbPutDouble(double val, x10_gas_ref_t dest,
				x10_switch_t sw)
{
	return __NbPutXXX<double>(val, dest, sw);
}

// float
x10_err_t NbPutFloat(float val, x10_gas_ref_t dest,
				x10_switch_t sw)
{
	return __NbPutXXX<float>(val, dest, sw);
}

} /* closing brace for namespace x10lib */

/* Fetch n contiguous bytes from src addr on a remote node
 * to the dest addr on the local node.
 */
extern "C"
x10_err_t x10_get(x10_gas_ref_t src, void *dest, int n)
{
	return x10lib::Get(src, dest, n);
}

/* Write n contiguous bytes from src addr on the local node
 * to the dest addr on a remote node.
 */
extern "C"
x10_err_t x10_put(void *src, x10_gas_ref_t dest, int n)
{
	return x10lib::Put(src, dest, n);
}

/* Non-blocking remote get operation for contiguous bytes. */
extern "C"
x10_err_t x10_get_nb(x10_gas_ref_t src, void *dest, int n,
					x10_switch_t sw)
{
	return x10lib::NbGet(src, dest, n, sw);
}

/* Non-blocking remote put operation for contiguous bytes. */
extern "C"
x10_err_t x10_put_nb(void *src, x10_gas_ref_t dest, int n,
					x10_switch_t sw)
{
	return x10lib::NbPut(src, dest, n, sw);
}

/* Remote get operation(s) for int/long/double/float. */

// int
extern "C"
int x10_get_int(x10_gas_ref_t src)
{
	return x10lib::GetInt(src);
}

// long
extern "C"
long x10_get_long(x10_gas_ref_t src)
{
	return x10lib::GetLong(src);
}

// double
extern "C"
double x10_get_double(x10_gas_ref_t src)
{
	return x10lib::GetDouble(src);
}

// float
extern "C"
float x10_get_float(x10_gas_ref_t src)
{
	return x10lib::GetFloat(src);
}

/* Remote put operation(s) for int/long/double/float.
 * [Blocking Operation(s)]
 */

// int
extern "C"
x10_err_t x10_put_int(int val, x10_gas_ref_t dest)
{
	return x10lib::PutInt(val, dest);
}

// long
extern "C"
x10_err_t x10_put_long(long val, x10_gas_ref_t dest)
{
	return x10lib::PutLong(val, dest);
}

// double
extern "C"
x10_err_t x10_put_double(double val, x10_gas_ref_t dest)
{
	return x10lib::PutDouble(val, dest);
}

// float
x10_err_t x10_put_float(float val, x10_gas_ref_t dest)
{
	return x10lib::PutFloat(val, dest);
}

/* Non-blocking remote put operation(s) for int/long/double/float. */

// int
x10_err_t x10_put_int_nb(int val, x10_gas_ref_t dest, x10_switch_t sw)
{
	return x10lib::NbPutInt(val, dest, sw);
}

// long
x10_err_t x10_put_long_nb(long val, x10_gas_ref_t dest,
				x10_switch_t sw)
{
	return x10lib::NbPutLong(val, dest, sw);
}

// double
x10_err_t x10_put_double_nb(double val, x10_gas_ref_t dest,
				x10_switch_t sw)
{
	return x10lib::NbPutDouble(val, dest, sw);
}

// float
x10_err_t x10_put_float_nb(float val, x10_gas_ref_t dest,
				x10_switch_t sw)
{
	return x10lib::NbPutFloat(val, dest, sw);
}

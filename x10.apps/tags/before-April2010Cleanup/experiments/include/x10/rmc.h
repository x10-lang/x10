/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: rmc.h,v 1.1 2007-08-02 11:22:44 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Remote memory copy interface. **/

#ifndef __X10_RMC_H
#define __X10_RMC_H

#include <x10/types.h>
#include <x10/gas.h>
#include <x10/switch.h>
#include <x10/err.h>

/* rmc operational types */
typedef enum {
	RMC_BLOCK,
	RMC_NON_BLOCK
} x10_rmc_type_t;

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* Fetch n contiguous bytes from src addr on a remote node
	 * to the dest addr on the local node.
	 *  - Should be implemented by an RDMA wherever possible.
	 *  - Should progress independently of calls made to X10Lib
	 *    by threads in the source process.
	 *  - On return from this call, the bytes will be available
	 *    in dest.
	 */
	x10_err_t Get(x10_gas_ref_t src, void *dest, int n);

	/* Write n contiguous bytes from src addr on the local node
	 * to the dest addr on a remote node.
	 *  - Should be implemented by an RDMA wherever possible.
	 *  - Should progress independently of calls made to X10Lib
	 *    by threads in the dest process.
	 *  - On return from this call, the bytes will be available
	 *    to be read from the remote node in dest.
	 */
	x10_err_t Put(void *src, x10_gas_ref_t dest, int n);

	/** Note:
	 ** X10Lib doesn't support "implicit" non-blocking calls.
	 ** The programmer should simply create a "default" switch
	 ** and use that explicitly.
	 **/

	/* Non-blocking remote get operation for contiguous bytes.
	 *  - Should be implemented by an RDMA wherever possible.
	 *  - Should progress independently of calls made to X10Lib
	 *    by threads in the source process.
	 *  - The bytes will be available only after the subsequent
	 *    Wait call successfully returns.
	 */
	x10_err_t NbGet(x10_gas_ref_t src, void *dest, int n,
					x10_switch_t sw);

	/* Non-blocking remote put operation for contiguous bytes.
	 *  - Should be implemented by an RDMA wherever possible.
	 *  - Should progress independently of calls made to X10Lib
	 *    by threads in the dest process.
	 *  - The bytes will be available only after the subsequent
	 *    Wait call successfully returns.
	 */
	x10_err_t NbPut(void *src, x10_gas_ref_t dest, int n,
					x10_switch_t sw);

	/* Remote get operation(s) for int/long/double/float.
	 *  - Should be implemented by an RDMA whenver possible.
	 *  - Should progress independently of calls made to X10Lib
	 *    by threads in the source process.
	 */
	int GetInt(x10_gas_ref_t src);
	long GetLong(x10_gas_ref_t src);
	double GetDouble(x10_gas_ref_t src);
	float GetFloat(x10_gas_ref_t src);

	/* Remote put operation(s) for int/long/double/float.
	 * [Blocking Operation(s)]
	 */
	x10_err_t PutInt(int val, x10_gas_ref_t dest);
	x10_err_t PutLong(long val, x10_gas_ref_t dest);
	x10_err_t PutDouble(double val, x10_gas_ref_t dest);
	x10_err_t PutFloat(float val, x10_gas_ref_t dest);

	/* Non-blocking remote put operation(s) for int/long/double/float.
	 *  - Should be implemented by an RDMA whenver possible.
	 *  - Should progress independently of calls made to X10Lib
	 *    by threads in the dest process.
	 */
	x10_err_t NbPutInt(int val, x10_gas_ref_t dest, x10_switch_t sw);
	x10_err_t NbPutLong(long val, x10_gas_ref_t dest, x10_switch_t sw);
	x10_err_t NbPutDouble(double val, x10_gas_ref_t dest,
						x10_switch_t sw);
	x10_err_t NbPutFloat(float val, x10_gas_ref_t dest,
						x10_switch_t sw);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Fetch n contiguous bytes from src addr on a remote node
 * to the dest addr on the local node.
 */
x10_err_t x10_get(x10_gas_ref_t src, void *dest, int n);

/* Write n contiguous bytes from src addr on the local node
 * to the dest addr on a remote node.
 */
x10_err_t x10_put(void *src, x10_gas_ref_t dest, int n);

/* Non-blocking remote get operation for contiguous bytes. */
x10_err_t x10_get_nb(x10_gas_ref_t src, void *dest, int n,
					x10_switch_t sw);

/* Non-blocking remote put operation for contiguous bytes. */
x10_err_t x10_put_nb(void *src, x10_gas_ref_t dest, int n,
					x10_switch_t sw);

/* Remote get operation(s) for int/long/double/float. */
int x10_get_int(x10_gas_ref_t src);
long x10_get_long(x10_gas_ref_t src);
double x10_get_double(x10_gas_ref_t src);
float x10_get_float(x10_gas_ref_t src);

/* Remote put operation(s) for int/long/double/float.
 * [Blocking Operation(s)]
 */
x10_err_t x10_put_int(int val, x10_gas_ref_t dest);
x10_err_t x10_put_long(long val, x10_gas_ref_t dest);
x10_err_t x10_put_double(double val, x10_gas_ref_t dest);
x10_err_t x10_put_float(float val, x10_gas_ref_t dest);

/* Non-blocking remote put operation(s) for int/long/double/float. */
x10_err_t x10_put_int_nb(int val, x10_gas_ref_t dest, x10_switch_t sw);
x10_err_t x10_put_long_nb(long val, x10_gas_ref_t dest,
					x10_switch_t sw);
x10_err_t x10_put_double_nb(double val, x10_gas_ref_t dest,
					x10_switch_t sw);
x10_err_t x10_put_float_nb(float val, x10_gas_ref_t dest,
					x10_switch_t sw);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_RMC_H */

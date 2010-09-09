/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: x10lib.h,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Public Interface for Compilers & Application Programmers. **/

#ifndef __X10_X10LIB_H
#define __X10_X10LIB_H

#include <x10/types.h>
#include <x10/err.h>
/*
#include <x10/clock.h>
#include <x10/switch.h>
#include <x10/gas.h>
#include <x10/activity.h>
*/

namespace x10lib {
	/* Initialization */
	int Init(x10_async_handler_t *handlers, int n);
	
	/* Termination */
	int Finalize();
	
	/* Remote get operation(s) for contiguous bytes */
	
	/* Fetch n contiguous bytes from src address on a remote node to the dest address
	 * on the local node.
	 *   - Should be implemented by an RDMA wherever possible.
	 *   - Should progress independently of calls made to x10lib by threads in the source process.
	 *   - On return from this call, the bytes are available in dest. 
	 */
	int Get(x10_gas_ref_t src, void *dest, int n);
	
	/* Non-blocking remote get operation for contiguous bytes.
	 *   - Should be implemented by an RDMA wherever possible.
	 *   - Should progress independently of calls made to x10lib by threads in the source process.
	 *   - The bytes will be available only after a Wait call successfully returns. 
	 */
	int NbGet(x10_gas_ref_t src, void *dest, int n, x10_switch_t hndl);
	
	/* Remote get operation(s) for int/long/double/float.
	 * Fetch and return the number of bytes indicated by the type from address src on node.
	 *   - Should be implemented by an RDMA whenever possible.
	 *   - Should progress independently of calls made to x10lib by threads in the source process.
	 */
	int GetValueInt(x10_gas_ref_t src);
	long GetValueLong(x10_gas_ref_t src);
	double GetValueDouble(x10_gas_ref_t src);
	float GetValueFloat(x10_gas_ref_t src);
	
	/* Strided get operations */
	int GetS(x10_gas_ref_t src, int src_stride[], void *dest, int dest_stride[],
				int count[], int levels);
				
	int NbGetS(x10_gas_ref_t src, int src_stride[], void *dest, int dest_stride[],
				int count[], int levels, x10_switch_t hndl);
				
	/* Generalized I/O vector get operations */
	int GetV(x10_giov_ref_t *dsrc, int len);
	
	int NbGetV(x10_giov_ref_t *dsrc, int len, x10_switch_t hndl);

	/* Remote Put operation(s) for contiguous bytes */
	
	/* Write n contiguous bytes from src address on the local node to the dest address
	 * on a remote node.
	 *  - Should be implemented by an RDMA wherever possible.
	 *  - Should progress independently of calls made to x10lib by threads in the dest process.
	 *  - On return from this call, the bytes are available to be read from the remote
	 *    node in dest.
	 */
	int Put(void *src, x10_gas_ref_t dest, int n);
	
	/* Non-blocking remote put operation for contiguous bytes.
	 *  - Should be implemented by an RDMA wherever possible.
	 *  - Should progress independently of calls made to x10lib by threads in the dest process.
	 *  - The bytes will be available only after a Wait call successfully returns.
	 * Note: x10lib doesn't support "implicit" non-blocking calls.  The programmer
	 *       should simply create a "default" switch and use that explicitly.
	 */
	int NbPut(void *src, x10_gas_ref_t dest, int n, x10_switch_t hndl);
	
	/* Remote put operation(s) for int/long/double/float.
	 * Places the given value at the specified dest node address.
	 *  - Should be implemented by an RDMA wherever possible.
	 *  - Should progress independently of calls made to x10lib by threads in the dest process.
	 */
	/*
	 * template <typename T>
	 * int PutXXX(T val, x10_gas_ref_t dest);
	 */
	int PutInt(int val, x10_gas_ref_t dest);
	int PutLong(long val, x10_gas_ref_t dest);
	int PutDouble(double val, x10_gas_ref_t dest);
	int PutFloat(float val, x10_gas_ref_t dest);
	
	/* Non-blocking remote put operation(s) for int/long/double/float.
	 *  - Should be implemented by an RDMA wherever possible.
	 *  - Should progress independently of calls made to x10lib by threads in the dest process.
	 */
	/*
	 * template <typename T>
	 * int NbPutXXX(T val, x10_gas_ref_t dest, x10_switch_t hndl);
	 */
	int NbPutInt(int val, x10_gas_ref_t dest, x10_switch_t hndl);
	int NbPutLong(long val, x10_gas_ref_t dest, x10_switch_t hndl);
	int NbPutDouble(double val, x10_gas_ref_t dest, x10_switch_t hndl);
	int NbPutFloat(float val, x10_gas_ref_t dest, x10_switch_t hndl);
	
} /* closing brace for namespace x10lib */

/* C Lang Interface */
extern "C" {
	/* Initialization */
	int x10_init(x10_async_handler_t *handlers, int n);
		
	/* Termination */
	int x10_finalize();
		
	/* Fetch n contiguous bytes from src address on a remote node to the dest address
	 * on the local node
	 */
	int x10_get(x10_gas_ref_t src, void *dest, int n);
	
	
	/* Non-blocking remote get operation for contiguous bytes */
	int x10_get_nb(x10_gas_ref_t src, void *dest, int n, x10_switch_t hndl);
		
	/* Remote get operation(s) for int/long/double/float */
	int x10_get_value_int(x10_gas_ref_t src);
		
	long x10_get_value_long(x10_gas_ref_t src);
		
	double x10_get_value_double(x10_gas_ref_t src);
		
	float x10_get_value_float(x10_gas_ref_t src);
		
	/* Strided get operations */
	int x10_get_s(x10_gas_ref_t src, int src_stride[], void *dest, int dest_stride[],
				int count[], int levels);
				
	int x10_get_s_nb(x10_gas_ref_t src, int src_stride[], void *dest, int dest_stride[],
				int count[], int levels, x10_switch_t hndl);
					
	/* Generalized I/O vector get operations */
	int x10_get_v(x10_giov_ref_t *dsrc, int len);
		
	int x10_get_v_nb(x10_giov_ref_t *dsrc, int len, x10_switch_t hndl);
		
	/* Write n contiguous bytes from src address on the local node to the dest address
	 * on a remote node
	 */
	int x10_put(void *src, x10_gas_ref_t dest, int n);
		
	/* Non-blocking remote put operation for contiguous bytes */
	int x10_put_nb(void *src, x10_gas_ref_t dest, int n, x10_switch_t hndl);
		
	/* Remote put operation(s) for int/long/double/float */
	int x10_put_int(int val, x10_gas_ref_t dest);
		
	int x10_put_long(long val, x10_gas_ref_t dest);
		
	int x10_put_double(double val, x10_gas_ref_t dest);
		
	int x10_put_float(float val, x10_gas_ref_t dest);
		
	/* Non-blocking remote put operation(s) for int/long/double/float */
	int x10_put_int_nb(int val, x10_gas_ref_t dest, x10_switch_t hndl);
		
	int x10_put_long_nb(long val, x10_gas_ref_t dest, x10_switch_t hndl);
		
	int x10_put_double_nb(double val, x10_gas_ref_t dest, x10_switch_t hndl);
		
	int x10_put_float_nb(float val, x10_gas_ref_t dest, x10_switch_t hndl);
	
} /* closing brance for extern "C" */

#endif /* __X10_X10LIB_H */

/** X10lib interface.
 * (C) IBM Corporation 2007
 */ 
#ifndef X10XFER_H_
#define X10XFER_H_

#include "x10-gas.h"
#include "x10-switch.h"

namespace x10lib {


/** 
 * The place where the current activity is executing.
 */

 place_t here();

//*******************GET OPERATIONS**********************************

/** A remote get operation for contiguous bytes. Fetches nbytes bytes
 * from address src on node to dest. 
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 * On return from this call, the bytes are available in dest.
 */
 int get(gas_ref_t src,  void* dest, int nbytes);

/** Initiation of a nonblocking remote get operation for contiguous bytes. 
 * Fetches nbytes bytes from address src on node to dest. 
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 * The bytes will be available only after a call wait(handle)
 * returns OK.
 * Note: X10lib does not support "implicit" nonblocking calls. The programmer
 * should simply create a "default" switch and use that explicitly.
 */
 int get_nb(gas_ref_t src, void* dest, int nbytes, 
                      switch_t handle);
                      
/** A remote get operation for int/long/double/float. 
 * Fetches and returns the number of bytes indicated by the type
 * from address src on node. 
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 */             

template <typename T>
 T getValue_int (gas_ref_t src);

// Also operations for strided copy, generalized i/o vector copy.

//*******************PUT OPERATIONS**********************************
/** A remote put operation for contiguous bytes. Writes nbytes bytes
 * from address src to address dest on node.
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the dest process.
 * On return from this call, the bytes are available to be read from
 * the remote node in dest.
 */
 int put(void* src, gas_ref_t dest, int bytes);


/** Initiation of a nonblocking remote put operation for contiguous bytes. 
 * Copies nbytes bytes from address src to address dest on node. 
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 * The bytes will be available only after a call wait(handle)
 * returns OK.
 * Note: X10lib does not support "implicit" nonblocking calls. The programmer
 * should simply create a "default" switch and use that explicitly.
 */
 int put_nb(void* src, gas_ref_t dest, int bytes, 
                      switch_t handle);

/** A remote immediate put operation for int/long/double/float. 
 * Places the given value at address dest on node.
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 */             
template <typename T>
 int putValue_int(T value, gas_ref_t dest);

// Way to organize processors into groups, e.g. using a clock.

// Perform collective operations, restricted to a group.

// 
}
#endif /*X10XFER_H_*/

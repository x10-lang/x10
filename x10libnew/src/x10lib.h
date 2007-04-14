/** X10lib interface.
 * (C) IBM Corporation 2007
 */ 
#ifndef X10LIB_H_
#define X10LIB_H_

#include "x10-clock.h";
#include "x10-switch.h";
#include "x10-gas.h";
#include "x10-activity.h"

extern int X10_init(X10_async_handler_t* handlers, int num);
extern int X10_finalize();
extern int X10_cleanup();

namespace x10lib {

enum error_code {X10_ILLEGAL_ARG, X10_OK, X10_NOT_OK};

/** 
 * The place where the current activity is executing.
 */

extern X10_place_t here();

//*******************GET OPERATIONS**********************************

/** A remote get operation for contiguous bytes. Fetches nbytes bytes
 * from address src on node to dest. 
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 * On return from this call, the bytes are available in dest.
 */
extern int X10_get(X10_gas_ref_t src,  void* dest, int nbytes);

/** Initiation of a nonblocking remote get operation for contiguous bytes. 
 * Fetches nbytes bytes from address src on node to dest. 
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 * The bytes will be available only after a call X10_wait(handle)
 * returns X10_OK.
 * Note: X10lib does not support "implicit" nonblocking calls. The programmer
 * should simply create a "default" switch and use that explicitly.
 */
extern int X10_get_nb(X10_gas_ref_t src, void* dest, int nbytes, 
                      X10_switch_t handle);
                      
/** A remote get operation for int/long/double/float. 
 * Fetches and returns the number of bytes indicated by the type
 * from address src on node. 
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 */             

template <typename T>
extern T X10_getValue_int (x10_gas_ref_t src);

/* extern int X10_getValue_int(X10_gas_ref_t src);
extern long X10_getValue_long(X10_gas_ref_t src);
extern double X10_getValue_double(X10_gas_ref_t src);
extern float X10_getValue_float(X10_gas_ref_t src); */

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
extern int X10_put(void* src, X10_gas_ref_t dest, int bytes);


/** Initiation of a nonblocking remote put operation for contiguous bytes. 
 * Copies nbytes bytes from address src to address dest on node. 
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 * The bytes will be available only after a call X10_wait(handle)
 * returns X10_OK.
 * Note: X10lib does not support "implicit" nonblocking calls. The programmer
 * should simply create a "default" switch and use that explicitly.
 */
extern int X10_put_nb(void* src, X10_gas_ref_t dest, int bytes, 
                      X10_switch_t handle);

/** A remote immediate put operation for int/long/double/float. 
 * Places the given value at address dest on node.
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 */             
template <typename T>
extern int X10_putValue_int(T value, X10_gas_ref_t dest);

/* extern int X10_putValue_int(int value,       X10_gas_ref_t dest);
extern int X10_putValue_long(long value,     X10_gas_ref_t dest);
extern int X10_putValue_double(double value, X10_gas_ref_t dest);
extern int X10_putValue_float(float value,   X10_gas_ref_t dest);
*/

/** A remote immediate nonblocking put operation for int/long/double/float. 
 * Places the given value at address dest on node.
 * This operation should be implemented by an RDMA wherever possible. 
 * The operation should progress independently of calls made to X10lib 
 * by threads in the source process.
 */                   
extern int X10_putValue_int(int value,       X10_gas_ref_t dest, X10_switch_t handle);
extern int X10_putValue_long(long value,     X10_gas_ref_t dest, X10_switch_t handle);
extern int X10_putValue_double(double value, X10_gas_ref_t dest, X10_switch_t handle);
extern int X10_putValue_float(float value,   X10_gas_ref_t dest, X10_switch_t handle);


// Way to organize processors into groups, e.g. using a clock.

// Perform collective operations, restricted to a group.

// 
}                  

#endif /*X10LIB_H_*/

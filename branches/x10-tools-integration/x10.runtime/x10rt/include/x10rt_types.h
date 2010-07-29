#ifndef X10RT_TYPES_H
#define X10RT_TYPES_H

#ifdef __cplusplus
#define X10RT_C extern "C"
#else
#define X10RT_C
#endif


/** \file
 * Common types
 *
 * Typedefs and structs used in the X10RT API.
 */

/** An integer type capable of representing any place or quantity of places.
 */
typedef unsigned long x10rt_place;

/** An integer type capable of representing any message type id.
 */
typedef unsigned x10rt_msg_type;

/** An integer type capable of representing a remote void* or size_t.
 */
typedef unsigned long long x10rt_remote_ptr;

/** An integer type capable of representing the maximum size (in bytes) of an inter-place data copy.
 * This applies to both get and put transfers.
 */
typedef unsigned long x10rt_copy_sz;


/** A set of parameters common to many of the message sending / handling functions.
 */
struct x10rt_msg_params {

    /** The place where the message will be delivered. */
    x10rt_place dest_place;

    /** The type of the message being sent. */
    x10rt_msg_type type;

    /** The message itself.  May be NULL.  The memory is allocated and managed by the caller.  The
     * caller shall not write to or free the buffer while the call is in progress, and the callee
     * shall not continue to use the buffer after the call has returned.  In the previous sentence,
     * X10RT and the client code take on alternate roles depending on whether the call is
     * x10rt_send_*() or whether the call is a user callback triggered by #x10rt_probe().
     */
    void *msg;

    /** The length of the message in bytes.  If #msg is NULL then #len shall be 0. */
    unsigned long len;
};

/** A callback for processing a newly received message.
 */
typedef void x10rt_handler(const x10rt_msg_params *);

/** A callback that runs on the CPU on behalf of the GPU, just before a kernel.
 * This is used to configure the kernel, providing blocks, threads, and the
 * amount of dynamic ``shared memory'' per block (via the write-back
 * parameters).  It also provides the arguments to the kernel (i.e. the
 * captured state).  \todo work in progress
 */
typedef void x10rt_cuda_pre(const x10rt_msg_params *, size_t *blocks, size_t *threads, size_t *shm,
                            size_t *argc, char **argv, size_t *cmemc, char **cmemv);

/** A callback that runs on the CPU on behalf of the GPU, just after a kernel
 * has completed.  This is used for updating finish states and other things
 * that must be done after a kernel but cannot be done on the GPU.  \todo work
 * in progress
 */
typedef void x10rt_cuda_post(const x10rt_msg_params *, void *);

/** A callback for finding a buffer at a remote place, that will be the subject of a copy operation.
 */
typedef void *x10rt_finder(const x10rt_msg_params *, x10rt_copy_sz);

/** A callback for finishing off a copy operation.  This can be arbitrary code
 * e.g. to inform the requester of the copy that the copy is complete.
 */
typedef void x10rt_notifier(const x10rt_msg_params *, x10rt_copy_sz);

/** The list of remote operations supported.
 */
typedef enum {
    X10RT_OP_ADD = 0, 
    X10RT_OP_AND = 1,
    X10RT_OP_OR  = 2,
    X10RT_OP_XOR = 3
} x10rt_op_type;

/** The list of optional x10rt_net features.
 */
typedef enum {
    X10RT_OPT_REMOTE_OP = 0
} x10rt_opt;

#endif

// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100

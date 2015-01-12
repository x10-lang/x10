/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10RT_TYPES_H
#define X10RT_TYPES_H

#ifdef __cplusplus
/** Used to expose c-compatible symbol names */
#define X10RT_C extern "C"
#else
/** Used to expose c-compatible symbol names */
#define X10RT_C
#endif

#include <stdint.h>

/** \file
 * Common types
 *
 * Typedefs and structs used in the X10RT API.
 */

/** An integer type capable of representing any place or quantity of places.
 */
typedef uint32_t x10rt_place;

/** An integer type capable of representing an endpoint within a place.
 */
typedef uint32_t x10rt_endpoint;

/** An integer type capable of representing any global team identifier.  Teams are used in the
 * collective operations API.
 */
typedef uint32_t x10rt_team;

/** User callback to signal that non-blocking operations have completed.
 */
typedef void x10rt_completion_handler (void *arg);

/** User callback to signal that non-blocking team construction operations have completed.
 */
typedef void x10rt_completion_handler2 (x10rt_team, void *arg);

/** An integer type capable of representing any message type id.
 */
#if defined(X10RT_32BIT_MSG_IDS) 
// use 32 bits to maintain 4 byte alignment for PAMI
typedef uint32_t x10rt_msg_type;
#else
// TODO: Remove?  We no longer support bgp or lapi
// only need 16 bits; optimize message size & maintain compatibility with binary pgas libraries (bgp, lapi)
typedef uint16_t x10rt_msg_type;
#endif

/** An integer type capable of representing a remote void* or size_t.
 */
typedef uint64_t x10rt_remote_ptr;

/** User callback to signal that non-blocking memory allocation operation has completed.
 */
typedef void x10rt_completion_handler3 (x10rt_remote_ptr ptr, void *arg);

/** An integer type capable of representing the maximum size (in bytes) of an inter-place data copy.
 * This applies to both get and put transfers.
 */
typedef uint32_t x10rt_copy_sz;


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
    uint32_t len;

    /** The endpoint where the message will be delivered at the dest place. */
    x10rt_endpoint dest_endpoint;
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
typedef void x10rt_cuda_post(const x10rt_msg_params *, size_t blocks, size_t threads, size_t shm,
                             size_t argc, char *argv, size_t cmemc, char *cmemv);

/** A callback for finding a buffer at a remote place, that will be the subject of a copy operation.
 */
typedef void *x10rt_finder(const x10rt_msg_params *, x10rt_copy_sz);

/** A callback for finishing off a copy operation.  This can be arbitrary code
 * e.g. to inform the requester of the copy that the copy is complete.
 */
typedef void x10rt_notifier(const x10rt_msg_params *, x10rt_copy_sz);

/** The list of remote operations supported.
 * The numerical values used correspond to the PAMI HFI operation values
 */
typedef enum {
    X10RT_OP_ADD = 0x00, // the ordering of these is important
    X10RT_OP_AND = 0x01,
    X10RT_OP_OR  = 0x02,
    X10RT_OP_XOR = 0x03
} x10rt_op_type;

/** The list of operations supported when doing a reduction. 
 * \see x10rt_allreduce
 */
typedef enum {
    X10RT_RED_OP_ADD = 0,
    X10RT_RED_OP_MUL = 1,
    X10RT_RED_OP_AND = 3,
    X10RT_RED_OP_OR  = 4,
    X10RT_RED_OP_XOR = 5,
    X10RT_RED_OP_MAX = 6,
    X10RT_RED_OP_MIN = 7
} x10rt_red_op_type;

/** The struct that must be used when using #X10RT_RED_TYPE_DBL_S32.
 * \see #x10rt_allreduce #x10rt_red_type
 */
struct x10rt_dbl_s32 {
    /** The floating point value that will be compared in the operation*/
    double val;
    /** The index identifying the value that will be preserved over the operation*/
    int32_t idx;
};

/** The list of types supported when doing a reduction operation.  \see x10rt_allreduce.
 * Signed/unsigned integer types are available.  Floating point types are supported but may not be
 * used when doing bitwise arithmetic.  Only MIN/MAX can be used when using #X10RT_RED_TYPE_DBL_S32.
 */
typedef enum
  {
    /** Unsigned byte*/ X10RT_RED_TYPE_U8  = 0,
    /** Signed byte*/ X10RT_RED_TYPE_S8  = 1,
    /** Unsigned word*/ X10RT_RED_TYPE_S16 = 2,
    /** Signed word*/ X10RT_RED_TYPE_U16 = 3,
    /** Unsigned dword*/ X10RT_RED_TYPE_S32 = 4,
    /** Signed dword*/ X10RT_RED_TYPE_U32 = 5,
    /** Signed qword*/ X10RT_RED_TYPE_S64 = 6,
    /** Unsigned qword*/ X10RT_RED_TYPE_U64 = 7,
    /** Double precision IEEE float*/ X10RT_RED_TYPE_DBL = 8,
    /** Single precision IEEE float*/ X10RT_RED_TYPE_FLT = 9,
    /** A pair of double and signed dword*/ X10RT_RED_TYPE_DBL_S32 = 10,
    /** std::complex<double> */ X10RT_RED_TYPE_COMPLEX_DBL = 11    
} x10rt_red_type;

/** The list of optional x10rt_net features.
 */
typedef enum {
	X10RT_COLL_NOCOLLECTIVES = 0,
	X10RT_COLL_BARRIERONLY = 1,
    X10RT_COLL_ALLBLOCKINGCOLLECTIVES = 2,
    X10RT_COLL_NONBLOCKINGBARRIER = 3,
    X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES = 4
} x10rt_coll_type;

/**
 * Structure to hold a remote update operation
 */
typedef struct {
    /** Destination place           */
    unsigned int       dest;
    /** Atomic operation type (x10rt_op_type) */
    unsigned int       op;
    /** buffer on destination place */
    unsigned long long dest_buf;
    /** operand value for operation */
    unsigned long long value;
} x10rt_remote_op_params;

/**
 * Structure to hold runtime statistics
 */
typedef struct {
    /** Total number of client-provided bytes sent by X10RT */
    uint64_t bytes_sent;
    /** Total number of client-provided messages sent by X10RT */
    uint64_t messages_sent;
    /** Total number of client-provided bytes received by X10RT */
    uint64_t bytes_received;
    /** Total number of client-provided messages received by X10RT */
    uint64_t messages_received;
} x10rt_msg_stats;

/**
 * Structure to hold runtime statistics for various kinds of messages
 */
typedef struct {
    /** Statistics related to x10rt_send_msg calls */
    x10rt_msg_stats msg;

    /** Statistics related to the preliminary byte buffer part of x10rt_send_put calls (not the actual copy) */
    x10rt_msg_stats put;
    /** The total number of bytes copied as a result of put operations sent by this place */
    uint64_t put_copied_bytes_sent;
    /** The total number of bytes copied as a result of put operations received at this place */
    uint64_t put_copied_bytes_received;

    /** Statistics related to the preliminary byte buffer part of x10rt_send_get calls (not the actual copy) */
    x10rt_msg_stats get;
    /** The total number of bytes copied as a result of get operations sent by this place */
    uint64_t get_copied_bytes_sent;
    /** The total number of bytes copied as a result of get operations received by this place  */
    uint64_t get_copied_bytes_received;
} x10rt_stats;

/**
 * Error code for x10rt apis.
 */
typedef enum {
    X10RT_ERR_OK			= 0,   /* No error */
    X10RT_ERR_MEM			= 1,   /* Out of memory error */
    X10RT_ERR_INVALID		= 2,   /* Invalid method call, at this time (e.g. probe() before init()) */
    X10RT_ERR_UNSUPPORTED	= 3,   /* Not supported by this implementation of X10RT */
    X10RT_ERR_INTL			= 254, /* Internal implementation error */
    X10RT_ERR_OTHER			= 255  /* Other unclassified runtime error */
} x10rt_error;

#endif

// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100

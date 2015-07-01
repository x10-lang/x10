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

#ifndef X10RT_CUDA_H
#define X10RT_CUDA_H

#include <cstdlib>

#include <x10rt_types.h>

/** \file
 * CUDA layer.
 *
 * \see \ref structure
 */

/** CUDA context.  Since more than one CUDA device can be used by a given host, the contexts allow them to be handled independently.
 */
struct x10rt_cuda_ctx;

/** Number of Devices.  \returns The number of distinct CUDA devices available at this host.
 */
X10RT_C unsigned x10rt_cuda_ndevs (void);

/** Initialise a CUDA device for use by X10RT.
 *
 * \param device A number between 0 and #x10rt_cuda_ndevs()-1 that identifies the particular piece of hardware to use.
 * \returns A context that is used to control the device.
 */
X10RT_C x10rt_cuda_ctx *x10rt_cuda_init (unsigned device);


/** Register callbacks to handle a message when received by a particular CUDA device.
 *
 * \see x10rt_register_msg_receiver
 *
 * \param ctx The CUDA device in question.
 * \param msg_type The message type that these callbacks will process.
 * \param pre As seen in x10rt_register_msg_receiver
 * \param post As seen in x10rt_register_msg_receiver
 * \param cubin As seen in x10rt_register_msg_receiver
 * \param kernel_name As seen in x10rt_register_msg_receiver
 */
X10RT_C void x10rt_cuda_register_msg_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                               x10rt_cuda_pre *pre, x10rt_cuda_post *post,
                                               const char *cubin, const char *kernel_name);

/** Register callbacks to handle a message when received by a particular CUDA device.
 *
 * \see x10rt_register_get_receiver
 *
 * \param ctx The CUDA device in question.
 * \param msg_type The message type that these callbacks will process.
 * \param cb As seen in x10rt_register_get_receiver
 */
X10RT_C void x10rt_cuda_register_get_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                               x10rt_notifier *cb1);

/** Register callbacks to handle a message when received by a particular CUDA device.
 *
 * \see x10rt_register_put_receiver
 *
 * \param ctx The CUDA device in question.
 * \param msg_type The message type that these callbacks will process.
 * \param cb As seen in x10rt_register_put_receiver
 */
X10RT_C void x10rt_cuda_register_put_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                               x10rt_notifier *cb);


/** Signal that all messages have been set up.  If messages arrive at this CUDA
 * device that have not had callbacks registered for them, the execution will
 * abort with an error.
 *
 * \see x10rt_registration_complete
 *
 * \param ctx The CUDA device in question.
 */
X10RT_C void x10rt_cuda_registration_complete (x10rt_cuda_ctx *ctx);

/** Send a message to the local CUDA device (invoke a kernel).
 *
 * \see x10rt_send_msg
 *
 * \param ctx The CUDA device in question.
 * \param p As seen in x10rt_send_msg
 */
X10RT_C void x10rt_cuda_send_msg (x10rt_cuda_ctx *ctx, x10rt_msg_params *p);

/** Copy memory from a CUDA device (DMA get).
 *
 * \see x10rt_send_get
 *
 * \param ctx The CUDA device in question.
 * \param p As seen in x10rt_send_get
 * \param srcAddr As seen in x10rt_send_get
 * \param dstAddr As seen in x10rt_send_get
 * \param len As seen in x10rt_send_get
 */
X10RT_C void x10rt_cuda_send_get (x10rt_cuda_ctx *ctx, x10rt_msg_params *p,
                                  void *srcAddr, void *dstAddr, x10rt_copy_sz len);

/** Copy memory to a local CUDA device (DMA put).
 *
 * \see x10rt_send_put
 *
 * \param ctx The CUDA device in question.
 * \param p As seen in x10rt_send_put
 * \param srcAddr As seen in x10rt_send_get
 * \param dstAddr As seen in x10rt_send_get
 * \param len As seen in x10rt_send_put
 */
X10RT_C void x10rt_cuda_send_put (x10rt_cuda_ctx *ctx, x10rt_msg_params *p,
                                  void *srcAddr, void *dstAddr, x10rt_copy_sz len);

/** Autoconfigure a kernel.
 *
 * \see x10rt_blocks_threads
 *
 * \param ctx The CUDA device in question.
 * \param type As seen in x10rt_blocks_threads
 * \param dyn_shm As seen in x10rt_blocks_threads
 * \param blocks As seen in x10rt_blocks_threads
 * \param threads As seen in x10rt_blocks_threads
 * \param cfg As seen in x10rt_blocks_threads
 */
X10RT_C void x10rt_cuda_blocks_threads (x10rt_cuda_ctx *ctx, x10rt_msg_type type, int dyn_shm,
                                        int *blocks, int *threads, const int *cfg);

/** Blocks until the CUDA device has completed all preceding requested tasks.
 *
 * \see x10rt_device_sync
 *
 * \param ctx The CUDA device to synchronize.
 */
X10RT_C void x10rt_cuda_device_sync (x10rt_cuda_ctx *ctx);

/** Allocate memory on the given CUDA device.  Note that the CUDA GPU will adopt the same alignment
 * and pointer width characteristics as the host, so a void* is adequate for holding these remote
 * pointers.
 *
 * \see x10rt_remote_alloc
 *
 * \param ctx The CUDA device whose RAM is to be used.
 *
 * \param sz The number of bytes to allocate.
 *
 * \returns A pointer to the newly created memory (not valid on the host).
 */
X10RT_C void *x10rt_cuda_device_alloc (x10rt_cuda_ctx *ctx, size_t sz);

/** Free the given memory block on the given CUDA device.
 *
 * \see x10rt_remote_free
 *
 * \param ctx The CUDA device where the memory is found.
 *
 * \param ptr A pointer to the beginning of the memory to be freed.
 */
X10RT_C void x10rt_cuda_device_free (x10rt_cuda_ctx *ctx, void *ptr);

/** Do any outstanding work relating to the given CUDA device.  This will invoke kernels, spool
 * DMAs, etc.  Callbacks will be called.
 *
 * \see x10rt_probe
 * 
 * \param ctx The CUDA device where work may be pending.
 * \returns true if something is actively running in the GPU or is in the GPU work queue
 */
X10RT_C bool x10rt_cuda_probe (x10rt_cuda_ctx *ctx);

/** Clean up and destroy the given CUDA device.
 *
 * \param ctx The CUDA device that has outstayed its welcome.
 */
X10RT_C void x10rt_cuda_finalize (x10rt_cuda_ctx *ctx); 

#endif
// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100

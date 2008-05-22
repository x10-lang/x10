#ifndef __X10_ACTS_H__
#define __X10_ACTS_H__

#include <stdio.h>

#include "x10_types.h"

extern x10_place_t __x10_here;

extern unsigned int __x10_numplaces;

/* init/finalize */

EXTERN x10_err_t
x10_init();

EXTERN x10_err_t
x10_finalize();

/* async */

/**
 * \brief spawn an async on given target (NON-BLOCKING).
 *         x10lib assumes that code (atleast the __x10_callback_asynswitch) is replicated everywhere 
 
 * \param tgt 			target place
 * \param closure               pointer to async closure (see x10_types.h)
 * \param cl_size               size of the async closure
 * \param frecord               pointer to the finish record (see x10_types.h)
 * \param clocks                clock set for the async (see x10_types.h)
 * \param num_clocks            number of clocks in the clock set

 *  \return			handle to wait for
 */

EXTERN x10_comm_handle_t
x10_async_spawn(const x10_place_t tgt, const x10_async_closure_t* closure, const size_t cl_size, 
		const x10_finish_record_t* frecord, const x10_clock_t* clocks, const int num_clocks);

/**
 * \brief wait for the async_spawn to complete locally  (BLOCKING)
 
 * \param handle                handle returned by x10_async_spawn (see x10_types.h)

 * \return                       returns an error or success
 */

EXTERN x10_err_t
x10_async_spawn_wait(x10_comm_handle_t handle);


/**
 * \brief check for any asyncs in the internal async queue and execute them.
 *  This method should be used on the receiver side to make progress (NON-BLOCKING)
 */
EXTERN x10_err_t
x10_probe();

/**
 * \brief performs x10_probe infinitely, until a termination message is received(BLOCKING)
 */
EXTERN x10_err_t
x10_infinite_poll();

/**
 * \brief  array put(NON-BLOCKING)
 */
EXTERN x10_comm_handle_t
x10_array_put(const x10_addr_t src, 
	      const x10_addr_t dst, const size_t offset, const size_t nbytes);

/**
 * \brief array get(NON-BLOCKING)
 */
EXTERN x10_comm_handle_t
x10_array_get(const x10_addr_t dst, 
	      const x10_addr_t src, const size_t offset, const size_t nbytes);

/**
 * \brief async array put(NON-BLOCKING)
 */
EXTERN x10_comm_handle_t
x10_async_array_put(const x10_place_t tgt, const x10_addr_t src, const size_t nbytes,
		    const x10_async_closure_t* dst_closure, const size_t dst_cl_size);

/* finish */

/**
 * \brief start the finish_scope (called by root activity only)

 * \param frecord	           the finish record
 * \param multi_ex_buf             buffer for the resulting multi_exceptions (if any)
 * \param ex_offsets		   offsets array for individual exceptions 
 * \param max_ex_buf_size          maximum size of the multi_ex_buf 
 * \param max_num_exceptions       maximum number of individual exceptions 
 */

EXTERN x10_err_t
x10_finish_begin(x10_finish_record_t* frecord, void* mult_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions);

EXTERN x10_err_t
x10_finish_begin_global(x10_finish_record_t* frecord, void* mult_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions);

/**
 * \brief end the finish_scope (called by root activity only).
                       Waits for global termination of all the activities (BLOCKING)

 * \param finish_record           pointer to finish_record
 * \param  num_exceptions         total number of exceptions
 */
EXTERN x10_err_t
x10_finish_end(const x10_finish_record_t* finish_record, int* num_exceptions);

/**
 * \brief notify the "root" that I have finished (called by children activity only)

 * \param frecord 		finish record
 * \param ex_buf		exception buffer 
 * \param ex_buf_size		size of the exception buffer
 */
EXTERN x10_err_t
x10_finish_child(const x10_finish_record_t* frecord, void* ex_buf, int ex_buf_size);

/* clocks  */

/**
 * \brief initialize a clock c (see x10_types.h for x10_clock_t)
 */
EXTERN x10_err_t
x10_clock_init(x10_clock_t* c);

EXTERN x10_err_t
x10_clock_free (x10_clock_t* c);

/**
 * \brief perform a resume operation on clock c
 */
EXTERN x10_err_t
x10_clock_resume(x10_clock_t* c);

/**
 * \brief drop a clock c
 */
EXTERN x10_err_t
x10_clock_drop(x10_clock_t* c);

/**
 * \brief perform a next operation
 */
EXTERN x10_err_t
x10_next(x10_clock_t* c);

EXTERN x10_err_t
x10_next_all();

/* remote reference  */

/**
 * \brief serialize a reference (local or remote)
 */
EXTERN x10_remote_ref_t 
x10_serialize_ref(x10_addr_t ref);

/**
 * \brief deserialize a remote reference
 */
EXTERN x10_addr_t
x10_deserialize_ref(x10_remote_ref_t ref);

/**
 *\ brief get the location of a reference
 */
EXTERN x10_place_t
x10_get_loc(x10_addr_t ref);

/**
 * \brief check if the reference is local
 */
EXTERN bool
x10_is_localref (x10_addr_t ref);


EXTERN x10_addr_t
x10_get_addr (x10_addr_t ref);
#endif

/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async.cc,v 1.21 2008-01-06 03:28:51 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for X10Lib's async interface. **/

#include <lapi.h>

#include <x10/am.h>
#include <x10/async.h>
#include <x10/types.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include "__x10lib.h__"

/* local methods */
static void*
AsyncSpawnHandler(lapi_handle_t hndl, void *uhdr,
			uint *uhdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **user_info);

using namespace x10lib;

/* The following method should be available during
 * x10lib initialization; mark it as external.
 */

/* Initialize Async Mechanism */
x10_err_t AsyncInit(void)
{
	X10_DEBUG(1, "Entry");
	LRC(LAPI_Addr_set(__x10_hndl, (void *)AsyncSpawnHandler,
				ASYNC_SPAWN_HANDLER));
	X10_DEBUG(1, "Exit");
	return X10_OK;
}

/* Beginning of x10lib's async interface */
namespace x10lib {

/* Spawn an inlinable async on a given place. */
x10_err_t AsyncSpawnInline(x10_place_t tgt,
				x10_async_handler_t hndlr,
				void *args, size_t size)
{
        X10_DEBUG(1, "Entry");
	
	lapi_cntr_t origin_cntr;
	int tmp;

	LRC(LAPI_Setcntr(__x10_hndl, &origin_cntr, 0));
	LRC(LAPI_Amsend(__x10_hndl, tgt,
			(void *)ASYNC_SPAWN_HANDLER, &hndlr,
			sizeof(hndlr), args, size,
			NULL, &origin_cntr, NULL));
	LRC(LAPI_Waitcntr(__x10_hndl, &origin_cntr, 1, &tmp));

	X10_DEBUG(1, "Exit");
	return X10_OK;
}

} /* closing brace for namespace x10lib */

/* Spawn an inlinable async on a given place. */
extern "C"
x10_err_t x10_async_spawn_inline(x10_place_t tgt,
				x10_async_handler_t hndlr,
				void *args, size_t size)
{
	return x10lib::AsyncSpawnInline(tgt, hndlr, args, size);
}

/* End of x10lib's async interface */

/* async handler implementation */
static void*
AsyncSpawnHandler(lapi_handle_t hndl, void *uhdr,
			uint *uhdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **user_info)
{
	X10_DEBUG(1, "Entry");

	lapi_return_info_t *ret_info =
			(lapi_return_info_t *)msg_len;
	assert(ret_info->udata_one_pkt_ptr);

	AsyncSwitch(*((x10_async_handler_t *)uhdr),
			(x10_async_arg_t *)ret_info->udata_one_pkt_ptr, 1);

	ret_info->ctl_flags = LAPI_BURY_MSG;
	*comp_h = NULL;

	X10_DEBUG(1, "Exit");

	return NULL;
}

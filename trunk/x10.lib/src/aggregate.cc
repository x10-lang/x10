/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate.cc,v 1.16 2007-06-27 17:06:57 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for inline aggregation. **/

#include <x10/aggregate.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include <stdarg.h>
#include <string.h>
#include <iostream>

using namespace x10lib;

static char __x10_agg_arg_buf[X10_MAX_AGG_HANDLERS][X10_MAX_AGG_TASKS][X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t)];

static int __x10_agg_counter[X10_MAX_AGG_HANDLERS][X10_MAX_AGG_TASKS];

static int __x10_agg_total[X10_MAX_AGG_HANDLERS];

typedef struct {
  x10_async_handler_t handler; 
  ulong len;
  int niter;
  void* buf;
} x10_agg_cmpl_t;

typedef struct {
  x10_async_handler_t handler;
  int niter;
} x10_agg_hdr_t;

static void
asyncSpawnCompHandlerAgg(lapi_handle_t *hndl, void *a)
{
	x10_agg_cmpl_t *c = (x10_agg_cmpl_t *)a;
	asyncSwitch(c->handler, (void *)(c->buf), c->niter);
	delete[] ((char*) c->buf);
	delete c;
}


static void *
asyncSpawnHandlerAgg(lapi_handle_t hndl, void *uhdr,
			uint *uhdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **user_info)
{
	x10_agg_hdr_t buf = *((x10_agg_hdr_t *)uhdr);
	lapi_return_info_t *ret_info =
					(lapi_return_info_t *)msg_len;
	if (ret_info->udata_one_pkt_ptr || (*msg_len) == 0) {
		asyncSwitch(buf.handler, ret_info->udata_one_pkt_ptr,
					buf.niter);
		ret_info->ctl_flags = LAPI_BURY_MSG;
		*comp_h = NULL;
		return NULL;
	} else {
		x10_agg_cmpl_t *c = new x10_agg_cmpl_t;
		c->len = *msg_len;
		c->handler = buf.handler;
		c->buf = (void *)new char [*msg_len];
		c->niter = buf.niter;
		*comp_h = asyncSpawnCompHandlerAgg;
		ret_info->ret_flags = LAPI_LOCAL_STATE;
		*user_info = (void *)c;
		return c->buf;
	}
}

x10_err_t 
asyncRegisterAgg()
{

	LRC(LAPI_Addr_set(__x10_hndl, (void *)asyncSpawnHandlerAgg, 1));
	return X10_OK;
}


static x10_err_t 
asyncSpawnInlineAgg_i(x10_place_t tgt,
				x10_async_handler_t hndlr, size_t size)
{

	__x10_agg_counter[hndlr][tgt]++;
	__x10_agg_total[hndlr]++;

	if ((__x10_agg_total[hndlr]+1) * size >=
				X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t) ||
			(__x10_agg_total[hndlr]+1) >= X10_MAX_AGG_SIZE) {
		int max = 0;
		int task = 0;

		for (x10_place_t i = 0; i < __x10_num_places; i++) {
			if (__x10_agg_counter[hndlr][i] > max) {
				max = __x10_agg_counter[hndlr][i];
				task = i;
			}
		}

		x10_agg_hdr_t buf;

		buf.handler = hndlr;
		buf.niter = __x10_agg_counter[hndlr][task];
		lapi_cntr_t cntr;
		int tmp;
		LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
		LRC(LAPI_Amsend(__x10_hndl, task, (void *)1, &buf,
				sizeof(x10_agg_hdr_t),
				(void *)__x10_agg_arg_buf[hndlr][task],
				size * __x10_agg_counter[hndlr][task],
				NULL, &cntr, NULL));
		LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));
		__x10_agg_total[hndlr] -= max;
		__x10_agg_counter[hndlr][task] = 0;
	}
	return X10_OK;
}

namespace x10lib {

x10_err_t asyncFlush(x10_async_handler_t hndlr, size_t size)
{
	lapi_cntr_t cntr;
	int tmp;
	x10_agg_hdr_t buf;
	buf.handler = hndlr;

	for (int j = 0; j < __x10_num_places; j++) {
		if (__x10_agg_counter[hndlr][j] != 0) {
			buf.niter = __x10_agg_counter[hndlr][j];
			LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
			LRC(LAPI_Amsend(__x10_hndl, j, (void *)1, &buf,
						sizeof(x10_agg_hdr_t),
						(void *)__x10_agg_arg_buf[hndlr][j],
						size * __x10_agg_counter[hndlr][j],
						NULL, &cntr, NULL));
			LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));
		}
		__x10_agg_total[hndlr] -= __x10_agg_counter[hndlr][j];
		__x10_agg_counter[hndlr][j] = 0;
	}
	return X10_OK;
}
x10_err_t
asyncSpawnInlineAgg(x10_place_t tgt, x10_async_handler_t hndlr,
			void *args, size_t size)
{
        assert (size <= X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t));
	int count = __x10_agg_counter[hndlr][tgt];
	memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]), args, size);
	return asyncSpawnInlineAgg_i(tgt, hndlr, size);
}

x10_err_t
asyncSpawnInlineAgg(x10_place_t tgt, x10_async_handler_t hndlr,
			int n, ...)
{
	va_list list;
	va_start(list, n);
	lapi_cntr_t origin_cntr;

	size_t size = sizeof(x10_async_arg_t);
        assert (n * size <= X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t));
	size_t count = __x10_agg_counter[hndlr][tgt];
	for (int i = 0; i < n; i++) {
		__x10_agg_arg_buf[hndlr][tgt][count * size +
					i * sizeof(x10_async_arg_t)] =
			va_arg(list, x10_async_arg_t);
	}
	va_end(list);
	return asyncSpawnInlineAgg_i(tgt, hndlr, size);
}

x10_err_t
asyncSpawnInlineAgg(x10_place_t tgt, x10_async_handler_t hndlr,
		x10_async_arg_t arg0)
{
	size_t size = sizeof(x10_async_arg_t);
	size_t count = __x10_agg_counter[hndlr][tgt];
	memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]),
			&arg0, sizeof(x10_async_arg_t));
	return asyncSpawnInlineAgg_i(tgt, hndlr, size);
}

x10_err_t
asyncSpawnInlineAgg(x10_place_t tgt, x10_async_handler_t hndlr,
		x10_async_arg_t arg0, x10_async_arg_t arg1)
{
	size_t size = 2 * sizeof(x10_async_arg_t);
	int count = __x10_agg_counter[hndlr][tgt];

	memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]),
			&arg0, sizeof(x10_async_arg_t));
	memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size +
			sizeof(x10_async_arg_t)]),
			&arg1, sizeof(x10_async_arg_t));
	return asyncSpawnInlineAgg_i(tgt, hndlr, size);
}

} /* closing brace for namespace x10lib */


x10_err_t
x10_async_spawn_inline_agg1(x10_place_t tgt,
			x10_async_handler_t hndlr, x10_async_arg_t arg0)
{
	return x10lib::asyncSpawnInlineAgg(tgt, hndlr, arg0);
}

x10_err_t
x10_async_spawn_inline_agg2(x10_place_t tgt,
			x10_async_handler_t hndlr,
			x10_async_arg_t arg0, x10_async_arg_t arg1)
{
	return x10lib::asyncSpawnInlineAgg(tgt, hndlr, arg0, arg1);
}

x10_err_t
x10_async_flush(x10_async_handler_t hndl, size_t size)
{
	return x10lib::asyncFlush(hndl, size);
}

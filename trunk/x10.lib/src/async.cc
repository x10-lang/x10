/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async.cc,v 1.17 2007-10-19 16:04:28 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <x10/async.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include <stdarg.h>
#include <lapi.h>

using namespace x10lib;

static void *
asyncSpawnHandler(lapi_handle_t hndl, void *uhdr,
		  uint *uhdr_len, ulong* msg_len,
		  compl_hndlr_t **comp_h, void **user_info)
{
  X10_DEBUG (1, "Entry");

  lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
  assert (ret_info->udata_one_pkt_ptr);

  asyncSwitch(*((x10_async_handler_t *)uhdr),
	      (x10_async_arg_t *)ret_info->udata_one_pkt_ptr,
	      1);
 
  ret_info->ctl_flags = LAPI_BURY_MSG;
  *comp_h = NULL;

  X10_DEBUG (1, "Exit");
  return NULL;
}


namespace x10lib {

  extern lapi_handle_t __x10_hndl;

  x10_err_t
  asyncSpawnInline(x10_place_t tgt, x10_async_handler_t hndlr,
		   void* args, size_t size)
  {
    X10_DEBUG (1, "Entry");
    
    extern lapi_handle_t __x10_hndl;
    
    lapi_cntr_t origin_cntr;
    int tmp;
    
    LRC(LAPI_Setcntr(__x10_hndl, &origin_cntr, 0));

    LRC(LAPI_Amsend(__x10_hndl, tgt, (void *) ASYNC_SPAWN_HANDLER, &hndlr,
		    sizeof(hndlr), args, size,
		    NULL, &origin_cntr, NULL));

    LRC(LAPI_Waitcntr(__x10_hndl, &origin_cntr, 1, &tmp));
    
    X10_DEBUG (1, "Exit");

    return X10_OK;
  }

} /* closing brace for namespace x10lib */

x10_err_t
asyncInit()
{
  X10_DEBUG (1, "Entry");

  LRC(LAPI_Addr_set(__x10_hndl, (void *)asyncSpawnHandler, ASYNC_SPAWN_HANDLER));

  X10_DEBUG (1, "Exit");
  return X10_OK;
}
extern "C"
x10_err_t
x10_async_spawn_inline (x10_place_t tgt, x10_async_handler_t hndlr, void* args, size_t size)
{
  X10_DEBUG (1, "Entry");

  x10_err_t err = x10lib::asyncSpawnInline(tgt, hndlr, args, size);

  X10_DEBUG (1, "Exit");
  return err;
}

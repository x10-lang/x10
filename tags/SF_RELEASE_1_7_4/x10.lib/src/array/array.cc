/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array.cc,v 1.17 2008-01-19 18:20:08 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** helper functions (internal) for array classe */

#include <x10/am.h>
#include <x10/types.h>
#include <x10/local_array.h>
#include <x10/array_table.h>
#include <x10/xmacros.h>
#include <iostream>
#include "lapi.h"
#include "__x10lib.h__"

void* 
arrayConstructionHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			  ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  int handle = * ((int* )uhdr);
  
  lapi_return_info_t* ret_info_ptr =(lapi_return_info_t*)  msg_len;
  
  assert (ret_info_ptr->udata_one_pkt_ptr);
  
  x10lib::GenericArray* local_array = new x10lib::GenericArray;
  
  memcpy (local_array, (x10lib::GenericArray*) ret_info_ptr->udata_one_pkt_ptr, sizeof(x10lib::GenericArray));
  
  local_array->_data = new char [local_array->_nelements * local_array->_elsize];
  
  x10lib::registerLocalSection (local_array, handle);

  ret_info_ptr->ctl_flags = LAPI_BURY_MSG;
  *comp_h = NULL;

  return  NULL;
}

void* 
arrayDeletionHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
		      ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  int handle = * ((int*) uhdr);

  x10lib::GenericArray* local_array = (x10lib::GenericArray*) x10lib::getLocalSection (handle);
	
  delete local_array;
  
  x10lib::freeLocalSection (handle);

  lapi_return_info_t* ret_info_ptr =(lapi_return_info_t*)  msg_len;
  ret_info_ptr->ctl_flags = LAPI_BURY_MSG;
  *comp_h = NULL;

  return NULL;
}

x10_err_t
ArrayInit ()
{
  LRC(LAPI_Addr_set(x10lib::__x10_hndl, (void *)arrayConstructionHandler, ARRAY_CONSTRUCTION_HANDLER));
  LRC(LAPI_Addr_set(x10lib::__x10_hndl, (void *)arrayDeletionHandler, ARRAY_DELETION_HANDLER));

  return X10_OK;
}

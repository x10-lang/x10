/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array.cc,v 1.13 2007-12-07 11:12:48 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** helper functions (internal) for array classe */

#include <iostream>
#include <x10/alloc.h>
#include <x10/types.h>
#include <x10/local_array.h>
#include <x10/array_table.h>
#include <x10/xmacros.h>

#include "lapi.h"

using namespace std;

namespace x10lib
{
  extern lapi_handle_t __x10_hndl;
  Allocator* GlobalSMAlloc = NULL;
}

void* 
arrayConstructionHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			  ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  int handle = * ((int* )uhdr);
  
  lapi_return_info_t* ret_info_ptr =(lapi_return_info_t*)  msg_len;
  
  assert (ret_info_ptr->udata_one_pkt_ptr);
  
  x10lib::ByteArray* local_array = new x10lib::ByteArray;
  
  memcpy (local_array, (x10lib::ByteArray*) ret_info_ptr->udata_one_pkt_ptr, sizeof(x10lib::ByteArray));
  
  local_array->_data = new char [local_array->_nelements * local_array->_elsize];
  
  x10lib::registerLocalSection (local_array, handle);
}

void* 
arrayDeletionHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
		      ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  int handle = * ((int*) uhdr);

  x10lib::ByteArray* local_array = (x10lib::ByteArray*) x10lib::getLocalSection (handle);
	
  delete local_array;
  
  x10lib::freeLocalSection (handle);
}

x10_err_t
arrayInit ()
{
  x10lib::GlobalSMAlloc = new x10lib::Allocator (1UL<<5);

  LRC(LAPI_Addr_set(x10lib::__x10_hndl, (void *)arrayConstructionHandler, ARRAY_CONSTRUCTION_HANDLER));
  LRC(LAPI_Addr_set(x10lib::__x10_hndl, (void *)arrayDeletionHandler, ARRAY_DELETION_HANDLER));

}

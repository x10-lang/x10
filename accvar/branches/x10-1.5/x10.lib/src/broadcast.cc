/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: broadcast.cc,v 1.7 2008-04-19 06:12:16 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for broadcasting of args. **/

#include <lapi.h>

#include <x10/broadcast.h>
#include <x10/xmacros.h>

#include "__x10lib.h__"

char* __x10_bcast_buf;
lapi_cntr_t wait_cntr;

static void* __x10_broadcast_handler (lapi_handle_t* hndl, void* uhdr, ulong* uhdr_size,
				      ulong* msg_len, compl_hndlr_t** comp, void** uinfo)
 {
    __x10_bcast_buf = new char [*msg_len];

    LAPI_Setcntr (x10lib::__x10_hndl, &wait_cntr, 1); 

    *comp = NULL;
    return __x10_bcast_buf;
 }
namespace x10lib {

void* Broadcast_buffer (void* buffer, size_t len) 
{
   lapi_cntr_t compl_cntr;
   int tmp;
   LAPI_Setcntr (__x10_hndl, &compl_cntr, 0);

    if (__x10_my_place == 0) {

     for (x10_place_t p = 0; p  < __x10_num_places; ++p) {
        if (p == __x10_my_place) continue;
	LAPI_Amsend (__x10_hndl,
		     p,
		     (void*) __x10_broadcast_handler,
		     &len,
		     sizeof(len),
		     buffer,
		     len,
		     NULL,
		     NULL, 
		     &compl_cntr);
     }

     LAPI_Waitcntr (__x10_hndl, &compl_cntr, __x10_num_places-1, &tmp);
   } else {
        int tmp = 0;
	LAPI_Waitcntr (__x10_hndl, &wait_cntr, 1, &tmp); 
        LAPI_Setcntr (__x10_hndl, &wait_cntr, 0); 
        return __x10_bcast_buf;
   }  

  return buffer;
}

/* Broadcast args from root to other places. */
x10_err_t Broadcast(void *buffer, size_t nbytes)
{
  lapi_long_t remoteAddresses[__x10_num_places];
  
  LRC(LAPI_Address_init64(__x10_hndl, (lapi_long_t)buffer,
			   remoteAddresses));
  
  if (__x10_my_place == 0) {
    for (x10_place_t p = 0; p < __x10_num_places; p++) {
      if (p == __x10_my_place) continue;
      LAPI_Put(__x10_hndl, p, nbytes, (void*) remoteAddresses[p], (void*) buffer,
	       NULL, NULL, NULL);
    }
  }
  
  // TODO: replace this with a counter
  LAPI_Gfence(__x10_hndl);
  return X10_OK;
}
  
} /* closing brace for namepsace x10lib */

/* Broadcast args from root to other places. */
extern "C"
x10_err_t x10_broadcast(void *buffer, size_t nbytes)
{
  return x10lib::Broadcast(buffer, nbytes);
}

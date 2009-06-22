/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: handlers.h,v 1.1 2007-08-02 11:22:43 srkodali Exp $ */

#ifndef __HANDLERS_H__
#define __HANDLERS_H__

#include <lapi.h>
#include <x10/types.h>

namespace x10lib {

template <int RANK, template <int N> class REGION, template <int N> class DIST>
struct metaDataDescr
{
  REGION<RANK> region;
  //for UNIQUE distribution this is sufficient
  //will add more as need arises
};

template<typename T> 
void* 
arrayElementUpdate (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
                    ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  uint64_t pos = *((uint64_t*)uhdr);
  T val = *((T*) ((char*)uhdr + sizeof(uint64_t)));
  (*((T* )(GlobalSMAlloc->addr() + pos * sizeof(T)))) ^= val;
  return NULL; 
}
                        
template<typename T, int RANK, template <int N> class REGION, template <int N> class  DIST> 
void* 
arrayConstructionGlobalSM (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
                    ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  lapi_return_info_t *ret_info_ptr;
 
  ret_info_ptr =(lapi_return_info_t*)  msg_len;
  char* base_addr = (char*) ret_info_ptr->udata_one_pkt_ptr;

  metaDataDescr<RANK, REGION, DIST>* a = new metaDataDescr<RANK, REGION, DIST>;
  memcpy (a, base_addr, sizeof(metaDataDescr<RANK, REGION, DIST>));

  x10_place_t places[__x10_num_places];
  for (int i = 0; i < __x10_num_places; i++)
    places[i] = i;

  DIST<RANK>* d = new DIST<RANK> (&(a->region), places);
  uint64_t local_size = a->region.card() / __x10_num_places;

  makeArrayLocal<T, RANK, REGION, DIST> (d->region(), d);

  delete a;
  delete d;

  *comp_h = NULL;
  return NULL;
}
}

#endif

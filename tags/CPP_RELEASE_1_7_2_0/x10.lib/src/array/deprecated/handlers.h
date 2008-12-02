/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: handlers.h,v 1.1 2007-12-07 11:25:48 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */


#ifndef __HANDLERS_H__
#define __HANDLERS_H__

#include <lapi.h>
#include <x10/types.h>

#define MAX_RANK 3

namespace x10lib {
  
  template<class T, int RANK> class Array;

  template <typename T, int RANK, template <int N> class REGION, template <int N> class DIST>
    extern Array<T, RANK>*
    makeArrayLocal (const Region<RANK>* region, const Dist<RANK>* dist);

  template <int RANK, template <int N> class REGION, template <int N> class DIST>
    struct metaDataDescr
    {
      REGION<RANK> region;
      //for UNIQUE distribution this is sufficient
      //will add more as need arises
    };
  

    struct remoteArrayElementDescr
    {
      void* addr;
      int point[MAX_RANK];
    };
    
    template <typename T, int RANK>
    void copy_array_ (remoteArrayElementDescr& r, void* data, ulong len)
      {
	Point<RANK> p (r.point);
	Array<T, RANK>* a = (Array<T, RANK>*) (r.addr);
	assert (a);
	memcpy ((void*) &a->getLocalElementAt(p), data, len);
      }
    
      
    template<typename T, int RANK> 
      void* 
      remoteArrayCopy (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
		       ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
      {
	remoteArrayElementDescr r;
	memcpy (&r, uhdr, sizeof(remoteArrayElementDescr));
	lapi_return_info_t *ret_info =
	  (lapi_return_info_t *)msg_len;
	if (ret_info->udata_one_pkt_ptr) {
	  copy_array_<T, RANK> (r, ret_info->udata_one_pkt_ptr, *msg_len);
	  ret_info->ctl_flags = LAPI_BURY_MSG;
	  *comp_h = NULL;
	  return NULL;
	} else {
	  cout << "not implemented yet " << endl;
	  assert (false);
	}
	
	return NULL; 
      }
  
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
	
	//x10_place_t places[__x10_num_places];      
	//for (int i = 0; i < __x10_num_places; i++)
	//	places[i] = i;
	
	x10_place_t p = __x10_my_place;
	DIST<RANK>* d = new DIST<RANK> (&(a->region), &p);
	       	
	Array<T, RANK>* r = makeArrayLocal<T, RANK, REGION, DIST> (d->region(), d);
	
	delete a;
	delete d;
	
	*comp_h = NULL;
	return NULL;
      }
    
}



#endif

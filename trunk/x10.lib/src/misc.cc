#include <x10/misc.h>
#include <x10/xmacros.h>

using namespace x10lib;

static int copyCount = 0;

static int max_uhdr_sz;

static void* asyncArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  asyncArrayCopyClosure* header = (asyncArrayCopyClosure*) uhdr;	

  lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;

  //  cout << header->destOffset << " " << header->handle << endl;

   if (ret_info->udata_one_pkt_ptr) {
     memcpy ((char*) arrayCopySwitch (header) + header->destOffset, 
	     ret_info->udata_one_pkt_ptr, *msg_len);
     ret_info->ctl_flags = LAPI_BURY_MSG;
     *comp_h = NULL;
     return NULL;
   } else {	  
     ret_info->ret_flags = LAPI_LOCAL_STATE;
     *comp_h = NULL;
     return (char*) arrayCopySwitch (header) + header->destOffset;
   }
  
  return NULL; 
}

x10_err_t
miscInit ()
{
  (void) LAPI_Qenv(__x10_hndl, MAX_UHDR_SZ, &max_uhdr_sz);

  LRC (LAPI_Addr_set (__x10_hndl, (void*) asyncArrayCopyHandler, ASYNC_ARRAY_COPY_HANDLER)); 
}

namespace x10lib {

  //TODO: take care of clock operations
  x10_err_t
  asyncArrayCopyRaw (void* src, size_t srcOffset,
		  asyncArrayCopyClosure* closure, size_t closureSize, 
		  size_t len, int target, Clock* c)
  {          
    assert (closureSize >= 0 && closureSize < max_uhdr_sz);
    
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) closure,
		      closureSize,
		      (void*) ((char*) src + srcOffset),
		      len,
		      NULL,
		      NULL,
		      NULL));
    
    return X10_OK;
  }

  x10_err_t
  asyncArrayCopy (void* src, size_t srcOffset,
		  asyncArrayCopyClosure* closure, size_t closureSize, 
		  size_t len, int target, Clock* c)
  {          
    assert (closureSize >= 0 && closureSize < max_uhdr_sz);

    lapi_cntr_t origin_cntr;
    LRC (LAPI_Setcntr (__x10_hndl, &origin_cntr, 0));
    int tmp = -1;

    //cout << "here " << closure->handle << " " << closureSize << endl;
    
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) closure,
		      closureSize,
		      (void*) ((char*) src + srcOffset),
		      len,
		      NULL,
		      &origin_cntr,
		      NULL));

    LAPI_Waitcntr (__x10_hndl, &origin_cntr, 1, &tmp); 
   
    return X10_OK;
  }  
}

/**
  * TODO: remove root; make it "0" always.
  */
x10_err_t 
x10lib::Broadcast (void* buffer, size_t nbytes, x10_place_t root)
{  
  lapi_long_t remoteAddresses[__x10_num_places];
  
  LRC (LAPI_Address_init64 (__x10_hndl, (lapi_long_t) buffer, remoteAddresses));
  
  if (root == __x10_my_place)
    {
      for (x10_place_t p = 0; p < __x10_num_places; p++) 
	{
	  if (p == __x10_my_place) continue;
	  LAPI_Put (__x10_hndl,
		    p,
		    nbytes,
		    (void*) remoteAddresses[p],
		    buffer,
		    NULL,
		    NULL,
		    NULL);
	  
	}
    }

  //TODO: replace this with a counter
  LAPI_Gfence (__x10_hndl);

  return X10_OK;
}

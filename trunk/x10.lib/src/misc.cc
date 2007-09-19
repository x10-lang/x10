#include <x10/misc.h>
#include <x10/xmacros.h>

using namespace x10lib;


/* extern "C"
void*
arrayCopySwitch (x10_async_handler_t h, void* args)
{
  cout << "arrayCopySwitch should be overriddern \n";
  exit(-1);
}*/

#define RING_SIZE 10

int copyCount = 0;

struct asyncArrayCopyHeader
{
  x10_async_handler_t handler;
  size_t destOffset;
  char args[100];
};

static void* asyncArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  asyncArrayCopyHeader header = *((asyncArrayCopyHeader*) uhdr);	
  lapi_return_info_t *ret_info =
    (lapi_return_info_t *)msg_len;
  if (ret_info->udata_one_pkt_ptr) {
    memcpy ((char*) arrayCopySwitch (header.handler, (void*) header.args) + header.destOffset, ret_info->udata_one_pkt_ptr, *msg_len);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    return NULL;
  } else {	  
    *comp_h = NULL;
    return (char*) arrayCopySwitch (header.handler, (void*) header.args) + header.destOffset;
  }
  
  return NULL; 
}

namespace x10lib {

  //TODO: take care of clock operations
  x10_err_t
  asyncArrayCopy (void* src, size_t srcOffset,
		  x10_async_handler_t handler,
		  void* args, size_t arg_size, 
		  size_t destOffset, size_t len, int target, Clock* c)
  {
    LRC (LAPI_Addr_set (__x10_hndl, (void*) asyncArrayCopyHandler, ASYNC_ARRAY_COPY_HANDLER));
    
    int max_uhdr_sz;
    (void) LAPI_Qenv(__x10_hndl, MAX_UHDR_SZ, &max_uhdr_sz);
    
    assert (arg_size >= 0 && arg_size < max_uhdr_sz);
   
    LAPI_Waitcntr (__x10_hndl, &(origin_cntr[copyCount]), 1,&tmp); 

    header[copyCount].handler = handler;
    header[copyCount].destOffset = destOffset;
    memcpy (header[copyCount].args, args,arg_size);
    
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) &(header[copyCount]),
		      sizeof (header[copyCount]),
		      (void*) ((char*) src + srcOffset),
		      len,
		      NULL,
		      &(origin_cntr[copyCount]),
		      NULL));
    
    copyCount = (copyCount + 1) % RING_SIZE;
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

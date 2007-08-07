#include <x10/misc.h>
#include <x10/xmacros.h>

using namespace x10lib;


/*
x10_err_t 
x10lib::asyncArrayCopy (void* src, int srcOffset, void* dst, int dstOffset,
		     int target, int len, x10_switch_t swch)
{    
  int tmp;
  lapi_cntr_t origin_cntr;
  LRC(LAPI_Setcntr(__x10_hndl, &origin_cntr, 0));
  LAPI_Put (__x10_hndl,
	    target,
	    len, 
	    (char*) dst + dstOffset,
	    (char*) src + srcOffset, 
	    NULL,
	    &origin_cntr,
	    NULL); //swch->get_handle());
  LRC(LAPI_Waitcntr(__x10_hndl, &origin_cntr, 1, &tmp));
  
}*/

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
  
  LAPI_Gfence (__x10_hndl);

  return X10_OK;
}

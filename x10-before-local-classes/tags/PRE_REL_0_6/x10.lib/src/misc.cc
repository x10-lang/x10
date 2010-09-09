#include <x10/misc.h>
#include <x10/xmacros.h>

x10_err_t 
x10lib::Broadcast (void* buffer, size_t nbytes)
{  
  lapi_long_t remoteAddresses[__x10_num_places];
  
  LRC (LAPI_Address_init64 (__x10_hndl, (lapi_long_t) buffer, remoteAddresses));
  
  if (__x10_my_place == 0)
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


extern "C"
x10_err_t
x10_broadcast (void* buffer, size_t nbytes)
{
  return x10lib::Broadcast (buffer, nbytes);
}

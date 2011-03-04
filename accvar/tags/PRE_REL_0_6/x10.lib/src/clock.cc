#include "clock.h"
#include <x10/xmacros.h>

x10_err_t
x10lib::clockNext (Clock* c, int n) 
{  

  LRC (LAPI_Gfence (__x10_hndl));

  return X10_OK;
}


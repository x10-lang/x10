#include <x10/switch.h>
#include <x10/xmacros.h>
#include <x10/gas.h>
#include <iostream>

using namespace std;
using namespace x10lib;


error_t
x10lib::switchInit (switch_t* s, int val)
{
  LAPI_Setcntr (GetHandle(), s, val);
  return X10_OK;
}

error_t
x10lib::switchNext (switch_t* s)
{
  X10_DEBUG (1, "Entering Switch Next\n");
  int tmp;   
  LRC (LAPI_Waitcntr (GetHandle(), s, 0, &tmp));
  X10_DEBUG (1, "Exiting Swith Next\n");
  return X10_OK;
}

error_t
x10lib::switchAddVal (switch_t* s, int val)
{
  int tmp; 
  LAPI_Getcntr (GetHandle(), s, &tmp);
  LAPI_Setcntr (GetHandle(), s, tmp + val);  
  return X10_OK;
}


extern "C"
error_t
x10_switch_init (switch_t* s, int val)
{
  return x10lib::switchInit (s, val);
}

extern "C"
error_t
x10_switch_next (switch_t* s)
{
  return x10lib::switchNext (s);
}

extern "C"
error_t
x10_switch_add_val (switch_t* s, int val)
{
  return x10lib::switchAddVal (s, val);
}

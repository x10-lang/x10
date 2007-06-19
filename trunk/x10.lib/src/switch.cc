#include "switch.h"

using namespace x10lib;
using namespace std;


void
x10lib::initSwitch (switch_t* switch)
{
  LRC (LAPI_Setcntr (GetHandle(), switch, 0));
}

error_t
x10lib::nextOnSwitch (switch_t* switch)
{
  int tmp;   
  LRC (LAPI_Waitcntr (GetHandle(), switch, 0, &tmp));
  return X10_OK;
}

error_t
x10lib::modifySwitch (switch_t* switch, int val)
{
  int tmp; 
  LAPI_Getcntr (GetHandle(), switch, &tmp);
  LAP_Setcntr (GetHandle(), switch, tmp + val); 
}

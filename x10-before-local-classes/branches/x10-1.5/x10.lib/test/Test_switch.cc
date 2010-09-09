#include <x10/x10lib.h>
#include <iostream>

using namespace x10lib;
using namespace std;

int cntr;

void* headerHandler (lapi_handle_t* hndl, void* uhdr, int* ulen,
		     ulong* mlen, compl_hndlr_t** comp, void** uinfo)
{
  cntr++;
  lapi_return_info_t *ret_info = (lapi_return_info_t*) mlen;
  ret_info->ctl_flags = LAPI_BURY_MSG;
  *comp = NULL;
  return NULL;
}

int main (int argc, char** argv)
{
  Init (NULL, 0);
  cntr = 0;
  LAPI_Addr_set (GetHandle(), (void*) headerHandler, X10_INVALID_HANDLER + 1);
  if (here() == 0) {
    x10_switch_t s  = AllocSwitch(); 
    for (int i = 0; i < numPlaces(); i++) {
      if (i == here())
	  {
	    cntr++;
	    continue;
	  }
      s->decrement(); 
      LAPI_Amsend (GetHandle(),
		   i,
		   (void*) (X10_INVALID_HANDLER + 1),
		   NULL,
		   0,
		   NULL,
		   0,
		   NULL,
		   NULL,
		   (lapi_cntr_t*) s->get_handle());
    }

    s->next();
    FreeSwitch (s);
  }
  
  SyncGlobal();
  assert (cntr == 1);    

  cout << "Test_switch PASSED" << endl;
 
  Finalize(); 
  return 0;
}

#include <x10/x10lib.h>
#include <iostream>

using namespace x10lib;
using namespace std;

int cntr;

void* headerHandler (lapi_handle_t* hndl, void* uhdr, int* ulen,
		     ulong* mlen, compl_hndlr_t** comp, void** uinfo)
{
  cntr++;
  *comp = NULL;
  return NULL;
}

int main (int argc, char** argv)
{
  cntr = 0;
  LAPI_Addr_set (GetHandle(), (void*) headerHandler, 10);
  if (here() == 0) {
    switch_t* s = new switch_t;
    switchInit (s, 0);
    for (int i = 0; i < numPlaces(); i++) {
      if (i == here())
	  {
	    cntr++;
	    continue;
	  }
      switchAddVal (s, -1);
      LAPI_Amsend (GetHandle(),
		   i,
		   (void*) 10,
		   NULL,
		   0,
		   NULL,
		   0,
		   NULL,
		   NULL,
		   s);
    }

    switchNext (s);
    delete s;
  }
  
  Gfence();
  assert (cntr == 1);    

  cout << "Test_switch PASSED" << endl;
  
  return 0;
}

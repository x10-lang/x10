#include <x10/x10lib.h>
#include <stdio.h>

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
  x10_init(NULL, 0);
  cntr = 0;
  LAPI_Addr_set (x10_get_handle(), (void*) headerHandler, X10_INVALID_HANDLER + 1);
  if (x10_here() == 0) {
    x10_switch_t s = x10_alloc_switch();
    for (int i = 0; i < x10_num_places(); i++) {
      if (i == x10_here())
	  {
	    cntr++;
	    continue;
	  }
      x10_decrement_switch(s);
      LAPI_Amsend (x10_get_handle(),
		   i,
		   (void*) (X10_INVALID_HANDLER + 1),
		   NULL,
		   0,
		   NULL,
		   0,
		   NULL,
		   NULL,
		   (lapi_cntr_t*) x10_switch_get_handle(s));
    }

    x10_next_on_switch (s);
    x10_free_switch (s);
  }
  
  x10_sync_global();
  assert (cntr == 1);    

  printf ("Test_switch_c PASSED\n"); 
  
  x10_finalize();
  return 0;
}
